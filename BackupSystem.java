public class BackupSystem {
    public static void main(String[] args) {
        HardDrive[] drives = new HardDrive[5];
        drives[0] = new HardDrive(1, 100.0, 90);   // healthy, 100 GB
        drives[1] = new HardDrive(2,  80.0, 55);   // broken  (health < 70) → should be skipped
        drives[2] = null;                           // empty slot             → should be skipped
        drives[3] = new HardDrive(3,  60.0, 75);   // healthy,  60 GB
        drives[4] = new HardDrive(4, 120.0, 85);   // healthy, 120 GB

        DataFile file = new DataFile(230.0);

        System.out.println("=== Starting backup of " + file.size + " GB ===");
        file.saveToDrives(drives);

        System.out.println("\n=== Drive Status After Backup ===");
        for (HardDrive drive : drives) {
            if (drive != null) {
                System.out.println(drive);
            }
        }

        System.out.println("\nBackup complete: " + file.isSaved);
    }
}
class HardDrive {
    Integer id;
    Double  capacity;
    Double  usedSpace;
    Integer health;

    HardDrive(Integer id, Double capacity, Integer health) {
        this.id        = id;
        this.capacity  = capacity;
        this.usedSpace = 0.0;
        this.health    = health;
    }

    Double getFreeSpace() {
        return capacity - usedSpace;
    }

    void addData(Double amount) {
        usedSpace += amount;
    }

    @Override
    public String toString() {
        return "Drive " + id + " [Health:" + health + "] Used:" + usedSpace + "/" + capacity;
    }
}

class DataFile {
    Double  size;
    Boolean isSaved;

    DataFile(Double size) {
        this.size    = size;
        this.isSaved = false;
    }

    void saveToDrives(HardDrive[] drives) {
        double remaining = size;

        for (HardDrive drive : drives) {
            if (drive == null) {
                continue;
            }

            if (drive.health < 70) {
                System.out.println("Skip Drive " + drive.id);
                continue;
            }

            double free = drive.getFreeSpace();

            if (free <= 0) {
                continue;
            }

            if (remaining <= free) {
                // Case 1: drive has enough room for everything that is left
                drive.addData(remaining);
                System.out.println("Part " + remaining + " -> Drive " + drive.id);
                remaining = 0.0;
            } else {
                // Case 2: fill the drive and carry the rest forward
                drive.addData(free);
                System.out.println("Part " + free + " -> Drive " + drive.id);
                remaining -= free;
            }

            if (remaining <= 0) {
                break;
            }
        }

        if (remaining <= 0) {
            isSaved = true;
            System.out.println("File Saved.");
        } else {
            System.out.println("File Incomplete. Left: " + remaining);
        }
    }
}

