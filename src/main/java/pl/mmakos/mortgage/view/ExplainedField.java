package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.ExplainedValue;

import javax.swing.*;
import java.text.NumberFormat;

public class ExplainedField extends JTextField {
  public ExplainedField() {
    setEditable(false);
  }

  public void setValue(ExplainedValue<? extends Number> value, NumberFormat format, double scale) {
    setText(format.format(value.value().doubleValue() * scale));
    if (value.explanation() != null) {
      setToolTipText(value.explanation());
    }
  }

  public void setValue(ExplainedValue<? extends Number> value, NumberFormat format) {
    setText(format.format(value.value()));
    if (value.explanation() != null) {
      setToolTipText(value.explanation());
    }
  }

  public void setValue(ExplainedValue<?> value) {
    setText(String.valueOf(value.value()));
    if (value.explanation() != null) {
      setToolTipText(value.explanation());
    }
  }
}
