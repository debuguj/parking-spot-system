package pl.debuguj.system.spot;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Getter
public class CurrencyRate {

    private final BigDecimal rate;
    private final Currency currency;

    public CurrencyRate(BigDecimal rate, Currency currency) {
        Objects.requireNonNull(rate, "Rate cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        this.rate = rate;
        this.currency = currency;
    }

}