package com.dilaverdemirel.simplespringbootk8sapp.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dilaverd - 12.11.2019
 */
@WebMvcTest(SalutationController.class)
public class SalutationControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    void testHello_SendName_ReturnSalutationMessage() throws Exception {
        final String name = "UnitTest";
        mockMvc.perform(get("/hello/" + name)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(String.format("Hello %s!", name))));
    }
}