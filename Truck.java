import java.util.ArrayList;
import java.util.List;

public class Truck {
    public enum TruckType {
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

    private int truckID;
    private Truck.TruckType truckType;
    private double volumeLimit;
    private double weightLimit;
    private double totalWeight;
    private double totalVolume;
    private List<Pack> packages;

    public Truck(int truckID, Truck.TruckType truckType) {
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

    public Truck.TruckType getTruckType() {
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

    public List<Pack> getPackages() {
        return packages;
    }

    public void addPackage(Pack pkg) {
        if (isFull(pkg)) {
            throw new TruckException("Cannot add package: truck is full");
        }
        packages.add(pkg);
        totalWeight += pkg.getWeight();
        totalVolume += pkg.getVolume();
    }

    public void removePackage(Pack pkg) {
        if (packages.remove(pkg)) {
            totalWeight -= pkg.getWeight();
            totalVolume -= pkg.getVolume();
        }
    }

    public boolean isFull(Pack pkg) {
        return totalWeight + pkg.getWeight() > weightLimit || totalVolume + pkg.getVolume() > volumeLimit;
    }

    public boolean canFit(Pack pkg) {
        return !isFull(pkg);
    }

    public void clearPackages() {
        packages.clear();
        totalWeight = 0;
        totalVolume = 0;
    }

    public int getPackageCount() {
        return packages.size();
    }

    @Override
    public String toString() {
        return String.format("Truck %d (%s): Weight=%.2f/%.2f kg, Volume=%.2f/%.2f m³",
                truckID, truckType, totalWeight, weightLimit, totalVolume, volumeLimit);
    }
}