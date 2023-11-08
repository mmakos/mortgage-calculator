package pl.mmakos.mortgage.view;

import lombok.RequiredArgsConstructor;
import pl.mmakos.mortgage.model.ExplainedValue;

import javax.swing.*;
import java.text.NumberFormat;

@RequiredArgsConstructor
public class ExplainedField extends JTextField {
  private final NumberFormat format;

  public void setValue(ExplainedValue<? extends Number> value) {
    setText(format.format(value.value()));
    if (value.explanation() != null) {
      setToolTipText(value.explanation());
    }
  }
}
