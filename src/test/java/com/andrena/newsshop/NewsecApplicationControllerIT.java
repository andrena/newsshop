package com.andrena.newsshop;

import com.andrena.newsshop.model.Newsletter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NewsecApplicationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldNeedLoggedInUserToGetAllNewsletters() throws Exception {
        mockMvc.perform(get("/api/newsletter/subscribers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldReturnNewsletters() throws Exception {
        List<Newsletter> newsletters = getNewsletters();
        assertThat(newsletters.size()).isGreaterThan(0);
    }

    private List<Newsletter> getNewsletters() throws Exception {
        MvcResult resultActions = mockMvc.perform(get("/api/newsletter/subscribers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>() {});
    }

    @Test
    @WithMockUser
    void shouldSubscribeToNewsletter() throws Exception {
        Newsletter newsletter = Newsletter.builder()
                .withEmail("email1@email.com")
                .withName("name1")
                .withSource("source1")
                .build();

        post(newsletter)
                .andExpect(status().isOk());

        List<Newsletter> newsletters = getNewsletters();
        assertThat(newsletters)
                .extracting("name", "email", "source")
                .contains(tuple("name1", "email1@email.com", "source1"));
    }

    private ResultActions post(Newsletter newsletter) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/newsletter/subscribe")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsletter)));
    }

    @Test
    void shouldNotBeAbleToRegisterSameEmailTwice() throws Exception {
        Newsletter newsletter = Newsletter.builder()
                .withEmail("email2@email.com")
                .withName("name2")
                .withSource("source2")
                .build();

        post(newsletter)
                .andExpect(status().isOk());
        post(newsletter)
                .andExpect(status().isConflict())
                .andExpect(content().string("Ein Newsletter-Abonnement für die E-Mail-Adresse 'email2@email.com' existiert bereits (Name: name2)"));
    }

    @Test
    @WithMockUser
    void shouldBeAbleToSearch() throws Exception {
        List<Newsletter> foundNewsletters = searchNewsletter("jo");
        assertThat(foundNewsletters)
                .extracting("name", "email")
                .containsExactly(
                        tuple("John Doe", "john.doe@example.com"),
                        tuple("Alice Jones", "alice.jones@example.com")
                );
    }

    @Test
    @WithMockUser
    void shouldBeAbleToDeleteNewsletter() throws Exception {
        Newsletter newsletter = Newsletter.builder()
                .withEmail("email1@email.com")
                .withName("name1")
                .withSource("source1")
                .build();

        post(newsletter);

        Long id = searchNewsletter("name1").getFirst().getId();
        assertThat(id).isNotNull();
        mockMvc.perform(delete("/api/newsletter/unsubscribe/" + id)).andExpect(status().isNoContent());

        List<Newsletter> foundNewsletters = searchNewsletter("name1");
        assertThat(foundNewsletters).hasSize(0);
    }

    private List<Newsletter> searchNewsletter(String searchText) throws Exception {
        MvcResult resultActions = mockMvc.perform(get("/api/newsletter/search").param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>() {});
    }

}
