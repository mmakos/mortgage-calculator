package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @param number          installment number (from 1). Number 0 is special installment paid on payment (no capital and interest, just insurances, provision and other initial costs).
 * @param capital         capital to pay in current installment. For installment 0 it's other initial costs.
 * @param interest        interest to pay in current installment. For installment 0 it's provision.
 * @param sup2percent     supplement from taxpayers under the "safe 2% mortgage" program
 * @param estateInsurance estate insurance
 * @param lifeInsurance   life insurance (if not null)
 * @param excess          excess
 * @param date            installment pay day
 * @param capitalLeft     capital left to pay after this installment was paid
 * @param margin          margin with which current installment is calculated
 * @param installmentType type of installment (EQUAL or DECREASING)
 * @param loanParams      all mortgage params
 */
public record Installment(int number, double capital, double interest, double sup2percent,
                          double estateInsurance, double lifeInsurance, double excess, LocalDate date,
                          double capitalLeft, double margin, InstallmentType installmentType, LoanParams loanParams)
        implements Iterable<Installment> {

  public static Installment initial(LoanParams loanParams) {
    return new Installment(0,
            loanParams.general().otherCosts(),
            loanParams.general().provision() * loanParams.general().loanValue(),
            0.,
            getEstateInsurance(0, loanParams),
            getLifeInsurance(0, loanParams),
            0.,
            loanParams.general().paymentDate(),
            loanParams.general().loanValue(),
            0.,
            loanParams.getInstallmentType(0),
            loanParams);
  }

  public double capitalAndInterest() {
    return capital + interest;
  }

  public double sumTo2Percent() {
    return capitalAndInterest() - sup2percent;
  }

  public double sumToInsurances() {
    return sumTo2Percent() + estateInsurance + lifeInsurance;
  }

  public double sumAll() {
    return sumToInsurances() + excess;
  }

  public double getRate() {
    return margin + loanParams.general().baseRate();
  }

  public Iterator<Installment> iterator() {
    return new Iterator<>() {
      Installment installment = Installment.this;

      @Override
      public boolean hasNext() {
        return installment.number + 1 <= installment.loanParams.general().loanInstallments() && installment.capitalLeft > 0.;
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
    if (capitalLeft <= 0) {
      throw new NoSuchElementException("Cannot calculate " + installmentNumber +
              " installment because there is no capital left");
    }
    if (installmentNumber > loanParams().general().loanInstallments()) {
      throw new NoSuchElementException("Cannot calculate " + installmentNumber +
              " installment because installments count is " + loanParams.general().loanInstallments());
    }

    LocalDate newDate = loanParams.general().getInstallmentDate(installmentNumber);
    double yearFactor = DateUtils.getYearFactor(date, newDate);
    InstallmentType newType = loanParams.getInstallmentType(installmentNumber);
    double newMargin = loanParams.getMargin(installmentNumber);
    double yearRate = newMargin + loanParams.general().baseRate();
    double rate = yearFactor * yearRate;
    double[] newCapitalAndInterest = getNextCapitalAndInterest(yearRate, rate, newType);
    double sup2percent = loanParams.loan2Percent() != null && installmentNumber <= 120 ?
            capitalLeft * (loanParams.loan2Percent().rate() - .02) / 12. : 0.;
    double newCapitalLeft = capitalLeft - newCapitalAndInterest[0];
    double newEstateInsurance = getEstateInsurance(installmentNumber, loanParams);
    double newLifeInsurance = getLifeInsurance(installmentNumber, loanParams);
    double newExcess = getExcess(installmentNumber,
            newCapitalAndInterest[0] + newCapitalAndInterest[1] - sup2percent + newEstateInsurance + newLifeInsurance,
            loanParams.excesses());
    newExcess = Math.min(newExcess, newCapitalLeft);

    return new Installment(
            installmentNumber,
            newCapitalAndInterest[0],
            newCapitalAndInterest[1],
            sup2percent,
            newEstateInsurance,
            newLifeInsurance,
            newExcess,
            newDate,
            newCapitalLeft - newExcess,
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

  private static double getExcess(int number, double sumInstallment, ExcessesParams excesses) {
    if (excesses == null || number < excesses.from()) return 0.;
    if (excesses.toValue()) {
      return Math.max(excesses.value() - sumInstallment, 0.);
    }
    if (excesses.period() == TimePeriod.MONTH || number % 12 == excesses.from() % 12) {
      return excesses.value();
    } else {
      return 0.;
    }
  }

  private static double getEstateInsurance(int number, LoanParams loanParams) {
    // Last installment - no insurance
    if (number == loanParams.general().loanInstallments()) return 0.;
    EstateInsuranceParams estateInsurance = loanParams.estateInsurance();
    if (estateInsurance.period() == TimePeriod.MONTH) {
      return estateInsurance.value() * estateInsurance.amount() / 12.;
    } else if (number % 12 == 0) {
      return estateInsurance.value() * estateInsurance.amount();
    }
    return 0.;
  }

  private static double getLifeInsurance(int number, LoanParams loanParams) {
    // Last installment - no insurance
    LifeInsuranceParams lifeInsurance = loanParams.lifeInsurance();
    if (lifeInsurance == null || number >= loanParams.general().loanInstallments()) return 0.;
    if (lifeInsurance.inAdvancePeriod() > 0) {
      if (number == 0) {
        return lifeInsurance.inAdvanceValue() * lifeInsurance.amount() * lifeInsurance.inAdvancePeriod() / 12.;
      } else if (number < lifeInsurance.inAdvancePeriod()) {
        return 0.;
      }
    }
    if (lifeInsurance.period() == TimePeriod.MONTH) {
      return lifeInsurance.value() * lifeInsurance.amount() / 12.;
    } else if (number % 12 == lifeInsurance.inAdvancePeriod() % 12) {
      int months = Math.min(12, loanParams.general().loanInstallments() - number);
      return lifeInsurance.value() * lifeInsurance.amount() * months / 12.;
    }
    return 0.;
  }

  @Override
  public String toString() {
    return String.format("Rata %d\tPłatna w dniu %s\tStopa: %.2f%%\tKapitał: %.2f zł\tOdsetki: %.2f zł\tDopłata: %.2f zł\tOdsetki po dopłacie: %.2f zł\tRazem: %.2f zł\tZostało: %.2f zł",
            number, date, (margin + loanParams.general().baseRate()) * 100., capital, interest, sup2percent, interest - sup2percent, capital + interest - sup2percent, capitalLeft);
  }
}
