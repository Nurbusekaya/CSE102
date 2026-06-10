public class Assignment1 {
    public static void main(String[] args) {
        System.out.println("--- AIRPORT GATE MANAGER TESTING ---");
        // 1. Setup
        Airport myAirport = new Airport(3, 2);
        DomesticFlight d1 = new DomesticFlight(101, 15, 1);
        DomesticFlight d2 = new DomesticFlight(102, 45, 2);
        DomesticFlight d3 = new DomesticFlight(103, 120, 1);
        DomesticFlight d4 = new DomesticFlight(104, 60, 3); // Extra flight for testing full capacity

        InternationalFlight i1 = new InternationalFlight(901, 30, 15);

        // 2. Test InternationalFlight Child Method
        System.out.println("\n--- Testing Child Methods ---");
        System.out.println("Actual Intl Departure Time (Expected 45): " + i1.getActualDepartureTime());

        // 3. Test Gate Assignment
        System.out.println("\n--- Testing Gate Assignment ---");
        System.out.println("Assign D1 (Expected true): " + myAirport.assignGate(d1));

        // Populate array to test capacity
        myAirport.assignGate(d2);
        myAirport.assignGate(d3);

        System.out.println("Assign D4 to full airport (Expected false): " + myAirport.assignGate(d4));
        System.out.println("Assign I1 (Expected true): " + myAirport.assignGate(i1));

        // 4. Test Best Gate Logic (Airport is full, D1 at index 0 leaves soonest in 15 mins)
        System.out.println("\n--- Testing Next Available Gate ---");
        System.out.println("Next available domestic gate index (Expected 0): " + myAirport.findNextAvailableDomesticGate());

        // 5. Test Update Time & Auto-Drop
        System.out.println("\n--- Testing Time Update & Auto-Drop ---");
        System.out.println("Simulating 20 minutes passing...");
        myAirport.updateTime(20);

        // D1 (15 mins) should have dropped. Gate 0 should be empty.
        System.out.println("Next available domestic gate index (Expected 0): " + myAirport.findNextAvailableDomesticGate());

        // Assign D4 again, it should now take the empty spot at index 0
        System.out.println("Assign D4 now (Expected true): " + myAirport.assignGate(d4));

        // 6. Test Iteration
        System.out.println("\n--- Testing Cascading Delay (Iteration) ---");
        // Currently, D4 (60 mins), D2 (45-20=25 mins), and D3 (120-20=100 mins) are at the gates
        int delayedCount = myAirport.applyCascadingDelay(30);
        System.out.println("Total domestic flights delayed (Expected 3): " + delayedCount);
        System.out.println("D2's new departure time (Expected 55): " + d2.getMinutesToDeparture());
    }
}
class Flight {
     int flightCode;
     int minutesToDeparture;
    public Flight (int flightCode,int minutesToDeparture) {
        this.flightCode = flightCode;
        this.minutesToDeparture = minutesToDeparture;
    }
    public int getFlightCode() {
        return flightCode;
    }
    public void setFlightCode (int flightCode) {
        this.flightCode = flightCode;
    }
    public int getMinutesToDeparture() {
        return minutesToDeparture;
    }
    public void setMinutesToDeparture (int minutesToDeparture) {
        this.minutesToDeparture= minutesToDeparture;
    }
}
 class DomesticFlight extends Flight {
     private int terminalZone;

     public DomesticFlight(int flightCode, int minutesToDeparture, int terminalZone) {
         super(flightCode, minutesToDeparture);
         this.terminalZone = terminalZone;
     }
     public int getTerminalZone() {
         return terminalZone;
     }
     public void setTerminalZone(int terminalZone) {
     }
 }
class InternationalFlight extends Flight {
    private int customsTime;

  public InternationalFlight(int flightCode, int minutesToDeparture, int customsTime) {
     super(flightCode,minutesToDeparture);
     this.customsTime= customsTime;
        }
public int getActualDepartureTime() {
      return  this.getMinutesToDeparture() + this.customsTime;
}
}
class Airport {
    private DomesticFlight[] domesticCapacity;
    private InternationalFlight[] intlCapacity;

    public Airport (int domesticCapacity,int intlCapacity) {
        this.domesticCapacity= new DomesticFlight[domesticCapacity];
        this.intlCapacity= new InternationalFlight[intlCapacity];
    }
    public boolean assignGate(DomesticFlight df) {
        for (int i = 0; i < domesticCapacity.length; i++) {
            if (domesticCapacity[i] == null) {
                domesticCapacity[i] = df;
                return true;
            }
        }
        return false;
    }

    public boolean assignGate(InternationalFlight itf) {
        for (int i = 0; i < intlCapacity.length; i++) {
            if (intlCapacity[i] == null) {
                intlCapacity[i] = itf;
                return true;
            }
        }
        return false;
    }

    public void updateTime(int minutesPassed) {
        for (int i = 0; i < domesticCapacity.length; i++) {
            if (domesticCapacity[i] != null) {
                int currentTime = domesticCapacity[i].getMinutesToDeparture();
                domesticCapacity[i].setMinutesToDeparture(currentTime - minutesPassed);

                if (domesticCapacity[i].getMinutesToDeparture() <= 0) {
                    domesticCapacity[i] = null;
                }
            }
        }
        for (int i = 0; i < intlCapacity.length; i++) {
            if (intlCapacity[i] != null) {
                int currentTime = intlCapacity[i].getMinutesToDeparture();
                intlCapacity[i].setMinutesToDeparture(currentTime - minutesPassed);

                if (intlCapacity[i].getMinutesToDeparture() <= 0) {
                    intlCapacity[i] = null;
                }
            }
        }
    }
    public int findNextAvailableDomesticGate() {
        for (int i = 0; i < domesticCapacity.length; i++) {
            if (domesticCapacity[i] == null) {
                return i;
            }
        }
        int minIndex = 0;
        int minTime = domesticCapacity[0].getMinutesToDeparture();

        for (int i = 1; i < domesticCapacity.length; i++) {
            if (domesticCapacity[i].getMinutesToDeparture() < minTime) {
                minTime = domesticCapacity[i].getMinutesToDeparture();
                minIndex = i;
            }
        }
        return minIndex;
    }
    public int applyCascadingDelay(int delayAmount) {
        int count = 0;
        for (int i = 0; i < domesticCapacity.length; i++) {
            if (domesticCapacity[i] != null) {
                int newTime = domesticCapacity[i].getMinutesToDeparture() + delayAmount;
                domesticCapacity[i].setMinutesToDeparture(newTime);
                count++;
            }
        }
        return count;
    }
    }

