package pl.debuguj.system.spot;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DriverTypeConverter implements AttributeConverter<DriverType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DriverType driverType) {
        if (driverType == null)
            return null;

        switch (driverType) {
            case REGULAR:
                return 1;
            case VIP:
                return 2;
              default:
                throw new IllegalArgumentException(driverType + " not supported.");
        }
    }

    @Override
    public DriverType convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;

        switch (dbData) {
            case 1:
                return DriverType.REGULAR;
            case 2:
                return DriverType.VIP;
            default:
                throw new IllegalArgumentException(dbData + " not supported.");
        }
    }
}