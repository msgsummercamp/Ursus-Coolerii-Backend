package com.example.airassist.controller;

import com.example.airassist.dto.EligibilityRequest;
import com.example.airassist.common.Disruption;
import com.example.airassist.service.CaseFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CaseFileController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    })
@DisplayName("CaseFileController Eligibility Endpoint Tests")
public class CaseFileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseFileService caseFileService;

    @Nested
    @DisplayName("Cancellation Eligibility Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should return true for cancellation with less than 14 days notice")
        public void testCancellationEligible_LessThan14Days() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(13);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false for cancellation with exactly 14 days notice")
        public void testCancellationNotEligible_Exactly14Days() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(14);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should return false for cancellation with more than 14 days notice")
        public void testCancellationNotEligible_MoreThan14Days() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(15);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should return true for cancellation with zero days notice")
        public void testCancellationEligible_ZeroDays() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(0);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }
    }

    @Nested
    @DisplayName("Delay Eligibility Tests")
    class DelayTests {

        @Test
        @DisplayName("Should return true for delay when passenger not arrived")
        public void testDelayEligible_NotArrived() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(false);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return true for delay when arrived with more than 3 hours delay")
        public void testDelayEligible_ArrivedMoreThan3Hours() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(4);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false for delay when arrived with exactly 3 hours delay")
        public void testDelayNotEligible_ArrivedExactly3Hours() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(3);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should return false for delay when arrived with less than 3 hours delay")
        public void testDelayNotEligible_ArrivedLessThan3Hours() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }

    @Nested
    @DisplayName("Denied Boarding Eligibility Tests")
    class DeniedBoardingTests {

        @Test
        @DisplayName("Should return true for denied boarding when not voluntarily given up")
        public void testDeniedBoardingEligible_NotVoluntary() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(false);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false for denied boarding when voluntarily given up")
        public void testDeniedBoardingNotEligible_Voluntary() throws Exception {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            mockMvc.perform(post("/api/case-files/eligibility")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }
}
