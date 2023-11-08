package pl.mmakos.mortgage.view;

import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

@RequiredArgsConstructor
public class CustomNameComboBoxRenderer<T> extends DefaultListCellRenderer {
  private final Function<T, String> nameProvider;

  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    @SuppressWarnings("unchecked")
    String name = nameProvider.apply((T) value);
    if (name != null) {
      label.setText(name);
    }
    return label;
  }
}
