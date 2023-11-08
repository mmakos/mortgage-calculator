package pl.mmakos.mortgage.model;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;

public record ExplainedValue<T>(T value, String explanation) {
  public static <T> ExplainedValue<T> of(T value) {
    return new ExplainedValue<>(value, null);
  }

  public static <T> ExplainedValue<T> of(T value, String explanationKey, Object... args) {
    if (args.length == 0) {
      return new ExplainedValue<>(value, BUNDLE.getString(explanationKey));
    } else {
      return new ExplainedValue<>(value, String.format(BUNDLE.getString(explanationKey), args));
    }
  }

  public ExplainedValue<T> with(String explanationKey) {
    return new ExplainedValue<>(value, BUNDLE.getString(explanationKey));
  }
}
