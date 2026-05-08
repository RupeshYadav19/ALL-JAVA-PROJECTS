import java.util.HashMap;
import java.util.Map;

/**
 * Stores currency exchange rates and provides conversion logic.
 * Base currency is USD for internal calculations.
 */
public class ExchangeRate {
    private Map<String, Double> rates;

    public ExchangeRate() {
        rates = new HashMap<>();
        initializeRates();
    }

    private void initializeRates() {
        // Exchange rates relative to 1 USD (Approximate values as of search context)
        rates.put("USD - US Dollar", 1.0);
        rates.put("INR - Indian Rupee", 83.25);
        rates.put("EUR - Euro", 0.92);
        rates.put("GBP - British Pound", 0.78);
        rates.put("JPY - Japanese Yen", 149.50);
        rates.put("AUD - Australian Dollar", 1.52);
        rates.put("CAD - Canadian Dollar", 1.35);
        rates.put("CNY - Chinese Yuan", 7.19);
        rates.put("AED - UAE Dirham", 3.67);
        rates.put("CHF - Swiss Franc", 0.88);
        rates.put("SGD - Singapore Dollar", 1.34);
        rates.put("NZD - New Zealand Dollar", 1.63);
        rates.put("ZAR - South African Rand", 18.80);
        rates.put("BRL - Brazilian Real", 4.95);
        rates.put("HKD - Hong Kong Dollar", 7.82);
        rates.put("SAR - Saudi Riyal", 3.75);
        rates.put("RUB - Russian Ruble", 91.50);
        rates.put("KRW - South Korean Won", 1320.0);
        rates.put("TRY - Turkish Lira", 31.80);
        rates.put("MXN - Mexican Peso", 16.80);
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Invalid currency selected.");
        }

        // Convert input amount to USD first, then to target currency
        double amountInUSD = amount / rates.get(fromCurrency);
        return amountInUSD * rates.get(toCurrency);
    }

    public String[] getSupportedCurrencies() {
        return rates.keySet().toArray(new String[0]);
    }
}
