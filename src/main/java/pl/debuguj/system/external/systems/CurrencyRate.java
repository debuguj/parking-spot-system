package pl.debuguj.system.external.systems;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

@Getter
public final class CurrencyRate {

    private final BigDecimal rate;
    private final Currency currency;

    public static final CurrencyRate PLN;
    public static final CurrencyRate USD;
    public static final CurrencyRate CHF;
    public static final CurrencyRate JPN;

    static {
        PLN = new CurrencyRate(new BigDecimal("1.0"), Currency.getInstance(new Locale("pl", "PL")));
        USD = new CurrencyRate(new BigDecimal("4.0"), Currency.getInstance(Locale.US));
        CHF = new CurrencyRate(new BigDecimal("5.4"), Currency.getInstance(Locale.UK));
        JPN = new CurrencyRate(new BigDecimal("0.03"), Currency.getInstance(Locale.JAPAN));
    }

    public CurrencyRate(final BigDecimal rate, final Currency currency) {
        Objects.requireNonNull(rate, "Rate cannot be null");
        if (BigDecimal.ZERO.compareTo(rate) > 0) {
            throw new ArithmeticException("Rate must be higher than 0");
        }
        Objects.requireNonNull(currency, "Currency cannot be null");
        this.rate = rate;
        this.currency = currency;
    }

    

}