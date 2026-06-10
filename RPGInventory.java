import java.util.Arrays;
public class RPGInventory {
    static int[] inventory = {101, 505, 202};
    public static void main(String[] args) {
        System.out.println("--- START ---");
        System.out.print("Inventory: " + Arrays.toString(inventory));
        System.out.println(" | Total Weight: " + calculateTotalWeight());

        System.out.println("--- LOOTING RING (550) ---");
        lootItem(550);
        System.out.print("Inventory: " + Arrays.toString(inventory));
        System.out.println(" | Total Weight: " + calculateTotalWeight());

        System.out.println("--- DROPPING SHIELD (202) ---");
        dropItem(202);
        System.out.print("Inventory: " + Arrays.toString(inventory));
        System.out.println(" | Total Weight: " + calculateTotalWeight());

        System.out.println("--- DROPPING MISSING ITEM (999) ---");
        dropItem(999);
    }
    public static void lootItem(int newItemID) {
        int[] temp = new int[inventory.length + 1];
        for (int i = 0; i < inventory.length; i++) {
            temp[i] = inventory[i];
        }
        temp[temp.length - 1] = newItemID;
        inventory = temp;
        System.out.println("--> Eşya Looted: " + newItemID + " (Yeni boyut: " + inventory.length + ")");
    }
    public static void dropItem(int itemToRemove) {
        int indexToRemove = -1;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == itemToRemove)
                indexToRemove = i;
            break;
        }
        if (indexToRemove == -1)
            System.out.println("--> Hata: Eşya bulunamadı " + itemToRemove + ". İşlem yapılmadı.");
        return;

        int[] temp = new int[inventory.length - 1];
        int tempIndex = 0;

        for (int i = 0; i < inventory.length; i++) {
            if (i != indexToRemove)
                temp[tempIndex] = inventory[i];
                tempIndex++;
        }
        inventory = temp;
        System.out.println("--> Eşya Dropped: " + itemToRemove + " (Yeni boyut: " + inventory.length + ")");
    }
    public static double calculateTotalWeight() {
        double totalWeight = 0.0;
        for (int itemID : inventory) {
            if (itemID >= 100 && itemID <= 199)
                totalWeight += 5.0;
            else if (itemID >= 200 && itemID <= 299)
                totalWeight += 10.0;
            else
                totalWeight += 1.0;
        }
        return totalWeight;
    }
}
