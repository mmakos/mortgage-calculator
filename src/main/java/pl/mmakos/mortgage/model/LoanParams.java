package pl.mmakos.mortgage.model;

import java.util.Arrays;

@SuppressWarnings("java:S6218")
public record LoanParams(GeneralLoanParams general,
                         EstateInsuranceParams estateInsurance,
                         LifeInsuranceParams lifeInsurance,
                         PromotionParams[] promotion,
                         Loan2PercentParams loan2Percent,
                         ExcessesParams excesses) {
  public ExplainedValue<Double> getMargin(int number) {
    return PromotionParams.promotionFor(number, promotion)
            .map(PromotionParams::margin)
            .map(m -> ExplainedValue.of(m, "explanation.margin.promotion"))
            .orElseGet(() -> ExplainedValue.of(general.margin(), "explanation.margin"));
  }

  public ExplainedValue<InstallmentType> getInstallmentType(int number) {
    if (loan2Percent == null) {
      return ExplainedValue.of(general.installmentType());
    }
    return number <= 120 ?
            ExplainedValue.of(InstallmentType.DECREASING, "explanation.installmentType.loan2percent") :
            ExplainedValue.of(general.installmentType(), "explanation.installmentType.loan2percent.after");
  }

  @Override
  public String toString() {
    return "LoanParams{" +
            "\tgeneral=" + general + "\n" +
            "\testateInsurance=" + estateInsurance + "\n" +
            "\tlifeInsurance=" + lifeInsurance + "\n" +
            "\tpromotion=" + Arrays.toString(promotion) + "\n" +
            "\tloan2Percent=" + loan2Percent + "\n" +
            "\texcesses=" + excesses + "\n" +
            '}';
  }
}
