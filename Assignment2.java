import java.util.*;
public class Assignment2 {
    public static void main(String[] args) {
        System.out.println("=== GLOBAL AIRPORT MANAGEMENT SYSTEM INITIALIZED ===\n");

        AirTrafficControl atc = new AirTrafficControl();

        // 1. Create Gates
        TerminalGate<CommercialFlight> gateA1 = new TerminalGate<>("A1");
        TerminalGate<CargoFlight> gateC1 = new TerminalGate<>("C1");

        // 2. Create Flights
        CommercialFlight commFlight = new CommercialFlight("TK1902", "Istanbul", 85.0);
        CargoFlight cargoFlight = new CargoFlight("FX11", "Memphis", 20.0); // Low fuel!
        EmergencyFlight medevac = new EmergencyFlight("LIFE-99", "Ankara", 40.0, "Organ Transport");

        atc.registerFlight(commFlight);
        atc.registerFlight(cargoFlight);
        atc.registerFlight(medevac);

        // 3. Passenger & Baggage Subsystem Test
        System.out.println("--- TESTING PASSENGER SECURITY & BOARDING ---");
        Passenger pass1 = new Passenger("TR-991", "Ahmet Yilmaz", Passenger.ECONOMY);
        pass1.addBaggage(new Baggage("BAG-A1", 20.5)); // Safe

        Passenger pass2 = new Passenger("US-882", "John Doe", Passenger.FIRST_CLASS);
        pass2.addBaggage(new Baggage("BAG-B1", 15.0)); // Safe

        Passenger pass3 = new Passenger("UK-773", "Jane Smith", Passenger.BUSINESS);
        pass3.addBaggage(new Baggage("BAG-C1", 105.0)); // Too Heavy! Fails security.

        // Process Security
        atc.clearPassengerSecurity(pass1);
        atc.clearPassengerSecurity(pass2);
        atc.clearPassengerSecurity(pass3); // Fails

        System.out.println();
        // Dock and Board
        gateA1.dockFlight(commFlight);
        commFlight.queueForBoarding(pass1);
        commFlight.queueForBoarding(pass2);
        commFlight.queueForBoarding(pass3); // Rejected at gate

        System.out.println();
        // Execute Boarding - First class boards before Economy!
        commFlight.prepareForDeparture();

        // 4. Cargo Logistics Test
        System.out.println("\n--- TESTING CARGO LOGISTICS ---");
        gateC1.dockFlight(cargoFlight);
        cargoFlight.loadFreight(new CargoPayload("MFST-001", 50.0, false));
        cargoFlight.loadFreight(new CargoPayload("MFST-HAZ-002", 10.0, true)); // Hazmat
        cargoFlight.prepareForDeparture();

        // 5. Air Traffic Control Test (Priority Queues)
        System.out.println("\n--- TESTING AIR TRAFFIC CONTROL ---");

        // Flights releasing from gates and requesting takeoff
        atc.requestTakeoff(gateA1.releaseFlight());
        atc.requestTakeoff(gateC1.releaseFlight());

        // Takeoffs process sequentially (FIFO Queue)
        atc.processNextTakeoff();
        atc.processNextTakeoff();

        // Incoming Flights request landing (PriorityQueue)
        atc.requestLanding(commFlight);
        atc.requestLanding(cargoFlight);
        atc.requestLanding(medevac); // Arrives late but jumps the queue!

        // Landing Order Expected: Medevac (9999), Cargo (Hazmat + Low Fuel), CommFlight (Safe Fuel)
        System.out.println("\n>>> INITIATING LANDING SEQUENCE <<<");
        atc.processNextLanding();
        atc.processNextLanding();
        atc.processNextLanding();

        // 6. System Reporting (Stack)
        atc.printDailyAuditLog();
    }
}
//CODE HERE
class Baggage {
    private String tagId;
    private double weight;

    public Baggage(String tagId, double weight) {
        this.tagId = tagId;
        this.weight = weight;
    }

    public boolean performSecurityCheck() {
        return weight <= 100.0;
    }
}

class Passenger implements Comparable<Passenger> {
    public static final int FIRST_CLASS = 3, BUSINESS = 2, ECONOMY = 1;
    private String passportId, name;
    private int ticketPriority;
    private List<Baggage> bags = new ArrayList<>();

    public Passenger(String passportId, String name, int ticketPriority) {
        this.passportId = passportId;
        this.name = name;
        this.ticketPriority = ticketPriority;
    }

    public void addBaggage(Baggage bag) {
        bags.add(bag);
    }
    public List<Baggage> getBags() {
        return bags;
    }
    public String getPassportId() {
        return passportId;
    }

    public boolean performSecurityCheck() {
        for (Baggage b : bags)
            if (!b.performSecurityCheck())
                return false;
        return true;
    }

    @Override
    public int compareTo(Passenger o) {
        return Integer.compare(o.ticketPriority, this.ticketPriority); }
}

class CargoPayload {
    private boolean hazardous;
    public CargoPayload(String id, double t, boolean h) {
        this.hazardous = h;
    }
    public boolean isHazardous() {
        return hazardous;
    }
}

