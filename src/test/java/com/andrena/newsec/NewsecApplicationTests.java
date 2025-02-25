package com.andrena.newsec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NewsecApplicationTests {

    
    @SuppressWarnings("unused")
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

}
