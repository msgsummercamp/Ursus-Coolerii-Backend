package com.example.airassist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = CaseFileController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.jwt\\..*"))
public class CaseFileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void isEligibleForCancellation_WithLessThan14Days_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/cancellation")
                        .param("noticeDays", "13"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void isEligibleForCancellation_With14Days_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/cancellation")
                        .param("noticeDays", "14"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void isEligibleForDelay_ArrivedWithMoreThan3Hours_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/delay")
                        .param("delayHours", "4")
                        .param("arrived", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void isEligibleForDelay_ArrivedWith3Hours_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/delay")
                        .param("delayHours", "3")
                        .param("arrived", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void isEligibleForDeniedBoarding_NotVoluntarilyGivenUp_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/denied-boarding")
                        .param("isVoluntarilyGivenUp", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void isEligibleForDeniedBoarding_VoluntarilyGivenUp_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(
                post("/api/case-file/eligibility/denied-boarding")
                        .param("isVoluntarilyGivenUp", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
