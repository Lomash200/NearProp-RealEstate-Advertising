package com.nearprop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSafetyStandardsApi() throws Exception {
        mockMvc.perform(get("/api/safety-standards")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("NearProp Child Safety and CSAE Standards")));
    }

    @Test
    public void testSafetyStandardsApiJson() throws Exception {
        mockMvc.perform(get("/api/safety-standards")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testMockReportGenerationForFranchisee() throws Exception {
        mockMvc.perform(post("/api/mock-reports/franchisee/1")
                .param("year", "2023")
                .param("month", "7")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "FRANCHISEE")
    public void testFranchiseeDashboard() throws Exception {
        mockMvc.perform(get("/api/franchisee/dashboard")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }
} 