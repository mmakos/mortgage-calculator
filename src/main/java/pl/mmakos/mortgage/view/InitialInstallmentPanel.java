package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.Installment;
import pl.mmakos.mortgage.utils.ViewUtils;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;

public class InitialInstallmentPanel extends JPanel {
  private final JTextField sumField = new JTextField();
  private final JTextField dateField = new JTextField();
  private final JTextField provisionField = new JTextField();
  private final JTextField otherCostsField = new JTextField();
  private final JTextField estateInsuranceField = new JTextField();
  private final JTextField lifeInsuranceField = new JTextField();

  public InitialInstallmentPanel() {
    setLayout(new GridBagLayout());
    setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.paymentParams")));
    init();
  }

  public void update(Installment initialInstallment) {
    dateField.setText(initialInstallment.date().toString());

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    sumField.setText(currencyFormatter.format(initialInstallment.sumAll()));
    provisionField.setText(currencyFormatter.format(initialInstallment.interest()));
    otherCostsField.setText(currencyFormatter.format(initialInstallment.capital()));
    estateInsuranceField.setText(currencyFormatter.format(initialInstallment.estateInsurance()));
    lifeInsuranceField.setText(currencyFormatter.format(initialInstallment.lifeInsurance()));
  }

  private void init() {
    JLabel sumLabel = new JLabel(BUNDLE.getString("panel.paymentParams.sum"));
    JLabel dateLabel = new JLabel(BUNDLE.getString("panel.paymentParams.date"));
    JLabel provisionLabel = new JLabel(BUNDLE.getString("panel.paymentParams.provision"));
    JLabel otherCostsLabel = new JLabel(BUNDLE.getString("panel.paymentParams.otherCosts"));
    JLabel estateInsuranceLabel = new JLabel(BUNDLE.getString("panel.paymentParams.estateInsurance"));
    JLabel lifeInsuranceLabel = new JLabel(BUNDLE.getString("panel.paymentParams.lifeInsurance"));

    sumField.setEditable(false);
    dateField.setEditable(false);
    provisionField.setEditable(false);
    otherCostsField.setEditable(false);
    estateInsuranceField.setEditable(false);
    lifeInsuranceField.setEditable(false);

    add(sumLabel, c(1, 1, 0, 0, 0));
    add(provisionLabel, c(1, 2, 0, 0, 5));
    add(otherCostsLabel, c(1, 3, 0, 0, 5));
    add(dateLabel, c(3, 1, 0, 5, 0));
    add(estateInsuranceLabel, c(3, 2, 0, 5, 5));
    add(lifeInsuranceLabel, c(3, 3, 0, 5, 5));
    add(sumField, c(2, 1, 1, 5, 0));
    add(provisionField, c(2, 2, 1, 5, 5));
    add(otherCostsField, c(2, 3, 1, 5, 5));
    add(dateField, c(4, 1, 1, 5, 0));
    add(estateInsuranceField, c(4, 2, 1, 5, 5));
    add(lifeInsuranceField, c(4, 3, 1, 5, 5));
  }

  private static GridBagConstraints c(int x, int y, int wx, int leftInset, int topInset) {
    return new GridBagConstraints(x, y, 1, 1, wx, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(topInset, leftInset, 0, 0), 0, 0);
  }
}
