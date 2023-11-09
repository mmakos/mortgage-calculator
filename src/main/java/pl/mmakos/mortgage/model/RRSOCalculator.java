package pl.mmakos.mortgage.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.mmakos.mortgage.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;

import static pl.mmakos.mortgage.MortgageCalculator.CURRENCY_FORMAT;
import static pl.mmakos.mortgage.MortgageCalculator.PERCENT_FORMAT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RRSOCalculator {
  private static final double INIT_RRSO = 0.1;

  public static ExplainedValue<Double> calculateRRSO(List<Installment> installments, double precision) {
    if (installments.isEmpty()) return ExplainedValue.of(0.);
    double left = installments.get(0).loanParams().general().loanValue();
    double currentRRSO = INIT_RRSO;
    double rrsoModifier = currentRRSO / 2;
    boolean toLowInitialRRSO = true;

    do {
      double right = calculateRightEquation(currentRRSO, installments);

      if (Math.abs(right - left) < precision) {
        return ExplainedValue.of(currentRRSO, "explanation.summary.rrso",
                CURRENCY_FORMAT.format(left), installments.size() - 1,
                PERCENT_FORMAT.format(INIT_RRSO * 100.),
                CURRENCY_FORMAT.format(calculateRightEquation(INIT_RRSO, installments)),
                CURRENCY_FORMAT.format(left),
                PERCENT_FORMAT.format(INIT_RRSO / 2. * 100.));
      } else {
        currentRRSO += right > left ? rrsoModifier : -rrsoModifier;
        toLowInitialRRSO &= right > left;
      }
      if (!toLowInitialRRSO) {
        rrsoModifier /= 2;
      }
    } while (true);
  }

  private static double calculateRightEquation(double rrso, List<Installment> installments) {
    return installments.stream()
            .mapToDouble(i -> calculateRightEquationForInstallment(rrso, i))
            .sum();
  }

  private static double calculateRightEquationForInstallment(double rrso, Installment installment) {
    LocalDate startDate = installment.loanParams().general().paymentDate();
    LocalDate date = installment.date().value();
    double time = DateUtils.getYearFactor(startDate, date);
    double installmentValue = installment.sumAll().value();

    return installmentValue / Math.pow(1 + rrso, time);
  }
}
