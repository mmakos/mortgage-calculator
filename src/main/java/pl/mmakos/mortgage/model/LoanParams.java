package pl.mmakos.mortgage.model;

import java.util.Arrays;

@SuppressWarnings("java:S6218")
public record LoanParams(GeneralLoanParams general,
                         EstateInsuranceParams estateInsurance,
                         LifeInsuranceParams lifeInsurance,
                         PromotionParams[] promotion,
                         Loan2PercentParams loan2Percent,
                         ExcessesParams excesses) {
  public double getMargin(int number) {
    return PromotionParams.promotionFor(number, promotion)
            .map(PromotionParams::margin)
            .orElse(general.margin());
  }

  public InstallmentType getInstallmentType(int number) {
    if (loan2Percent == null) {
      return general.installmentType();
    }
    return number <= 120 ? InstallmentType.DECREASING : general.installmentType();
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
