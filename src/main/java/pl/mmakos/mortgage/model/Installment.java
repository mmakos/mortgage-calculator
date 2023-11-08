package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

public record Installment(int number, double capital, double interest, double sup2percent, double estateInsurance,
                          double lifeInsurance, LocalDate date, double capitalLeft, double margin,
                          InstallmentType installmentType, LoanParams loanParams) implements Iterable<Installment> {
  public static Installment initial(LoanParams loanParams) {
    return new Installment(0, 0., 0., 0., 0., 0.,
            loanParams.general().paymentDate(), loanParams.general().loanValue(), 0.,
            InstallmentType.EQUAL, loanParams);
  }

  public Iterator<Installment> iterator() {
    return new Iterator<>() {
      Installment installment = Installment.this;

      @Override
      public boolean hasNext() {
        return installment.number + 1 <= installment.loanParams.general().loanInstallments();
      }

      @Override
      @SuppressWarnings("java:S1121")
      public Installment next() {
        return installment = installment.next();
      }
    };
  }

  public Installment next() {
    int installmentNumber = number + 1;
    if (installmentNumber > loanParams().general().loanInstallments()) {
      throw new NoSuchElementException("Cannot calculate " + installmentNumber +
              " installment because installments count is " + loanParams.general().loanInstallments());
    }

    LocalDate newDate = loanParams.general().getInstallmentDate(installmentNumber);
    double yearFactor = DateUtils.getYearFactor(date, newDate);
    InstallmentType newType = loanParams.getInstallmentType(installmentNumber);
    double newMargin = loanParams.getMargin(number);
    double yearRate = newMargin + loanParams.general().baseRate();
    double rate = yearFactor * yearRate;
    double[] newCapitalAndInterest = getNextCapitalAndInterest(yearRate, rate, newType);
    double sup2percent = loanParams.loan2Percent() != null && number <= 120 ?
            capitalLeft * (loanParams.loan2Percent().rate() - .02) / 12. : 0.;

    return new Installment(
            installmentNumber,
            newCapitalAndInterest[0],
            newCapitalAndInterest[1],
            sup2percent,
            0.,
            0.,
            newDate,
            capitalLeft - newCapitalAndInterest[0],
            newMargin,
            newType,
            loanParams
    );
  }

  private double[] getNextCapitalAndInterest(double yearRate, double rate, InstallmentType type) {
    int installmentsLeft = loanParams.general().loanInstallments() - number;
    if (type == InstallmentType.EQUAL) {
      rate = yearRate / 12;
      double interest = capitalLeft * rate;
      double p1n = Math.pow(1 + rate, installmentsLeft);
      return new double[]{
              capitalLeft * p1n * rate / (p1n - 1) - interest,
              interest
      };
    } else {
      return new double[]{
              capitalLeft / installmentsLeft,
              capitalLeft * rate
      };
    }
  }

  @Override
  public String toString() {
    return String.format("Rata %d\tPłatna w dniu %s\tStopa: %.2f%%\tKapitał: %.2f zł\tOdsetki: %.2f zł\tDopłata: %.2f zł\tOdsetki po dopłacie: %.2f zł\tRazem: %.2f zł\tZostało: %.2f zł",
            number, date, (margin + loanParams.general().baseRate()) * 100., capital, interest, sup2percent, interest - sup2percent, capital + interest - sup2percent, capitalLeft);
  }
}
