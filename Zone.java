/**
 * Zone class representing delivery zones
 */
public class Zone {
    private String zoneId;
    private String zoneName;
    private double baseDeliveryTime; // in hours

    /**
     * Constructor
     */
    public Zone(String zoneId, String zoneName, double baseDeliveryTime) {
        if (zoneId == null || zoneId.isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty");
        }
        if (zoneName == null || zoneName.isEmpty()) {
            throw new IllegalArgumentException("Zone name cannot be null or empty");
        }
        if (baseDeliveryTime <= 0) {
            throw new IllegalArgumentException("Base delivery time must be positive");
        }
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.baseDeliveryTime = baseDeliveryTime;
    }

    // Getters
    public String getZoneId() { return zoneId; }
    public String getZoneName() { return zoneName; }
    public double getBaseDeliveryTime() { return baseDeliveryTime; }

    @Override
    public String toString() {
        return String.format("Zone{%s: %s, time: %.2f hours}", zoneId, zoneName, baseDeliveryTime);
    }
}