abstract class Flight implements Comparable<Flight> {
    public static final String STATUS_SCHEDULED = "Scheduled", STATUS_BOARDING = "Boarding",
            STATUS_IN_FLIGHT = "In Flight", STATUS_LANDED = "Landed", STATUS_DOCKED = "Docked";
    protected String flightId, destination, status;
    protected double remainingFuel;

    public Flight(String id, String dest, double fuel) {
        this.flightId = id;
        this.destination = dest;
        this.remainingFuel = fuel;
        this.status = STATUS_SCHEDULED;
    }

    public abstract void prepareForDeparture();
    public abstract double calculatePriority();

    public void setStatus(String s) {
        this.status = s;
    }
    public String getFlightId() {
        return flightId;
    }
    public String getDestination() {
        return destination;
    }

    @Override
    public int compareTo(Flight o) {
        return this.flightId.compareTo(o.flightId);
    }
}

class TerminalGate<T extends Flight> {
    private String gateId;
    private T dockedFlight;

    public TerminalGate(String id) {
        this.gateId = id;
    }

    public boolean dockFlight(T f) {
        if (dockedFlight != null) return false;
        dockedFlight = f;
        f.setStatus(Flight.STATUS_DOCKED);
        System.out.println("Flight " + f.getFlightId() + " docked at Gate " + gateId);
        return true;
    }

    public T releaseFlight() {
        T f = dockedFlight;
        dockedFlight = null;
        return f;
    }
}

class CommercialFlight extends Flight {
    private PriorityQueue<Passenger> boardingQueue = new PriorityQueue<>();
    private Deque<Baggage> cargoHold = new ArrayDeque<>();

    public CommercialFlight(String id, String d, double f) {
        super(id, d, f);
    }

    public void queueForBoarding(Passenger p) {
        if (p.performSecurityCheck()) boardingQueue.offer(p);
        else System.out.println("Boarding denied for: " + p.getPassportId());
    }

    @Override
    public void prepareForDeparture() {
        System.out.println("Boarding " + flightId + "...");
        while (!boardingQueue.isEmpty()) {
            Passenger p = boardingQueue.poll();
            for (Baggage b : p.getBags()) cargoHold.push(b);
        }
        setStatus(STATUS_SCHEDULED);
    }

    @Override
    public double calculatePriority() { return 10.0 + (100.0 / (remainingFuel + 1)); }
}

class CargoFlight extends Flight {
    private List<CargoPayload> freight = new ArrayList<>();
    public CargoFlight(String id, String d, double f) { super(id, d, f); }
    public void loadFreight(CargoPayload p) { freight.add(p); }
    @Override
    public void prepareForDeparture() {
        setStatus(STATUS_SCHEDULED);
    }
    @Override
    public double calculatePriority() {
        double p = 5.0;
        for (CargoPayload cp : freight) if (cp.isHazardous()) p += 50.0;
        return p + (100.0 / (remainingFuel + 1));
    }
}

class EmergencyFlight extends Flight {
    public EmergencyFlight(String id, String d, double f, String prot) { super(id, d, f); }

    @Override
    public void prepareForDeparture() {
        setStatus(STATUS_SCHEDULED);
    }
    @Override
    public double calculatePriority() {
        return 9999.9;
    }
}

class AirTrafficControl {
    private PriorityQueue<Flight> approachQueue = new PriorityQueue<>((a, b) -> Double.compare(b.calculatePriority(), a.calculatePriority()));
    private Queue<Flight> departureQueue = new LinkedList<>();
    private Set<String> clearedPassports = new HashSet<>();
    private Queue<String> auditLog = new ArrayDeque<>();

    public void registerFlight(Flight f) {
        auditLog.offer("REGISTERED: " + f.getFlightId()); }

    public void clearPassengerSecurity(Passenger p) {
        if (p.performSecurityCheck()) {
            clearedPassports.add(p.getPassportId());
            auditLog.offer("CLEARED: " + p.getPassportId());
        } else {
            auditLog.offer("DENIED: " + p.getPassportId());
        }
    }

    public void requestLanding(Flight f) {
        f.setStatus(Flight.STATUS_IN_FLIGHT);
        approachQueue.offer(f);
        auditLog.offer("LANDING REQUESTED: " + f.getFlightId());
    }

    public void processNextLanding() {
        Flight f = approachQueue.poll();
        if (f != null) {
            f.setStatus(Flight.STATUS_LANDED);
            System.out.println("Landed: " + f.getFlightId());
            auditLog.offer("LANDED: " + f.getFlightId());
        }
    }

    public void requestTakeoff(Flight f) {
        departureQueue.offer(f);
        auditLog.offer("TAKEOFF REQUESTED: " + f.getFlightId());
    }

    public void processNextTakeoff() {
        Flight f = departureQueue.poll();
        if (f!= null) {
            f.setStatus(Flight.STATUS_IN_FLIGHT);
            System.out.println("Took off: " + f.getFlightId());
            auditLog.offer("TOOK OFF: " + f.getFlightId());
        }
    }

    public void printDailyAuditLog() {
        System.out.println("--- DAILY AUDIT LOG (LATEST FIRST) ---");
        while (!auditLog.isEmpty())
            System.out.println(auditLog.poll());
    }
}