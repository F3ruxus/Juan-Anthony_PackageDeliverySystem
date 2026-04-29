import java.util.Date;
public class Pack implements Comparable<Pack> {
    public enum DeliveryType {
        REGULAR,
        SPECIAL
    }

    // Attributes
    private int packageID;
    private String receiverCompany;
    private String deliveryZone;
    private Date deliveryDate;
    private double weight;
    private double volume;
    private DeliveryType deliveryType;
    private int timeDeadline;


    public Pack(int packageID,
                String receiverCompany,
                String deliveryZone,
                Date deliveryDate,
                double weight,
                double volume,
                DeliveryType deliveryType,
                int timeDeadline) {

        if (packageID <= 0) {
            throw new IllegalArgumentException("Package ID must be a positive integer.");
        }

        this.packageID = packageID;

        setReceiverCompany(receiverCompany);
        setDeliveryZone(deliveryZone);
        setDeliveryDate(deliveryDate);
        setWeight(weight);
        setVolume(volume);
        setDeliveryType(deliveryType);
        setTimeDeadline(timeDeadline);
    }

    // Getters

    public int getPackageID() {
        return packageID;
    }

    public String getReceiverCompany() {
        return receiverCompany;
    }

    public String getDeliveryZone() {
        return deliveryZone;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public double getWeight() {
        return weight;
    }

    public double getVolume() {
        return volume;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public int getTimeDeadline() {
        return timeDeadline;
    }

    // Setters

    public void setReceiverCompany(String receiverCompany) {
        if (receiverCompany == null || !receiverCompany.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException(
                    "Receiver company must contain alphabetic characters only."
            );
        }
        this.receiverCompany = receiverCompany;
    }

    public void setDeliveryZone(String deliveryZone) {
        if (deliveryZone == null || !deliveryZone.matches("[A-Z][1-9]")) {
            throw new IllegalArgumentException(
                    "Delivery zone must be in format A1 to Z9."
            );
        }
        this.deliveryZone = deliveryZone;
    }

    public void setDeliveryDate(Date deliveryDate) {
        if (deliveryDate == null) {
            throw new IllegalArgumentException("Delivery date cannot be null.");
        }
        this.deliveryDate = deliveryDate;
    }

    public void setWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be greater than 0."
            );
        }
        this.weight = weight;
    }

    public void setVolume(double volume) {
        if (volume <= 0) {
            throw new IllegalArgumentException(
                    "Volume must be greater than 0."
            );
        }
        this.volume = volume;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        if (deliveryType == null) {
            throw new IllegalArgumentException(
                    "Delivery type cannot be null."
            );
        }
        this.deliveryType = deliveryType;
    }

    public void setTimeDeadline(int timeDeadline) {
        if (deliveryType == DeliveryType.SPECIAL) {
            if (timeDeadline < 9 || timeDeadline > 16) {
                throw new IllegalArgumentException(
                        "Time deadline for SPECIAL packages must be between 9 and 16."
                );
            }
            this.timeDeadline = timeDeadline;
        } else {
            // REGULAR packages do not use timeDeadline, so set timeDeadline to 0
            this.timeDeadline = 0;
        }
    }

    @Override
    public int compareTo(Pack other) {
        int dateComparison = this.deliveryDate.compareTo(other.deliveryDate);

        if (dateComparison != 0) {
            return dateComparison;
        }

        if (this.deliveryType == DeliveryType.SPECIAL &&
            other.deliveryType == DeliveryType.SPECIAL) {
            return Integer.compare(this.timeDeadline, other.timeDeadline);
        }

        return Integer.compare(this.packageID, other.packageID);
    }

    @Override
    public String toString() {
        return "Pack{" +
                "packageID=" + packageID +
                ", receiverCompany='" + receiverCompany + '\'' +
                ", deliveryZone='" + deliveryZone + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", weight=" + weight +
                ", volume=" + volume +
                ", deliveryType=" + deliveryType +
                ", timeDeadline=" + timeDeadline +
                '}';
    }
}