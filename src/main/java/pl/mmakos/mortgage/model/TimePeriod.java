package pl.mmakos.mortgage.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;

@Getter
@RequiredArgsConstructor
public enum TimePeriod {
  YEAR(BUNDLE.getString("time.plural.year"),
          BUNDLE.getString("time.singular.year"),
          BUNDLE.getString("time.adverb.year")),
  MONTH(BUNDLE.getString("time.plural.month"),
          BUNDLE.getString("time.singular.month"),
          BUNDLE.getString("time.adverb.month"));

  private final String plural;
  private final String singular;
  private final String adverb;

  public double convert(double value, TimePeriod to) {
    if (this == to) return value;
    return this == YEAR ? value * 12 : value / 12;
  }

  public int convert(int value, TimePeriod to) {
    if (this == to) return value;
    return this == YEAR ? value * 12 : value / 12;
  }

  @Override
  public String toString() {
    return plural;
  }
}
