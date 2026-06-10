public class Main {
    public static void main(String[] args) {
        Vehicle vehicle= new Vehicle("Hyundai",2009,2800.0);
        Car car= new Car("Fiat", 2004, 3600.0,5,true);
        Truck truck= new Truck("Toyota",2013,1700.0,320000.0);
        System.out.println(car.toString());
        System.out.println(truck.toString());

        vehicle.start();
        car.start();
        truck.start();
        truck.stop();
        truck.addWeight(500.0);
        System.out.println(truck.toString());
        truck.removeWeight(200.0);
        System.out.println(truck.toString());

        System.out.println("--- Final Status --- ");
        System.out.println(vehicle.toString());
        System.out.println(car.toString());
        System.out.println(truck.toString());
        }
    }
    class Vehicle {
    private String model;
    private int year;
    private double price;
    private boolean running;

    public Vehicle(String model, int year, double price) {
        this.model=model;
        this.year= year;
        this.price=price;
    }
    public String getModel() {
        return model;
    }
    public int getYear() {
        return year;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double newPrice) {
        price =newPrice;
    }
    public void start() {
        running=true;
        System.out.println("Car is now running");
    }
        public void stop() {
            running=false;
            System.out.println("Car is now stopping");
        }
    @Override
        public String toString() {
        return model + " " + year + " ( " + price + " ) ";
        }
}
class Car extends Vehicle {
    private int numberOfGears;
    private boolean automatic;

    public Car (String model, int year, double price, int numberOfGears,boolean automatic) {
        super(model,year,price);
        this.numberOfGears= numberOfGears;
        this.automatic=automatic;
    }
    @Override
    public String toString() {
        return super.toString() + " " + automatic;
    }
}
class Truck extends Vehicle {
    private double capacity,currentWeight;

    public Truck (String model, int year, double price, double capacity) {
        super(model,year,price);
        this.capacity=capacity;
    }
    public String toString() {
        return super.toString() + " [Kapasite: " + capacity + ", Mevcut Yük: " + currentWeight + "]";
    }
    public void addWeight(double weight) {
        this.currentWeight += weight;
    }
    public void removeWeight(double weight) {
        if (currentWeight - weight >= 0) {
            currentWeight -= weight;
        } else {
            System.out.println("Kamyonda o kadar yük yok!");
        }
    }
}