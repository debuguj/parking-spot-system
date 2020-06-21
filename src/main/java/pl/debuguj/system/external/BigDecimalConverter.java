package pl.debuguj.system.external;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationPropertiesBinding
public class BigDecimalConverter implements Converter<String, BigDecimal> {
    @Override
    public BigDecimal convert(String source) {
        if (null == source || source.isEmpty()) {
            return null;
        }
        //TODO: validation
        return new BigDecimal(source);
    }
}