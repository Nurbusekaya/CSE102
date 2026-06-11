import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
public class FactoryLogisticsSimulator {
    public static void main(String[] args) {
        System.out.println("=== Module Cost Tests ===");
        ServerUnit sv = new ServerUnit("ServerX", 5, 3, 4);
        DroneUnit du = new DroneUnit("DroneZ", 2, 7, 3.0);
        System.out.println(sv.getModelName() + " cost: " + sv.getManufacturingCost()); // 980.0
        System.out.println(du.getModelName() + " cost: " + du.getManufacturingCost()); // 100 + 27*0.2 = 105.4
        System.out.println("\n=== Shipping Fee Tests ===");
        // Manhattan: |5-0| + |3-0| = 8  × 15 = 120
        System.out.println("Shipping fee: " + sv.calculateShippingFee(0, 0)); // 120.0
        // |2-2| + |7-2| = 5 × 15 = 75
        System.out.println("Shipping fee: " + du.calculateShippingFee(2, 2)); // 75.0

        System.out.println("\n=== WiringMachine Tests ===");
        WiringMachine wm = new WiringMachine("Wirer-1");
        wm.processHardware(sv);
        System.out.println("After wiring: " + sv.getManufacturingCost()); // 1030.0
        System.out.println("\n=== WarehouseBin Tests ===");
        WarehouseBin<TechModule> bin = new WarehouseBin<>(2);
        bin.addPart(sv);
        bin.addPart(du);
        bin.addPart(new ServerUnit("Overflow", 0, 0, 1)); // ignored (full)
        System.out.println("Retrieved : " + bin.retrievePart().getModelName()); // DroneZ
        System.out.println("Retrieved : " + bin.retrievePart().getModelName()); // ServerX
        System.out.println("Empty retrieve: " + bin.retrievePart());                // null
        System.out.println("\n=== FactoryFloor Assembly Line ===");
        MachineUnit[][] grid = {
                {new WiringMachine("W1"), null, new WiringMachine("W2")},
                {null, new WiringMachine("W3"), null}
        };
        Queue<TechModule> queue = new LinkedList<>();
        ServerUnit s1 = new ServerUnit("Alpha", 1, 1, 2);  // cost: 740
        DroneUnit d1 = new DroneUnit("Beta", 3, 3, 2.0); // cost: 101.6
        queue.add(s1);
        queue.add(d1);
        FactoryFloor floor = new FactoryFloor(grid, queue);
        System.out.println("Ready before run: " + floor.getReadyModules().size()); // 0
        floor.runAssemblyLine();
        Stack<TechModule> ready = floor.getReadyModules();
        System.out.println("Ready modules: " + ready.size()); // 2
        TechModule top = ready.pop();
        System.out.println("Top (LIFO): " + top.getModelName()          // Beta
                + " cost: " + top.getManufacturingCost());  // 251.6
        TechModule next = ready.pop();
        System.out.println("Next: " + next.getModelName() + " cost: " + next.getManufacturingCost());      // 890.0
    }
}
interface IShipable {
    double calculateShippingFee (int startX, int startY);
}
 abstract class TechModule implements IShipable {
        private final String modelName;
        private int bayX;
        private int bayY;
        private double manufacturingCost;

    TechModule (String modelName,int bayX,int bayY)  {
        this.modelName= modelName;
        this.bayX=bayX;
        this.bayY=bayY;
        this.manufacturingCost=0.0;
    }
     public String getModelName() {
        return modelName;
    }
     public int getBayX()   {
        return bayX;
    }
     public int getBayY()  {
        return bayY;
    }
     public double getManufacturingCost()  {
        return manufacturingCost;
    }
     public void setBayX (int bayX)  {
        this.bayX = bayX;
    }
     public void setBayY (int bayY)   {
        this.bayY = bayY;
    }
     public void setManufacturingCost(double cost)  {
        this.manufacturingCost = cost;
    }
@Override
 double calculateShippingFee(int startX, int startY) {
    int distance = Math.abs(bayX - startX) + Math.abs(bayY - startY);
    return distance * 15.0;
}
}
class ServerUnit extends TechModule {
    private String modelName;
    private int bayX;
    private int bayY;
    private final int cpuCount;

    public ServerUnit (String modelName,int bayX,int bayY,int cpuCount) {
        super(modelName,bayX,bayY);
        this.cpuCount = cpuCount;
        setManufacturingCost(500.0 + (cpuCount * 120.0));
    }
}
class DroneUnit extends TechModule {
    private String modelName;
    private int bayX;
    private int bayY;
    private final double cubicFrameSize;

    public DroneUnit (String modelName, int bayX,int bayY, double cubicFrameSize) {
        super(modelName,bayX,bayY);
        this.cubicFrameSize=cubicFrameSize;
        setManufacturingCost(100.0 + cubicFrameSize* cubicFrameSize*0.2);
    }
}
abstract class MachineUnit {
    private final String name;

    public MachineUnit (String name) {
        this.name=name;
    }
    public String getName() {
        return name;
    }

public abstract void processHardware(TechModule module);
}

class WiringMachine extends MachineUnit {
    String name;

    public WiringMachine (String name) {
        super(name);
    }

    @Override
    public void processHardware(TechModule module) {
        module.setManufacturingCost(module.getManufacturingCost() + 50.0);
    }
}

class WarehouseBin<T>  {
    Object[] parts;
    int [] count;

    public WarehouseBin (int capacity) {
        parts = new Object[capacity];
        count = 0;
    }
    public void addPart (T part) {
        if (count < parts.length) {
            parts[count] = part;
            count++;
        }
    }
    public T retrievePart() {
        if (count = 0)
            return null;
        count--;
        return (T) parts[count];
    }
}
class FactoryFloor {
    MachineUnit[][] grid;
    Queue<TechModule> chassisQueue;
    Stack<TechModule> readyModules;

public FactoryFloor(MachineUnit[][] grid, Queue<TechModule> chassisQueue) {
    this.grid         = grid;
    this.chassisQueue = chassisQueue;
    this.readyModules = new Stack<>();
}
public void runAssemblyLine() {
    while (!chassisQueue.isEmpty()) {
        TechModule module = chassisQueue.poll();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    grid[i][j].processHardware(module);
                }
            }
        }
        readyModules.push(module);
    }
}
    public Stack<TechModule> getReadyModules() {
        return readyModules;
    }
}





 
