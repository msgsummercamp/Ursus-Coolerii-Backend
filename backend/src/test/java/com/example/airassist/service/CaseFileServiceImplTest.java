package com.example.airassist.service;

import com.example.airassist.common.Disruption;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.persistence.dao.CaseFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CaseFileServiceImpl Tests")
class CaseFileServiceImplTest {

    private CaseFileServiceImpl caseFileService;

    @BeforeEach
    void setUp() {
        caseFileService = new CaseFileServiceImpl(Mockito.mock(CaseFileRepository.class), Mockito.mock(AirportService.class));
    }

    @Nested
    @DisplayName("Cancellation Eligibility Tests")
    class CancellationEligibilityTests {

        @Test
        @DisplayName("Should return true when notice days is less than 14")
        void isEligible_CancellationWithLessThan14Days_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(13);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when notice days is exactly 14")
        void isEligible_CancellationWith14Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(14);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when notice days is more than 14")
        void isEligible_CancellationWithMoreThan14Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(15);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when notice days is zero")
        void isEligible_CancellationWithZeroDays_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(0);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when notice days is negative")
        void isEligible_CancellationWithNegativeDays_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(-1);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Delay Eligibility Tests")
    class DelayEligibilityTests {

        @Test
        @DisplayName("Should return true when passenger has not arrived regardless of delay hours")
        void isEligible_DelayNotArrived_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(1);
            request.setArrived(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when passenger arrived and delay is more than 3 hours")
        void isEligible_DelayArrivedWithMoreThan3Hours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(4);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when passenger arrived and delay is exactly 3 hours")
        void isEligible_DelayArrivedWith3Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when passenger arrived and delay is less than 3 hours")
        void isEligible_DelayArrivedWithLessThan3Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when not arrived with zero delay hours")
        void isEligible_DelayNotArrivedWithZeroHours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(0);
            request.setArrived(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when arrived with zero delay hours")
        void isEligible_DelayArrivedWithZeroHours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(0);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle negative delay hours correctly for arrived passenger")
        void isEligible_DelayArrivedWithNegativeHours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(-1);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle negative delay hours correctly for not arrived passenger")
        void isEligible_DelayNotArrivedWithNegativeHours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(-1);
            request.setArrived(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Denied Boarding Eligibility Tests")
    class DeniedBoardingEligibilityTests {

        @Test
        @DisplayName("Should return true when passenger did not voluntarily give up seat")
        void isEligible_DeniedBoardingNotVoluntarilyGivenUp_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when passenger voluntarily gave up seat")
        void isEligible_DeniedBoardingVoluntarilyGivenUp_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesTests {


        @Test
        @DisplayName("Should handle null noticeDays for cancellation")
        void isEligible_CancellationWithNullNoticeDays_ShouldThrowException() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(null);

            assertThrows(NullPointerException.class, () -> caseFileService.isEligible(request));
        }

        @Test
        @DisplayName("Should handle null arrived status for delay")
        void isEligible_DelayWithNullArrived_ShouldThrowException() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(5);
            request.setArrived(null);

            assertThrows(NullPointerException.class, () -> caseFileService.isEligible(request));
        }

        @Test
        @DisplayName("Should handle null delayHours for delay")
        void isEligible_DelayWithNullDelayHours_ShouldThrowException() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(null);
            request.setArrived(true);

            assertThrows(NullPointerException.class, () -> caseFileService.isEligible(request));
        }

        @Test
        @DisplayName("Should handle null voluntarily given up for denied boarding")
        void isEligible_DeniedBoardingWithNullVoluntarilyGivenUp_ShouldThrowException() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(null);

            assertThrows(NullPointerException.class, () -> caseFileService.isEligible(request));
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should test cancellation boundary at 13 days")
        void isEligible_CancellationBoundary13Days_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(13);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should test cancellation boundary at 15 days")
        void isEligible_CancellationBoundary15Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(15);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should test delay boundary at 2 hours for arrived passenger")
        void isEligible_DelayBoundary2Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(2);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should test delay boundary at 4 hours for arrived passenger")
        void isEligible_DelayBoundary4Hours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(4);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }
    }
}
