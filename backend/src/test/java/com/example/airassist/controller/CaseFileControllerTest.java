package com.example.airassist.controller;

import com.example.airassist.common.Disruption;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.service.CaseFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("CaseFileController Direct Tests")
public class CaseFileControllerTest {

    @Mock
    private CaseFileService caseFileService;

    private CaseFileController caseFileController;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            caseFileController = new CaseFileController(caseFileService);
        }
    }

    @Nested
    @DisplayName("Cancellation Eligibility Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should return true for cancellation with less than 14 days notice")
        public void testCancellationEligible_LessThan14Days() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(13);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return false for cancellation with exactly 14 days notice")
        public void testCancellationNotEligible_Exactly14Days() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(14);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return false for cancellation with more than 14 days notice")
        public void testCancellationNotEligible_MoreThan14Days() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(15);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return true for cancellation with zero days notice")
        public void testCancellationEligible_ZeroDays() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(0);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Boolean.TRUE, response.getBody());
        }
    }

    @Nested
    @DisplayName("Delay Eligibility Tests")
    class DelayTests {

        @Test
        @DisplayName("Should return true for delay when passenger not arrived")
        public void testDelayEligible_NotArrived() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(false);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return true for delay when arrived with more than 3 hours delay")
        public void testDelayEligible_ArrivedMoreThan3Hours() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(4);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return false for delay when arrived with exactly 3 hours delay")
        public void testDelayNotEligible_ArrivedExactly3Hours() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(3);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return false for delay when arrived with less than 3 hours delay")
        public void testDelayNotEligible_ArrivedLessThan3Hours() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotEquals(Boolean.TRUE, response.getBody());
        }
    }

    @Nested
    @DisplayName("Denied Boarding Eligibility Tests")
    class DeniedBoardingTests {

        @Test
        @DisplayName("Should return true for denied boarding when not voluntarily given up")
        public void testDeniedBoardingEligible_NotVoluntary() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(false);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(true);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Boolean.TRUE, response.getBody());
        }

        @Test
        @DisplayName("Should return false for denied boarding when voluntarily given up")
        public void testDeniedBoardingNotEligible_Voluntary() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(true);

            when(caseFileService.isEligible(any(EligibilityRequest.class))).thenReturn(false);

            ResponseEntity<Boolean> response = caseFileController.isEligible(request);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotEquals(Boolean.TRUE, response.getBody());
        }
    }
}
