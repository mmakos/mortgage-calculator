package pl.mmakos.mortgage.model;

import pl.mmakos.mortgage.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
                          ExplainedValue<Double> estateInsurance, ExplainedValue<Double> lifeInsurance, ExplainedValue<Double> excess,
                          ExplainedValue<LocalDate> date, ExplainedValue<Double> capitalLeft, ExplainedValue<Double> margin,
                          ExplainedValue<InstallmentType> installmentType, LoanParams loanParams)
        implements Iterable<Installment> {

  private static final double MORTGAGE_PCC_3 = 19.;
  private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
  private static final NumberFormat PERCENTAGE_FORMAT = new DecimalFormat("#.###### '%'");

  public static Installment initial(LoanParams loanParams) {
    return new Installment(0,
            ExplainedValue.of(loanParams.general().otherCosts() + MORTGAGE_PCC_3,
                    "explanation.otherCosts", CURRENCY_FORMAT.format(loanParams.general().otherCosts())),
            ExplainedValue.of(loanParams.general().provision() * loanParams.general().loanValue(),
                    "explanation.provision", CURRENCY_FORMAT.format(loanParams.general().loanValue()),
                    PERCENTAGE_FORMAT.format(loanParams.general().provision() * 100.)),
            ExplainedValue.of(0.),
            getEstateInsurance(0, loanParams),
            getLifeInsurance(0, loanParams),
            ExplainedValue.of(0.),
            ExplainedValue.of(loanParams.general().paymentDate()),
            ExplainedValue.of(loanParams.general().loanValue()),
            ExplainedValue.of(0.),
            loanParams.getInstallmentType(0),
            loanParams);
  }

  public ExplainedValue<Double> capitalAndInterest() {
    return ExplainedValue.of(capital.value() + interest.value(),
            "explanation.capitalAndInterest",
            CURRENCY_FORMAT.format(capital.value()),
            CURRENCY_FORMAT.format(interest.value()));
  }

  public ExplainedValue<Double> sumTo2Percent() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value(),
            "explanation.sumTo2Percent",
            CURRENCY_FORMAT.format(capital.value()),
            CURRENCY_FORMAT.format(interest.value()),
            CURRENCY_FORMAT.format(sup2percent.value()));
  }

  public ExplainedValue<Double> sumToInsurances() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value() + estateInsurance.value() + lifeInsurance.value(),
            "explanation.sumToInsurances",
            CURRENCY_FORMAT.format(capital.value()),
            CURRENCY_FORMAT.format(interest.value()),
            CURRENCY_FORMAT.format(sup2percent.value()),
            CURRENCY_FORMAT.format(estateInsurance.value()),
            CURRENCY_FORMAT.format(lifeInsurance.value()));
  }

  public ExplainedValue<Double> sumAll() {
    return ExplainedValue.of(capital.value() + interest.value() - sup2percent.value() + estateInsurance.value() + lifeInsurance.value() + excess.value(),
            "explanation.sumAll",
            CURRENCY_FORMAT.format(capital.value()),
            CURRENCY_FORMAT.format(interest.value()),
            CURRENCY_FORMAT.format(sup2percent.value()),
            CURRENCY_FORMAT.format(estateInsurance.value()),
            CURRENCY_FORMAT.format(lifeInsurance.value()),
            CURRENCY_FORMAT.format(excess.value()));
  }

  public ExplainedValue<Double> getRate() {
    return ExplainedValue.of(margin.value() + loanParams.general().baseRate(), "explanation.rate",
            PERCENTAGE_FORMAT.format(margin.value() * 100.), PERCENTAGE_FORMAT.format(loanParams.general().baseRate() * 100.));
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

    ExplainedValue<LocalDate> newDate = loanParams.general().getInstallmentDate(installmentNumber);
    double yearFactor = DateUtils.getYearFactor(date.value(), newDate.value());
    ExplainedValue<InstallmentType> newType = loanParams.getInstallmentType(installmentNumber);
    ExplainedValue<Double> newMargin = loanParams.getMargin(installmentNumber);
    double yearRate = newMargin.value() + loanParams.general().baseRate();
    ExplainedValue<Double>[] newCapitalAndInterest = getNextCapitalAndInterest(yearRate, yearFactor, newType.value(), date.value().until(newDate.value(), ChronoUnit.DAYS));
    ExplainedValue<Double> sup2percent = getSup2percent(installmentNumber);
    double newCapitalLeft = capitalLeft.value() - newCapitalAndInterest[0].value();
    ExplainedValue<Double> newEstateInsurance = getEstateInsurance(installmentNumber, loanParams);
    ExplainedValue<Double> newLifeInsurance = getLifeInsurance(installmentNumber, loanParams);
    ExplainedValue<Double> newExcess = getExcess(installmentNumber,
            newCapitalAndInterest[0].value() + newCapitalAndInterest[1].value() - sup2percent.value()
                    + newEstateInsurance.value() + newLifeInsurance.value(),
            loanParams.excesses());
    if (newExcess.value() > newCapitalLeft) {
      newExcess = ExplainedValue.of(newCapitalLeft, "explanation.excess.whole");
    }

    ExplainedValue<Double> newCapitalLeftAfterExcess = ExplainedValue.of(newCapitalLeft - newExcess.value(), "explanation.capitalLeft",
            CURRENCY_FORMAT.format(capitalLeft.value()), CURRENCY_FORMAT.format(newCapitalAndInterest[0].value()), CURRENCY_FORMAT.format(newExcess.value()));

    return new Installment(
            installmentNumber,
            newCapitalAndInterest[0],
            newCapitalAndInterest[1],
            sup2percent,
            newEstateInsurance,
            newLifeInsurance,
            newExcess,
            newDate,
            newCapitalLeftAfterExcess,
            newMargin,
            newType,
            loanParams
    );
  }

  @SuppressWarnings("unchecked")
  private ExplainedValue<Double>[] getNextCapitalAndInterest(double yearRate, double yearFactor, InstallmentType type, long days) {
    int installmentsLeft = loanParams.general().loanInstallments() - number;
    if (type == InstallmentType.EQUAL) {
      double rate = yearRate / 12;
      ExplainedValue<Double> interest = ExplainedValue.of(capitalLeft.value() * rate);
      double p1n = Math.pow(1 + rate, installmentsLeft);
      ExplainedValue<Double> capital = ExplainedValue.of(capitalLeft.value() * p1n * rate / (p1n - 1) - interest.value());

      return new ExplainedValue[]{capital, interest};
    } else {
      double rate = yearFactor * yearRate;
      ExplainedValue<Double> interest = ExplainedValue.of(capitalLeft.value() * rate, "explanation.decreasing.interest",
              CURRENCY_FORMAT.format(capitalLeft.value()), PERCENTAGE_FORMAT.format(yearRate * 100.), days);
      ExplainedValue<Double> capital = ExplainedValue.of(capitalLeft.value() / installmentsLeft, "explanation.decreasing.capital",
              CURRENCY_FORMAT.format(capitalLeft.value()), installmentsLeft);

      return new ExplainedValue[]{capital, interest};
    }
  }

  private ExplainedValue<Double> getSup2percent(int number) {
    if (loanParams.loan2Percent() != null) {
      if (number <= 120) {
        return ExplainedValue.of(capitalLeft.value() * (loanParams.loan2Percent().rate() - .02) / 12.,
                "explanation.loan2Percent",
                CURRENCY_FORMAT.format(capitalLeft.value()), PERCENTAGE_FORMAT.format(loanParams.loan2Percent().rate() * 100.));
      } else {
        return ExplainedValue.of(0., "explanation.loan2Percent.end");
      }
    }
    return ExplainedValue.of(0.);
  }

  private static ExplainedValue<Double> getExcess(int number, double sumInstallment, ExcessesParams excesses) {
    if (excesses == null || number < excesses.from()) return ExplainedValue.of(0.);
    if (excesses.toValue()) {
      double excess = excesses.value() - sumInstallment;
      if (excess < 0) {
        return ExplainedValue.of(0., "explanation.excess.zero", CURRENCY_FORMAT.format(excesses.value()));
      } else {
        return ExplainedValue.of(excess, "explanation.excess.toValue",
                CURRENCY_FORMAT.format(excesses.value()), CURRENCY_FORMAT.format(sumInstallment));
      }
    }
    if (excesses.period() == TimePeriod.MONTH || number % 12 == excesses.from() % 12) {
      return ExplainedValue.of(excesses.value());
    } else {
      return ExplainedValue.of(0.);
    }
  }

  private static ExplainedValue<Double> getEstateInsurance(int number, LoanParams loanParams) {
    // Last installment - no insurance
    if (number == loanParams.general().loanInstallments()) return ExplainedValue.of(0.);
    EstateInsuranceParams estateInsurance = loanParams.estateInsurance();
    if (estateInsurance.period() == TimePeriod.MONTH) {
      return ExplainedValue.of(estateInsurance.value() * estateInsurance.amount() / 12., "explanation.estateInsurance.month",
              CURRENCY_FORMAT.format(estateInsurance.amount()), PERCENTAGE_FORMAT.format(estateInsurance.value() * 100.));
    } else if (number % 12 == 0) {
      return ExplainedValue.of(estateInsurance.value() * estateInsurance.amount(), "explanation.estateInsurance.year",
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
                "explanation.lifeInsurance.inAdvance", lifeInsurance.inAdvancePeriod(),
                CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.inAdvanceValue() * 100.));
      } else if (number < lifeInsurance.inAdvancePeriod()) {
        return ExplainedValue.of(0., "explanation.lifeInsurance.noDuringAdvancePeriod");
      }
    }
    if (lifeInsurance.period() == TimePeriod.MONTH) {
      return ExplainedValue.of(lifeInsurance.value() * lifeInsurance.amount() / 12., "explanation.lifeInsurance.month",
              CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.value() * 100.));
    } else if (number % 12 == lifeInsurance.inAdvancePeriod() % 12) {
      int months = Math.min(12, loanParams.general().loanInstallments() - number);
      return ExplainedValue.of(lifeInsurance.value() * lifeInsurance.amount() * months / 12., "explanation.lifeInsurance.year",
              CURRENCY_FORMAT.format(lifeInsurance.amount()), PERCENTAGE_FORMAT.format(lifeInsurance.value() * 100.));
    }
    return ExplainedValue.of(0.);
  }
}
