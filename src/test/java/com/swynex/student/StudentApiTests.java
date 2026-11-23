package com.swynex.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentApiTests {

    @Autowired
    private MockMvc mvc;

    private static final String VALID =
            "{\"name\":\"Madhuri\",\"email\":\"madhuri@example.com\",\"age\":21,\"course\":\"CS\"}";

    @Test
    void postWithoutApiKeyReturns401() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid or missing API key"));
    }

    @Test
    void postWithWrongApiKeyReturns401() throws Exception {
        mvc.perform(post("/api/students").header("X-API-KEY", "wrong")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postInvalidPayloadReturns400() throws Exception {
        mvc.perform(post("/api/students").header("X-API-KEY", "change-me-locally")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\",\"age\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.email").exists());
    }

    @Test
    void postValidPayloadReturns201() throws Exception {
        mvc.perform(post("/api/students").header("X-API-KEY", "change-me-locally")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\",\"email\":\"test.user@example.com\",\"age\":20}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getIsPublicAndMissingStudentReturns404() throws Exception {
        mvc.perform(get("/api/students")).andExpect(status().isOk());
        mvc.perform(get("/api/students/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
