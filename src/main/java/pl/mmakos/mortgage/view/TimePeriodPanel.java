package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.TimePeriod;

import javax.swing.*;
import java.awt.*;

public class TimePeriodPanel extends JPanel {
  private final Spinner.Int timeSpinner = new Spinner.Int();
  private final JComboBox<TimePeriod> timePeriodComboBox = new JComboBox<>(TimePeriod.values());

  public TimePeriodPanel() {
    super(new BorderLayout());
    bindTimePeriodSpinner(timePeriodComboBox, timeSpinner);

    add(timeSpinner, BorderLayout.CENTER);
    add(timePeriodComboBox, BorderLayout.EAST);
  }

  public void setTime(int time) {
    timeSpinner.setValue(time);
  }

  public void setPeriod(TimePeriod period) {
    timePeriodComboBox.setSelectedItem(period);
  }

  public String getTimeString() {
    return timeSpinner.stringValue();
  }

  public TimePeriod getPeriod() {
    return (TimePeriod) timePeriodComboBox.getSelectedItem();
  }

  public void setComboBoxRenderer(ListCellRenderer<? super TimePeriod> listCellRenderer) {
    timePeriodComboBox.setRenderer(listCellRenderer);
  }

  public void setSpinnerNumberModel(SpinnerNumberModel spinnerNumberModel) {
    timeSpinner.setModel(spinnerNumberModel);
  }

  public int getMonthPeriod() {
    TimePeriod selectedPeriod = (TimePeriod) timePeriodComboBox.getSelectedItem();
    assert selectedPeriod != null;
    return selectedPeriod.convert(timeSpinner.intValue(), TimePeriod.MONTH);
  }

  @Override
  public void setEnabled(boolean enabled) {
    timeSpinner.setEnabled(enabled);
    timePeriodComboBox.setEnabled(enabled);
    super.setEnabled(enabled);
  }

  public static void bindTimePeriodSpinner(JComboBox<TimePeriod> comboBox, JSpinner spinner) {
    TimePeriod[] currentPeriod = new TimePeriod[]{TimePeriod.YEAR};
    comboBox.addActionListener(e -> {
      TimePeriod selectedItem = (TimePeriod) comboBox.getSelectedItem();
      changeSpinnerPeriod(currentPeriod[0], selectedItem, spinner);
      currentPeriod[0] = selectedItem;
    });
  }

  private static void changeSpinnerPeriod(TimePeriod previous, TimePeriod current, JSpinner spinner) {
    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
    Number maximum = (Number) model.getMaximum();
    Number number = model.getNumber();

    if (number instanceof Integer) {
      model.setMaximum(previous.convert(maximum.intValue(), current));
      model.setValue(previous.convert(number.intValue(), current));
    } else {
      model.setMaximum(previous.convert(maximum.doubleValue(), current));
      model.setValue(previous.convert(number.doubleValue(), current));
    }
  }
}
