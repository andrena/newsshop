package com.andrena.newsshop;

import com.andrena.newsshop.model.Newsletter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ValidationTest {


    private Newsletter createInvalidNewsletter() {
        Newsletter newsletter = new Newsletter();
        newsletter.setEmail("Käsebrot"+randomString()); // invalid email e.g. has no @ symbol
        newsletter.setName(randomString());
        newsletter.setSource(randomString());
        newsletter.setMailProperties("Nothing");
        return newsletter;
    }
    private String randomString(){
        return (Math.random() + "").replace(".","");
    }

    @Test
    void testCreateSubscriptionWithInvalidEmailAddress() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/api/newsletter/subscribe";

        try{
            Newsletter newSubscription = createInvalidNewsletter();
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl,newSubscription, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
