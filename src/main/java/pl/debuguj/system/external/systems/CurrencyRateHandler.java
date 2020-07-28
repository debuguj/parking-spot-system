package pl.debuguj.system.external.systems;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

@Service
public class CurrencyRateHandler {

    @Value("${currency.rate.default}")
    String defaultCurrencyRate;

    public CurrencyRate getCurrencyRate() {
        if (!defaultCurrencyRate.isEmpty()) {
            final BigDecimal rate = new BigDecimal(defaultCurrencyRate);
            return new CurrencyRate(rate, Currency.getInstance(new Locale("pl", "PL")));
        }
        return CurrencyRate.PLN;
    }

}