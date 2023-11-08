package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.utils.DateUtils;

import javax.swing.*;
import java.time.LocalDate;

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
  }

  class Date extends JSpinner {
    public LocalDate dateValue() {
      return DateUtils.toLocalDate((java.util.Date) getValue());
    }

    public String stringValue() {
      return dateValue().toString();
    }
  }
}
