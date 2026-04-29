import java.util.Date;
public class SpecPack extends Pack {
    public SpecPack(int packageID,
                    String receiverCompany,
                    String deliveryZone,
                    Date deliveryDate,
                    double weight,
                    double volume,
                    int timeDeadline) {

        super(
                packageID,
                receiverCompany,
                deliveryZone,
                deliveryDate,
                weight,
                volume,
                DeliveryType.SPECIAL,
                timeDeadline
        );
    }
    
    @Override
    public void setTimeDeadline(int timeDeadline) {
        if (timeDeadline < 9 || timeDeadline > 16) {
            throw new IllegalArgumentException(
                    "Special package deadline must be between 9 and 16."
            );
        }

        super.setTimeDeadline(timeDeadline);
    }

    /**
     * Overrides toString method
     */
    @Override
    public String toString() {
        return "SpecPack{" +
                "packageID=" + getPackageID() +
                ", receiverCompany='" + getReceiverCompany() + '\'' +
                ", deliveryZone='" + getDeliveryZone() + '\'' +
                ", deliveryDate=" + getDeliveryDate() +
                ", weight=" + getWeight() +
                ", volume=" + getVolume() +
                ", deliveryType=" + getDeliveryType() +
                ", timeDeadline=" + getTimeDeadline() +
                '}';
    }
}