package com.andrena.newsshop.controller;


import com.andrena.newsshop.config.SecurityConfig;
import com.andrena.newsshop.repository.NewsletterRepository;
import com.andrena.newsshop.user.CustomUserDetailsService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class NewsletterControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @MockitoBean
    private NewsletterRepository newsletterRepository;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "a@", "a@b." })
    @Disabled()
    void validateEmailFails(String email) throws Exception {
        String body = createBody(email, "sdf", "Friend");
        evaluateNewsletterForm(body, status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@b.de" })
    @Disabled()
    void validateEmailSuccess(String email) throws Exception {
        String body = createBody(email, "sdf", "Friend");
        evaluateNewsletterForm(body, status().is2xxSuccessful());
    }

    @ParameterizedTest
    @MethodSource("badNameStrings")
    @Disabled()
    void validateNameFails(String name) throws Exception {
        String body = createBody("a@b.de", name, "Friend");
        evaluateNewsletterForm(body, status().is4xxClientError());
    }

    static Stream<String> badNameStrings() {
        return Stream.of(
                "a".repeat(51),
                "",
                "O'Connor"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"John Doe" })
    @Disabled()
    void validateNameSuccess(String name) throws Exception {
        String body = createBody("a@b.de", name, "Friend");
        ResultMatcher matcher = status().is2xxSuccessful();
        evaluateNewsletterForm(body, matcher);
    }

    private void evaluateNewsletterForm(String body, ResultMatcher matcher) throws Exception {
        mockmvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(matcher);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Social Media", "Friend", "Website", "Other"})
    @Disabled()
    void validateSourceSuccess(String source) throws Exception {
        String body = createBody("a@b.de", "name", source);
        evaluateNewsletterForm(body, status().is2xxSuccessful());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "etwasAnderes"})
    @Disabled()
    void validateSourceFail(String source) throws Exception {
        String body = createBody("a@b.de", "name", source);
        evaluateNewsletterForm(body, status().is4xxClientError());
    }

    private static String createBody(String email, String name, String source) {
        return "{\"email\":\""+ email + "\",\"name\":\"" + name + "\",\"source\":\"" + source + "\", \"otherSource\":\"notthing\"}";
    }
}