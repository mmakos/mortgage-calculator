package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.time.LocalDate;

public record GeneralLoanParams(int loanValue, int loanInstallments, double margin, double baseRate, double provision,
                                int otherCosts, InstallmentType installmentType, LocalDate paymentDate,
                                LocalDate firstInstallmentDate, boolean installmentsOnlyInWorkDays) {
  public LocalDate getInstallmentDate(int number) {
    if (number < 1) throw new IllegalArgumentException("Installment rate must be greater than 0");
    LocalDate installmentDate = firstInstallmentDate.plusMonths(number - 1L);

    if (installmentsOnlyInWorkDays) {
      while (DateUtils.isHoliday(installmentDate)) {
        installmentDate = installmentDate.plusDays(1);
      }
    }
    return installmentDate;
  }
}
