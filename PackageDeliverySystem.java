import java.util.*;
import java.util.stream.Collectors;

/**
 * PackageDeliverySystem - Main class for managing package delivery operations
 * 
 * Manages trucks and packages with an optimization algorithm to assign packages
 * to trucks while minimizing total truck hours.
 */
public class PackageDeliverySystem {
    private List<Truck> trucks;
    private List<Pack> packages;
    private static final double PACKAGE_DELIVERY_TIME_MINUTES = 10.0; // minutes per package

    /**
     * Constructor - Initializes empty lists for trucks and packages
     */
    public PackageDeliverySystem() {
        this.trucks = new ArrayList<>();
        this.packages = new ArrayList<>();
    }

    /**
     * Adds a truck to the delivery system
     */
    public void addTruck(Truck truck) {
        if (truck == null) {
            throw new IllegalArgumentException("Truck cannot be null");
        }
        trucks.add(truck);
    }

    /**
     * Adds a package to the delivery system
     */
    public void addPackage(Pack pkg) {
        if (pkg == null) {
            throw new IllegalArgumentException("Package cannot be null");
        }
        packages.add(pkg);
    }

    /**
     * Gets all trucks
     */
    public List<Truck> getTrucks() {
        return new ArrayList<>(trucks);
    }

    /**
     * Gets all packages
     */
    public List<Pack> getPackages() {
        return new ArrayList<>(packages);
    }

