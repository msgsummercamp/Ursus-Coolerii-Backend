import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.model.*;
import com.example.airassist.service.CaseFileServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CaseFileTest {
    private CaseFile caseFile;
    private User user;
    private Passenger passenger;
    private User employee;
    private Flight flightA;
    private Flight flightB;
    private CaseFlights cf1;
    private CaseFlights cf2;

    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);
        passenger = Mockito.mock(Passenger.class);
        employee = Mockito.mock(User.class);

        flightA = Mockito.mock(Flight.class);
        flightB = Mockito.mock(Flight.class);

        cf1 = Mockito.mock(CaseFlights.class);
        Mockito.when(cf1.isFirst()).thenReturn(true);
        Mockito.when(cf1.isLast()).thenReturn(false);
        Mockito.when(flightA.getDepartureAirport()).thenReturn("JFK");
        Mockito.when(flightA.getDestinationAirport()).thenReturn("LHR");
        Mockito.when(cf1.getFlight()).thenReturn(flightA);

        cf2 = Mockito.mock(CaseFlights.class);
        Mockito.when(cf2.isFirst()).thenReturn(false);
        Mockito.when(cf2.isLast()).thenReturn(true);
        Mockito.when(flightB.getDepartureAirport()).thenReturn("LHR");
        Mockito.when(flightB.getDestinationAirport()).thenReturn("DXB");
        Mockito.when(cf2.getFlight()).thenReturn(flightB);

        caseFile = new CaseFile();
        caseFile.setReservationNumber("RES123");
        caseFile.setUser(user);
        caseFile.setPassenger(passenger);
        caseFile.setEmployee(employee);
        caseFile.setCaseFlights(Arrays.asList(cf1, cf2));
    }

    @Test
    void testFindFirstAndLastFlight() {
        CaseFlights firstFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isFirst)
                .findFirst()
                .orElse(null);

        CaseFlights lastFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isLast)
                .findFirst()
                .orElse(null);

        assertNotNull(firstFlight);
        assertTrue(firstFlight.isFirst());
        assertEquals(flightA, firstFlight.getFlight());

        assertNotNull(lastFlight);
        assertTrue(lastFlight.isLast());
        assertEquals(flightB, lastFlight.getFlight());
    }

    @Test
    void testCalculateDistanceAndReward() {
        CaseFileServiceImpl service = new CaseFileServiceImpl(null);

        CaseFlights firstFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isFirst)
                .findFirst()
                .orElse(null);
        CaseFlights lastFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isLast)
                .findFirst()
                .orElse(null);

        assertNotNull(firstFlight);
        assertNotNull(lastFlight);

        String departureAirport = firstFlight.getFlight().getDepartureAirport();
        String destinationAirport = lastFlight.getFlight().getDestinationAirport();

        double distance = service.calculateDistance(departureAirport, destinationAirport);
        assertTrue(distance > 0);

        int reward = service.calculateCaseReward(caseFile);
        assertTrue(reward > 0);
    }

    @AfterEach
    void tearDown() {
        caseFile = null;
        user = null;
        passenger = null;
        employee = null;
        flightA = null;
        flightB = null;
        cf1 = null;
        cf2 = null;
    }
}