package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.utils.DateUtils;

import javax.swing.*;
import java.time.LocalDate;

import static pl.mmakos.mortgage.MortgageCalculator.CURRENCY_FORMAT;

public interface Spinner {
  class Int extends JSpinner {
    public int intValue() {
      return ((Number) getValue()).intValue();
    }

    public String stringValue() {
      return Integer.toString(intValue());
    }
  }

  class Double extends JSpinner {
    public double doubleValue() {
      return ((Number) getValue()).doubleValue();
    }

    public String stringValue() {
      return java.lang.Double.toString(doubleValue());
    }

    @Override
    protected JComponent createEditor(SpinnerModel model) {
      return new NumberEditor(this, "#.##### '%'");
    }
  }

  class Date extends JSpinner {
    public LocalDate dateValue() {
      return DateUtils.toLocalDate((java.util.Date) getValue());
    }

    public String stringValue() {
      return dateValue().toString();
    }
  }

  class Currency extends Double {
    @Override
    protected JComponent createEditor(SpinnerModel model) {
      return new NumberEditor(this, CURRENCY_FORMAT.toPattern());
    }
  }
}
