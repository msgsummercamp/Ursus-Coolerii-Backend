package com.example.airassist.service;

import com.example.airassist.common.dto.CaseRequest;
import com.example.airassist.common.dto.FlightSaveDTO;
import com.example.airassist.common.enums.DaysBeforeNotice;
import com.example.airassist.common.enums.Disruption;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.common.enums.HoursBeforeArrival;
import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.dao.CaseFlightRepository;
import com.example.airassist.persistence.dao.PassengerRepository;
import com.example.airassist.persistence.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("CaseFileServiceImpl Tests")
class CaseFileServiceImplTest {

    @Mock
    private CaseFileService caseFileService;

    @Mock
    private UserService userService;

    @Mock
    private CaseFileRepository caseFileRepository;

    @Mock
    private AirportService airportService;

    @Mock
    private AirlineService airlineService;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private CaseFlightRepository caseFlightRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private MailSenderService mailSenderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caseFileService = new CaseFileServiceImpl(caseFileRepository,
                airportService,
                userService,
                airlineService,
                passengerRepository,
                caseFlightRepository,
                flightService,
                mailSenderService);
    }

    @Nested
    @DisplayName("Cancellation Eligibility Tests")
    class CancellationEligibilityTests {

        @Test
        @DisplayName("Should return true when notice days is less than 14")
        void isEligible_CancellationWithLessThan14Days_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.LESS_THAN_14);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when notice days is exactly 14")
        void isEligible_CancellationWith14Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.MORE_THAN_14);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when notice days is more than 14")
        void isEligible_CancellationWithMoreThan14Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.MORE_THAN_14);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when notice days is zero")
        void isEligible_CancellationWithZeroDays_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.ON_FLIGHT_DAY);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when notice days is negative")
        void isEligible_CancellationWithNegativeDays_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.ON_FLIGHT_DAY);

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
            request.setDelayHours(HoursBeforeArrival.MORE_THAN_3);
            request.setArrived(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when passenger arrived and delay is more than 3 hours")
        void isEligible_DelayArrivedWithMoreThan3Hours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.MORE_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when passenger arrived and delay is exactly 3 hours")
        void isEligible_DelayArrivedWith3Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LESS_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when passenger arrived and delay is less than 3 hours")
        void isEligible_DelayArrivedWithLessThan3Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LESS_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true when not arrived with zero delay hours")
        void isEligible_DelayNotArrivedWithZeroHours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.MORE_THAN_3);
            request.setArrived(false);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when arrived with zero delay hours")
        void isEligible_DelayArrivedWithZeroHours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LESS_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle negative delay hours correctly for arrived passenger")
        void isEligible_DelayArrivedWithNegativeHours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LESS_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle negative delay hours correctly for not arrived passenger")
        void isEligible_DelayNotArrivedWithNegativeHours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LOST_CONNECTION);
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
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should test cancellation boundary at 13 days")
        void isEligible_CancellationBoundary13Days_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.LESS_THAN_14);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should test cancellation boundary at 15 days")
        void isEligible_CancellationBoundary15Days_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(DaysBeforeNotice.MORE_THAN_14);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should test delay boundary at 2 hours for arrived passenger")
        void isEligible_DelayBoundary2Hours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.LESS_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should test delay boundary at 4 hours for arrived passenger")
        void isEligible_DelayBoundary4Hours_ShouldReturnTrue() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(HoursBeforeArrival.MORE_THAN_3);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Null Input Handling Service Tests")
    class NullInputHandlingTests {

        @Test
        @DisplayName("Should return false when disruption is null")
        void isEligible_NullDisruption_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(null);

            assertFalse(caseFileService.isEligible(request));
        }

        @Test
        @DisplayName("Should return false for cancellation with null noticeDays")
        void isEligible_CancellationNullNoticeDays_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.CANCELLATION);
            request.setNoticeDays(null);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for delay with null delayHours")
        void isEligible_DelayNullDelayHours_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(null);
            request.setArrived(true);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for denied boarding with null voluntarily given up")
        void isEligible_DeniedBoardingNullVoluntarilyGivenUp_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DENIED_BOARDING);
            request.setIsVoluntarilyGivenUp(null);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for delay with both null delayHours and arrived")
        void isEligible_DelayBothNull_ShouldReturnFalse() {
            EligibilityRequest request = new EligibilityRequest();
            request.setDisruption(Disruption.DELAY);
            request.setDelayHours(null);
            request.setArrived(null);

            Boolean result = caseFileService.isEligible(request);

            assertFalse(result);
        }

    }

    @Nested
    @DisplayName("Save case files tests")
    class SaveCaseTests{

        @Test
        @DisplayName("Should save case file with flights and return the saved entity")
        void saveCaseFile_shouldSaveAndReturnCaseFile() {
            User user = new User();
            user.setEmail("mock@gmail.com");
            Passenger passenger = new Passenger();
            passenger.setFirstName("Mock");
            CaseRequest saveRequest = new CaseRequest();
            saveRequest.setUserEmail("mock@gmail.com");
            saveRequest.setPassenger(passenger);
            saveRequest.setReservationNumber("ABC123");
            FlightSaveDTO flightDTO = new FlightSaveDTO();
            flightDTO.setFlightNumber("FL123");
            flightDTO.setDepartureAirport("Cluj");
            flightDTO.setDestinationAirport("Bucuresti");
            flightDTO.setAirlineName("Wizz");
            flightDTO.setDepartureTime(null);
            flightDTO.setArrivalTime(null);
            flightDTO.setProblemFlight(false);
            flightDTO.setFirstFlight(true);
            flightDTO.setLastFlight(true);
            saveRequest.setFlights(List.of(flightDTO));

            Flight flight = Flight.builder()
                    .flightNumber("FL123")
                    .departureAirport("Cluj")
                    .destinationAirport("Bucuresti")
                    .airline(null)
                    .departureTime(null)
                    .arrivalTime(null)
                    .build();

            CaseFile caseFile = CaseFile.builder()
                    .passenger(passenger)
                    .reservationNumber("ABC123")
                    .user(user)
                    .status(com.example.airassist.common.enums.CaseStatus.NOT_ASSIGNED)
                    .caseId(UUID.randomUUID())
                    .build();

            CaseFlights caseFlights = CaseFlights.builder()
                    .caseFile(caseFile)
                    .flight(flight)
                    .isFirst(true)
                    .isLast(true)
                    .isProblemFlight(false)
                    .build();

            when(userService.findByEmail("mock@gmail.com")).thenReturn(Optional.of(user));
            when(passengerRepository.save(passenger)).thenReturn(passenger);
            when(caseFileRepository.save(any(CaseFile.class))).thenReturn(caseFile);
            when(flightService.saveAll(anyList())).thenReturn(List.of(flight));
            when(caseFlightRepository.saveAll(anyList())).thenReturn(List.of(caseFlights));
            CaseFile result = caseFileService.saveCase(saveRequest, List.of());

            assertNotNull(result);
            assertEquals(passenger, result.getPassenger());
            assertEquals(user, result.getUser());
            assertEquals("ABC123", result.getReservationNumber());
            assertEquals(com.example.airassist.common.enums.CaseStatus.NOT_ASSIGNED, result.getStatus());
        }

    }
}
