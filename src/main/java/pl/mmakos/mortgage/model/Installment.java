package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public record Installment(int number, ExplainedValue<Double> capital, ExplainedValue<Double> interest, ExplainedValue<Double> sup2percent,
                          ExplainedValue<Double> estateInsurance, ExplainedValue<Double> lifeInsurance, ExplainedValue<Double> excess, LocalDate date,
                          ExplainedValue<Double> capitalLeft, ExplainedValue<Double> margin, InstallmentType installmentType, LoanParams loanParams)
        implements Iterable<Installment> {

  private static final double MORTGAGE_PCC_3 = 19.;
  private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
  private static final NumberFormat PERCENTAGE_FORMAT = new DecimalFormat("#.###### '%'");

  public static Installment initial(LoanParams loanParams) {
    return new Installment(0,
            ExplainedValue.of(loanParams.general().otherCosts() + MORTGAGE_PCC_3,
                    "panel.paymentParams.otherCosts.exp", CURRENCY_FORMAT.format(loanParams.general().otherCosts())),
            ExplainedValue.of(loanParams.general().provision() * loanParams.general().loanValue(),
                    "panel.paymentParams.provision.exp", CURRENCY_FORMAT.format(loanParams.general().loanValue()),
                    PERCENTAGE_FORMAT.format(loanParams.general().provision() * 100.)),
            ExplainedValue.of(0.),
            getEstateInsurance(0, loanParams),
            getLifeInsurance(0, loanParams),
            ExplainedValue.of(0.),
            loanParams.general().paymentDate(),
            ExplainedValue.of(loanParams.general().loanValue()),
            ExplainedValue.of(0.),
            loanParams.getInstallmentType(0),
            loanParams);
  }

  public ExplainedValue<Double> capitalAndInterest() {
    return ExplainedValue.of(capital.value() + interest.value());
  }

  public ExplainedValue<Double> sumTo2Percent() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value());
  }

  public ExplainedValue<Double> sumToInsurances() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value() + estateInsurance.value() + lifeInsurance.value());
  }

  public ExplainedValue<Double> sumAll() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value() + estateInsurance.value() + lifeInsurance.value() + excess.value());
  }

  public ExplainedValue<Double> getRate() {
    return ExplainedValue.of(margin.value() + loanParams.general().baseRate());
  }

  public Iterator<Installment> iterator() {
    return new Iterator<>() {
      Installment installment = Installment.this;

      @Override
      public boolean hasNext() {
        return installment.number + 1 <= installment.loanParams.general().loanInstallments() && installment.capitalLeft.value() > 0.;
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
    if (capitalLeft.value() <= 0) {
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
            capitalLeft.value() * (loanParams.loan2Percent().rate() - .02) / 12. : 0.;
    double newCapitalLeft = capitalLeft.value() - newCapitalAndInterest[0];
    ExplainedValue<Double> newEstateInsurance = getEstateInsurance(installmentNumber, loanParams);
    ExplainedValue<Double> newLifeInsurance = getLifeInsurance(installmentNumber, loanParams);
    double newExcess = getExcess(installmentNumber,
            newCapitalAndInterest[0] + newCapitalAndInterest[1] - sup2percent + newEstateInsurance.value() + newLifeInsurance.value(),
            loanParams.excesses());
    newExcess = Math.min(newExcess, newCapitalLeft);

    return new Installment(
            installmentNumber,
            ExplainedValue.of(newCapitalAndInterest[0]),
            ExplainedValue.of(newCapitalAndInterest[1]),
            ExplainedValue.of(sup2percent),
            newEstateInsurance,
            newLifeInsurance,
            ExplainedValue.of(newExcess),
            newDate,
            ExplainedValue.of(newCapitalLeft - newExcess),
            ExplainedValue.of(newMargin),
            newType,
            loanParams
    );
  }

  private double[] getNextCapitalAndInterest(double yearRate, double rate, InstallmentType type) {
    int installmentsLeft = loanParams.general().loanInstallments() - number;
    if (type == InstallmentType.EQUAL) {
      rate = yearRate / 12;
      double interest = capitalLeft.value() * rate;
      double p1n = Math.pow(1 + rate, installmentsLeft);
      return new double[]{
              capitalLeft.value() * p1n * rate / (p1n - 1) - interest,
              interest
      };
    } else {
      return new double[]{
              capitalLeft.value() / installmentsLeft,
              capitalLeft.value() * rate
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

  private static ExplainedValue<Double> getEstateInsurance(int number, LoanParams loanParams) {
    // Last installment - no insurance
    if (number == loanParams.general().loanInstallments()) return ExplainedValue.of(0.);
    EstateInsuranceParams estateInsurance = loanParams.estateInsurance();
    if (estateInsurance.period() == TimePeriod.MONTH) {
      return ExplainedValue.of(estateInsurance.value() * estateInsurance.amount() / 12., "panel.paymentParams.estateInsurance.exp.month",
              CURRENCY_FORMAT.format(estateInsurance.amount()), PERCENTAGE_FORMAT.format(estateInsurance.value() * 100.));
    } else if (number % 12 == 0) {
      return ExplainedValue.of(estateInsurance.value() * estateInsurance.amount(), "panel.paymentParams.estateInsurance.exp.year",
              CURRENCY_FORMAT.format(estateInsurance.amount()), PERCENTAGE_FORMAT.format(estateInsurance.value() * 100.));
    }
    return ExplainedValue.of(0.);
  }

  private static ExplainedValue<Double> getLifeInsurance(int number, LoanParams loanParams) {
    // Last installment - no insurance
    LifeInsuranceParams lifeInsurance = loanParams.lifeInsurance();
    if (lifeInsurance == null || number >= loanParams.general().loanInstallments()) return ExplainedValue.of(0.);
    if (lifeInsurance.inAdvancePeriod() > 0) {
      if (number == 0) {
        return ExplainedValue.of(lifeInsurance.inAdvanceValue() * lifeInsurance.amount() * lifeInsurance.inAdvancePeriod() / 12.,
                "panel.paymentParams.lifeInsurance.exp.inAdvance", lifeInsurance.inAdvancePeriod(),
                CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.inAdvanceValue() * 100.));
      } else if (number < lifeInsurance.inAdvancePeriod()) {
        return ExplainedValue.of(0., "panel.paymentParams.lifeInsurance.exp.noDuringAdvancePeriod");
      }
    }
    if (lifeInsurance.period() == TimePeriod.MONTH) {
      return ExplainedValue.of(lifeInsurance.value() * lifeInsurance.amount() / 12., "panel.paymentParams.lifeInsurance.exp.month",
              CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.value() * 100.));
    } else if (number % 12 == lifeInsurance.inAdvancePeriod() % 12) {
      int months = Math.min(12, loanParams.general().loanInstallments() - number);
      return ExplainedValue.of(lifeInsurance.value() * lifeInsurance.amount() * months / 12., "panel.paymentParams.lifeInsurance.exp.year",
              CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.value() * 100.));
    }
    return ExplainedValue.of(0.);
  }
}
