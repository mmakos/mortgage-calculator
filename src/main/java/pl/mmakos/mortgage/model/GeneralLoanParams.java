package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.time.LocalDate;

public record GeneralLoanParams(double loanValue, int loanInstallments, double margin, double baseRate, double provision,
                                double otherCosts, InstallmentType installmentType, LocalDate paymentDate,
                                LocalDate firstInstallmentDate, boolean installmentsOnlyInWorkDays) {
  public ExplainedValue<LocalDate> getInstallmentDate(int number) {
    if (number < 1) throw new IllegalArgumentException("Installment rate must be greater than 0");
    if (number == 1) return ExplainedValue.of(firstInstallmentDate);
    LocalDate installmentDate = firstInstallmentDate.plusMonths(number - 1L);
    LocalDate finalDate = firstInstallmentDate.plusMonths(number - 1L);

    if (installmentsOnlyInWorkDays) {
      while (DateUtils.isHoliday(finalDate)) {
        finalDate = finalDate.plusDays(1);
      }
    }
    if (installmentDate.equals(finalDate)) {
      return ExplainedValue.of(finalDate, "explanation.date");
    } else {
      return ExplainedValue.of(finalDate, "explanation.date.workDay", installmentDate);
    }
  }
}
