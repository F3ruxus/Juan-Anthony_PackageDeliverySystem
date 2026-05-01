import java.io.*;
import java.util.*;

public class main {
    private static final String LOG_FILE = "log.txt";
    private static final String DELIVERIES_FILE = "deliveries.txt";
    private static PrintWriter logWriter;

    public static void main(String[] args) {
        PackageDeliverySystem system = new PackageDeliverySystem();
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Initialize log file writer
            logWriter = new PrintWriter(new FileWriter(LOG_FILE, false)); // false to overwrite
            
            // Prompt for input file name
            System.out.print("Enter input file name: ");
            String inputFileName = scanner.nextLine().trim();
            
            if (inputFileName.isEmpty()) {
                logError("Error: Input file name cannot be empty");
                System.err.println("Error: Input file name cannot be empty");
                return;
            }
            
            // Read data from input file
            readDataFromFile(inputFileName, system);
            
            // Assign packages to trucks
            system.assignPackagesToTrucks();
            
            // Output to screen
            outputToScreen(system);
            
            // Output to deliveries.txt
            outputToDeliveries(system);
            
            System.out.println("\nDelivery system complete. Check deliveries.txt and log.txt for details.");
            
        } catch (IOException e) {
            logError("IO Error: " + e.getMessage());
            System.err.println("IO Error: " + e.getMessage());
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
            scanner.close();
        }
    }

    /**
     * Reads data from input file and populates the delivery system
     */
    private static void readDataFromFile(String inputFileName, PackageDeliverySystem system) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                // Check if it's a truck line or package line
                if (line.startsWith("TRUCK")) {
                    parseTruckLine(line, system, lineNumber);
                } else if (line.startsWith("PACKAGE") || line.startsWith("SPECIAL")) {
                    parsePackageLine(line, system, lineNumber);
                }
            }
        } catch (FileNotFoundException e) {
            logError("File not found: " + inputFileName);
            System.err.println("Error: File not found: " + inputFileName);
        } catch (IOException e) {
            logError("Error reading file: " + e.getMessage());
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Parses a truck line from the input file
     * Format: TRUCK <truckID> <type> (SMALL, MEDIUM, or LARGE)
     */
    private static void parseTruckLine(String line, PackageDeliverySystem system, int lineNumber) {
        try {
            String[] parts = line.split("\\s+");
            if (parts.length < 3) {
                throw new TruckException("Invalid truck format at line " + lineNumber + ": " + line);
            }
            
            int truckID = Integer.parseInt(parts[1]);
            String typeStr = parts[2].toUpperCase();
            
            Truck.TruckType type = Truck.TruckType.valueOf(typeStr);
            Truck truck = new Truck(truckID, type);
            system.addTruck(truck);
            
        } catch (TruckException e) {
            logError(e.getMessage());
        } catch (NumberFormatException e) {
            logError("Invalid truck ID at line " + lineNumber + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logError("Invalid truck type at line " + lineNumber + ": " + e.getMessage());
        }
    }

    /**
     * Parses a package line from the input file
     * Format: PACKAGE <id> <company> <zone> <month> <day> <year> <weight> <volume>
     *         SPECIAL <id> <company> <zone> <month> <day> <year> <weight> <volume> <deadline>
     */
    private static void parsePackageLine(String line, PackageDeliverySystem system, int lineNumber) {
        try {
            String[] parts = line.split("\\s+");
            String packageType = parts[0].toUpperCase();
            
            if (packageType.equals("PACKAGE")) {
                if (parts.length < 9) {
                    throw new PackageException("Invalid package format at line " + lineNumber);
                }
                
                int packageID = Integer.parseInt(parts[1]);
                String company = parts[2];
                String zone = parts[3];
                int month = Integer.parseInt(parts[4]);
                int day = Integer.parseInt(parts[5]);
                int year = Integer.parseInt(parts[6]);
                double weight = Double.parseDouble(parts[7]);
                double volume = Double.parseDouble(parts[8]);
                
                Date deliveryDate = createDate(month, day, year, lineNumber);
                if (deliveryDate != null) {
                    Pack pkg = new Pack(packageID, company, zone, deliveryDate, 
                                       weight, volume, Pack.DeliveryType.REGULAR, 0);
                    system.addPackage(pkg);
                }
                
            } else if (packageType.equals("SPECIAL")) {
                if (parts.length < 10) {
                    throw new SpecialPackageException("Invalid special package format at line " + lineNumber);
                }
                
                int packageID = Integer.parseInt(parts[1]);
                String company = parts[2];
                String zone = parts[3];
                int month = Integer.parseInt(parts[4]);
                int day = Integer.parseInt(parts[5]);
                int year = Integer.parseInt(parts[6]);
                double weight = Double.parseDouble(parts[7]);
                double volume = Double.parseDouble(parts[8]);
                int deadline = Integer.parseInt(parts[9]);
                
                    Date deliveryDate = createDate(month, day, year, lineNumber);
                    if (deliveryDate != null) {
                        SpecPack pkg = new SpecPack(packageID, company, zone, deliveryDate,
                                              weight, volume, deadline);
                        system.addPackage(pkg);
                }
            }
            
        } catch (SpecialPackageException e) {
            logError(e.getMessage());
        } catch (PackageException e) {
            logError(e.getMessage());
        } catch (NumberFormatException e) {
            logError("Invalid number format at line " + lineNumber + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logError("Invalid package data at line " + lineNumber + ": " + e.getMessage());
        }
    }

    /**
     * Helper method to create a Date object with error handling
     */
    private static Date createDate(int month, int day, int year, int lineNumber) {
        try {
            Date date = new Date(month, day, year);
            // Validate the date was set correctly
            if (date.getMonth() == 0 || date.getDay() == 0) {
                throw new DateException("Invalid date at line " + lineNumber + ": " + 
                                       month + "/" + day + "/" + year);
            }
            return date;
        } catch (DateException e) {
            logError(e.getMessage());
            return null;
        }
    }

    /**
     * Outputs summary to screen (number of trucks by size and total truck hours)
     */
    private static void outputToScreen(PackageDeliverySystem system) {
        Map<Truck.TruckType, Integer> truckCounts = new HashMap<>();
        
        for (Truck truck : system.getTrucks()) {
            truckCounts.put(truck.getTruckType(), 
                          truckCounts.getOrDefault(truck.getTruckType(), 0) + 1);
        }
        
        System.out.println("\n========================================");
        System.out.println("     DELIVERY SYSTEM SUMMARY");
        System.out.println("========================================");
        
        // Output truck counts by size
        System.out.println("\nTRUCKS USED BY SIZE:");
        for (Truck.TruckType type : Truck.TruckType.values()) {
            int count = truckCounts.getOrDefault(type, 0);
            System.out.println("  " + type + " Trucks: " + count);
        }
        
        // Output total truck hours
        double totalTruckHours = calculateTotalTruckHours(system);
        System.out.println("\nTOTAL TRUCK HOURS: " + String.format("%.2f", totalTruckHours) + " hours");
        System.out.println("========================================\n");
    }

    /**
     * Calculates total truck hours using the formula:
     * TotalTruckHours = Σ (TypeWeight × DeliveryTime)
     * TypeWeight: SMALL=1, MEDIUM=2, LARGE=3
     */
    private static double calculateTotalTruckHours(PackageDeliverySystem system) {
        double totalHours = 0;
        final double DELIVERY_TIME_PER_PACKAGE = 10.0; // minutes
        
        for (Truck truck : system.getTrucks()) {
            int typeWeight = getTypeWeight(truck.getTruckType());
            int packageCount = truck.getPackages().size();
            double deliveryTimeHours = (packageCount * DELIVERY_TIME_PER_PACKAGE) / 60.0;
            
            totalHours += typeWeight * deliveryTimeHours;
        }
        
        return totalHours;
    }

    /**
     * Helper method to get weight factor for truck type
     */
    private static int getTypeWeight(Truck.TruckType type) {
        switch (type) {
            case SMALL:
                return 1;
            case MEDIUM:
                return 2;
            case LARGE:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Outputs delivery details to deliveries.txt file
     */
    private static void outputToDeliveries(PackageDeliverySystem system) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DELIVERIES_FILE))) {
            writer.println("========================================");
            writer.println("     DELIVERY MANIFEST");
            writer.println("========================================\n");
            
            for (Truck truck : system.getTrucks()) {
                writer.println("TRUCK " + truck.getTruckID() + " - " + truck.getTruckType());
                writer.println("  Capacity: Weight=" + truck.getWeightLimit() + 
                             " kg, Volume=" + truck.getVolumeLimit() + " m³");
                writer.println("  Current Load: Weight=" + String.format("%.2f", truck.getTotalWeight()) + 
                             " kg, Volume=" + String.format("%.2f", truck.getTotalVolume()) + " m³");
                writer.println("  Total Packages: " + truck.getPackages().size());
                
                if (!truck.getPackages().isEmpty()) {
                    writer.println("\n  PACKAGES TO DELIVER (in order):");
                    int packageNumber = 1;
                    for (Pack pkg : truck.getPackages()) {
                        writer.println("\n    Package #" + packageNumber + ":");
                        writer.println("      ID: " + pkg.getPackageID());
                        writer.println("      Receiver: " + pkg.getReceiverCompany());
                        writer.println("      Zone: " + pkg.getDeliveryZone());
                        writer.println("      Delivery Date: " + pkg.getDeliveryDate());
                        writer.println("      Weight: " + String.format("%.2f", pkg.getWeight()) + " kg");
                        writer.println("      Volume: " + String.format("%.2f", pkg.getVolume()) + " m³");
                        writer.println("      Type: " + pkg.getDeliveryType());
                        if (pkg.getDeliveryType() == Pack.DeliveryType.SPECIAL) {
                            writer.println("      Time Deadline: " + pkg.getTimeDeadline() + ":00");
                        }
                        packageNumber++;
                    }
                }
                writer.println("\n----------------------------------------\n");
            }
            
            writer.println("========================================");
            writer.println("Total Packages Delivered: " + system.getPackages().size());
            writer.println("Total Truck Hours: " + String.format("%.2f", calculateTotalTruckHours(system)));
            writer.println("========================================");
            
        } catch (IOException e) {
            logError("Error writing to deliveries.txt: " + e.getMessage());
            System.err.println("Error writing to deliveries.txt: " + e.getMessage());
        }
    }

    /**
     * Logs an error message to log.txt file
     */
    private static void logError(String message) {
        if (logWriter != null) {
            logWriter.println("[ERROR] " + new Date() + " - " + message);
            logWriter.flush();
        }
    }
}
