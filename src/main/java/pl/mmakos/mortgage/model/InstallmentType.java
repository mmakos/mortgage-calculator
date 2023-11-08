package pl.mmakos.mortgage.model;

import lombok.RequiredArgsConstructor;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;

@RequiredArgsConstructor
public enum InstallmentType {
  DECREASING(BUNDLE.getString("installment.decreasing")),
  EQUAL(BUNDLE.getString("installment.equal"));

  private final String string;

  @Override
  public String toString() {
    return string;
  }
}
