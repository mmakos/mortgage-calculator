package pl.mmakos.mortgage.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;
import static pl.mmakos.mortgage.MortgageCalculator.CURRENCY_FORMAT;

public record Summary(ExplainedValue<Double> sum,
                      ExplainedValue<Double> cost,
                      ExplainedValue<Double> interestAnd2percentSum,
                      ExplainedValue<Double> rrso,
                      ExplainedValue<LocalDate> lastDate,
                      ExplainedValue<String> period,
                      ExplainedValue<Double> capitalSum,
                      ExplainedValue<Double> interestSum,
                      ExplainedValue<Double> sup2percentSum,
                      ExplainedValue<Double> estateInsuranceSum,
                      ExplainedValue<Double> lifeInsuranceSum,
                      ExplainedValue<Double> excessesSum) {

  public static Summary fromInstallments(Installment initial, List<Installment> installments) {
    double capitalSum = calcCapitalSum(installments);
    double interestSum = calcInterestSum(installments);
    double sup2percentSum = calcSup2percentSum(installments);
    double interestAnd2percentSum = interestSum - sup2percentSum;
    double estateInsuranceSum = calcEstateInsuranceSum(installments) + initial.estateInsurance().value();
    double lifeInsuranceSum = calcLifeInsuranceSum(installments) + initial.lifeInsurance().value();
    double excessesSum = calcExcessesSum(installments);

    double cost = interestAnd2percentSum + estateInsuranceSum + lifeInsuranceSum + initial.interest().value() + initial.capital().value();
    double loanValue = initial.loanParams().general().loanValue();
    double sum = loanValue + cost;
    double rrso = 0.0512; // TODO
    LocalDate lastDate = installments.get(installments.size() - 1).date().value();
    Period period = initial.date().value().until(lastDate);

    return new Summary(
            ExplainedValue.of(sum, "explanation.summary.sum",
                    CURRENCY_FORMAT.format(cost), CURRENCY_FORMAT.format(loanValue)),
            ExplainedValue.of(cost, "explanation.summary.cost",
                    CURRENCY_FORMAT.format(initial.interest().value()), CURRENCY_FORMAT.format(initial.capital().value()),
                    CURRENCY_FORMAT.format(capitalSum), CURRENCY_FORMAT.format(interestSum),
                    CURRENCY_FORMAT.format(sup2percentSum), CURRENCY_FORMAT.format(estateInsuranceSum),
                    CURRENCY_FORMAT.format(lifeInsuranceSum), CURRENCY_FORMAT.format(excessesSum)
                    ),
            ExplainedValue.of(interestAnd2percentSum, "explanation.summary.interestAnd2percentSum",
                    CURRENCY_FORMAT.format(interestSum), CURRENCY_FORMAT.format(sup2percentSum)),
            ExplainedValue.of(rrso, "explanation.summary.rrso"),
            ExplainedValue.of(lastDate, "explanation.summary.lastDate"),
            ExplainedValue.of(formatPeriod(period), "explanation.summary.period", lastDate, initial.date()),
            ExplainedValue.of(capitalSum),
            ExplainedValue.of(interestSum),
            ExplainedValue.of(sup2percentSum),
            ExplainedValue.of(estateInsuranceSum),
            ExplainedValue.of(lifeInsuranceSum),
            ExplainedValue.of(excessesSum)
    );
  }

  private static double calcCapitalSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::capital)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static double calcInterestSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::interest)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static double calcSup2percentSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::sup2percent)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static double calcEstateInsuranceSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::estateInsurance)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static double calcLifeInsuranceSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::lifeInsurance)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static double calcExcessesSum(List<Installment> installments) {
    return installments.stream()
            .map(Installment::excess)
            .mapToDouble(ExplainedValue::value)
            .sum();
  }

  private static String formatPeriod(Period period) {
    return String.format("%d %s %d %s %d %s",
            period.getYears(), BUNDLE.getString("period.years." + getForm(period.getYears())),
            period.getMonths(), BUNDLE.getString("period.months." + getForm(period.getMonths())),
            period.getDays(), BUNDLE.getString("period.days." + getForm(period.getDays())));
  }

  private static String getForm(int number) {
    if (number == 1) {
      return "singular";
    } else if (number > 1 && number <= 4) {
      return "plural.nominative";
    } else  {
      return "plural.genitive";
    }
  }
}
