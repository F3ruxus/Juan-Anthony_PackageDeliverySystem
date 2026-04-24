import java.util.ArrayList;
import java.util.List;

enum TruckType {
    SMALL(1000, 2000),
    MEDIUM(2000, 4000),
    LARGE(4000, 8000);

    private final double volumeLimit;
    private final double weightLimit;

    TruckType(double volumeLimit, double weightLimit) {
        this.volumeLimit = volumeLimit;
        this.weightLimit = weightLimit;
    }

    public double getVolumeLimit() {
        return volumeLimit;
    }

    public double getWeightLimit() {
        return weightLimit;
    }
}

public class Truck {
    private int truckID;
    private TruckType truckType;
    private double volumeLimit;
    private double weightLimit;
    private double totalWeight;
    private double totalVolume;
    private List<Package> packages;

    public Truck(int truckID, TruckType truckType) {
        this.truckID = truckID;
        this.truckType = truckType;
        this.volumeLimit = truckType.getVolumeLimit();
        this.weightLimit = truckType.getWeightLimit();
        this.totalWeight = 0;
        this.totalVolume = 0;
        this.packages = new ArrayList<>();
    }

    public int getTruckID() {
        return truckID;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public double getVolumeLimit() {
        return volumeLimit;
    }

    public double getWeightLimit() {
        return weightLimit;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void addPackage(Package pkg) {
        if (isFull(pkg)) {
            throw new IllegalStateException("Cannot add package: truck is full");
        }
        packages.add(pkg);
        totalWeight += pkg.getWeight();
        totalVolume += pkg.getVolume();
    }

    public void removePackage(Package pkg) {
        if (packages.remove(pkg)) {
            totalWeight -= pkg.getWeight();
            totalVolume -= pkg.getVolume();
        }
    }

    public boolean isFull(Package pkg) {
        return totalWeight + pkg.getWeight() > weightLimit || totalVolume + pkg.getVolume() > volumeLimit;
    }
}