    public void assignPackagesToTrucks() {
        if (trucks.isEmpty()) {
            throw new IllegalStateException("No trucks available for assignment");
        }
        if (packages.isEmpty()) {
            System.out.println("No packages to assign");
            return;
        }

        // Step 1: Sort packages by delivery date, then by time deadline (for special packages)
        List<Pack> sortedPackages = packages.stream()
                .sorted((p1, p2) -> {
                    int dateComparison = p1.getDeliveryDate().compareTo(p2.getDeliveryDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    // If same delivery date, prioritize special packages with earlier deadlines
                    if (p1 instanceof SpecPack && p2 instanceof SpecPack) {
                        return Integer.compare(((SpecPack) p1).getTimeDeadline(),
                                ((SpecPack) p2).getTimeDeadline());
                    }
                    if (p1 instanceof SpecPack) return -1; // Special packages first
                    if (p2 instanceof SpecPack) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        // Step 2: For each package, find the best truck
        for (Pack pkg : sortedPackages) {
            Truck bestTruck = null;
            double minTruckHours = Double.POSITIVE_INFINITY;

            // Step 3: Evaluate all truck types and instances
            for (Truck.TruckType truckType : Truck.TruckType.values()) {
                List<Truck> trucksOfType = trucks.stream()
                        .filter(t -> t.getTruckType() == truckType)
                        .collect(Collectors.toList());

                for (Truck truck : trucksOfType) {
                    // Check if package can fit in this truck
                    if (truck.canFit(pkg)) {
                        // Consider hypothetical assignment
                        truck.addPackage(pkg);
                        double truckHours = calculateTruckHours();

                        // Track the truck with minimum hours
                        if (truckHours < minTruckHours) {
                            minTruckHours = truckHours;
                            bestTruck = truck;
                        }

                        // Undo hypothetical assignment
                        truck.removePackage(pkg);
                    }
                }
            }

            // Step 4: Assign to best truck or log error
            if (bestTruck != null) {
                bestTruck.addPackage(pkg);
                System.out.println("✓ Assigned " + pkg.getPackageID() + " to " + bestTruck.getTruckID());
            } else {
                System.err.println("✗ No suitable truck found for package: " + pkg.getPackageID());
            }
        }
    }

    /**
     * Calculates total truck hours for the current assignment
     * 
     * Formula: TotalTruckHours = Σ (TypeWeight × DeliveryTime)
     * where TypeWeight is 1 for Small, 2 for Medium, 3 for Large
     * and DeliveryTime is based on number of packages × average delivery time per package
     */
    public double calculateTruckHours() {
        double totalTruckHours = 0;

        for (Truck truck : trucks) {
            int typeWeight = getTruckTypeWeight(truck.getTruckType());
            double deliveryTime = calculateDeliveryTime(truck.getPackages());
            totalTruckHours += typeWeight * deliveryTime;
        }

        return totalTruckHours;
    }

    /**
     * Gets the weight factor for a truck type
     * Small: 1, Medium: 2, Large: 3
     */
    private int getTruckTypeWeight(Truck.TruckType truckType) {
        switch (truckType) {
            case SMALL:
                return 1;
            case MEDIUM:
                return 2;
            case LARGE:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown truck type: " + truckType);
        }
    }

    /**
     * Calculates delivery time for a list of packages
     * 
     * Heuristic: Each package takes PACKAGE_DELIVERY_TIME_MINUTES minutes to deliver
     * Time is returned in hours
     */
    private double calculateDeliveryTime(List<Pack> packageList) {
        if (packageList.isEmpty()) {
            return 0;
        }
        // Simple heuristic: 10 minutes per package, converted to hours
        return (packageList.size() * PACKAGE_DELIVERY_TIME_MINUTES) / 60.0;
    }

    /**
     * Generates a comprehensive delivery report
     */
    public String generateDeliveryReport() {
        StringBuilder report = new StringBuilder();

        report.append("\n========================================\n");
        report.append("     PACKAGE DELIVERY REPORT\n");
        report.append("========================================\n\n");

        // Summary statistics
        report.append("SYSTEM SUMMARY:\n");
        report.append(String.format("Total Trucks: %d\n", trucks.size()));
        report.append(String.format("Total Packages: %d\n", packages.size()));
        report.append(String.format("Total Truck Hours: %.2f hours\n", calculateTruckHours()));
        report.append("\n");

        // Truck-wise breakdown
        report.append("TRUCK ASSIGNMENTS:\n");
        report.append("----------------------------------------\n");

        int assignedPackages = 0;
        for (Truck truck : trucks) {
            report.append(truck.toString()).append("\n");

            if (!truck.getPackages().isEmpty()) {
                report.append("  Packages:\n");
                for (Pack pkg : truck.getPackages()) {
                    report.append(String.format("    - %s (Weight: %.2f kg, Volume: %.2f m³)\n",
                            pkg.getPackageID(), pkg.getWeight(), pkg.getVolume()));
                    assignedPackages++;
                }
            }
            report.append("\n");
        }

        // Unassigned packages
        int unassignedPackages = packages.size() - assignedPackages;
        report.append("PACKAGE SUMMARY:\n");
        report.append("----------------------------------------\n");
        report.append(String.format("Assigned Packages: %d\n", assignedPackages));
        report.append(String.format("Unassigned Packages: %d\n", unassignedPackages));

        if (unassignedPackages > 0) {
            report.append("\nUNASSIGNED PACKAGES:\n");
            Set<Integer> assignedPackageIds = trucks.stream()
                    .flatMap(t -> t.getPackages().stream())
                    .map(Pack::getPackageID)
                    .collect(Collectors.toSet());

            for (Pack pkg : packages) {
                if (!assignedPackageIds.contains(pkg.getPackageID())) {
                    report.append(String.format("  - %s: %s\n", pkg.getPackageID(), pkg.toString()));
                }
            }
        }

        report.append("\n========================================\n");
        return report.toString();
    }

    /**
     * Gets utilization report for all trucks
     */
    public void printUtilizationReport() {
        System.out.println("\nTRUCK UTILIZATION REPORT:");
        System.out.println("----------------------------------------");

        for (Truck truck : trucks) {
            Truck.TruckType type = truck.getTruckType();
            double weightUtilization = (truck.getTotalWeight() / type.getWeightLimit()) * 100;
            double volumeUtilization = (truck.getTotalVolume() / type.getVolumeLimit()) * 100;

            System.out.printf("%d:\n", truck.getTruckID());
            System.out.printf("  Weight Utilization: %.2f%% (%s)\n", weightUtilization,
                    getUtilizationBar(weightUtilization));
            System.out.printf("  Volume Utilization: %.2f%% (%s)\n", volumeUtilization,
                    getUtilizationBar(volumeUtilization));
            System.out.println();
        }
    }

    /**
     * Helper method to create a visual utilization bar
     */
    private String getUtilizationBar(double percentage) {
        int filledBlocks = (int) (percentage / 10);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filledBlocks ? "█" : "░");
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Clears all assignments (resets trucks to empty state)
     */
    public void clearAllAssignments() {
        for (Truck truck : trucks) {
            truck.clearPackages();
        }
        System.out.println("All assignments cleared.");
    }

    /**
     * Gets assignment statistics
     */
    public void printAssignmentStatistics() {
        System.out.println("\nASSIGNMENT STATISTICS:");
        System.out.println("----------------------------------------");

        Map<Truck.TruckType, Integer> typeCount = new HashMap<>();
        Map<Truck.TruckType, Integer> assignedPackagesByType = new HashMap<>();

        for (Truck truck : trucks) {
            typeCount.put(truck.getTruckType(), typeCount.getOrDefault(truck.getTruckType(), 0) + 1);
            assignedPackagesByType.put(truck.getTruckType(),
                    assignedPackagesByType.getOrDefault(truck.getTruckType(), 0) + truck.getPackageCount());
        }

        for (Truck.TruckType type : Truck.TruckType.values()) {
            int truckCount = typeCount.getOrDefault(type, 0);
            int packageCount = assignedPackagesByType.getOrDefault(type, 0);
            System.out.printf("%s Trucks: %d trucks with %d packages (Avg: %.2f packages/truck)\n",
                    type, truckCount, packageCount,
                    truckCount > 0 ? (double) packageCount / truckCount : 0);
        }
    }
}
