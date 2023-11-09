package pl.mmakos.mortgage.view;

import com.jgoodies.forms.layout.CellConstraints;
import pl.mmakos.mortgage.model.Installment;
import pl.mmakos.mortgage.utils.ViewUtils;

import javax.swing.*;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;
import static pl.mmakos.mortgage.MortgageCalculator.CURRENCY_FORMAT;

public class InitialInstallmentPanel extends JPanel {
  private final ExplainedField sumField = new ExplainedField();
  private final ExplainedField dateField = new ExplainedField();
  private final ExplainedField provisionField = new ExplainedField();
  private final ExplainedField otherCostsField = new ExplainedField();
  private final ExplainedField estateInsuranceField = new ExplainedField();
  private final ExplainedField lifeInsuranceField = new ExplainedField();

  public InitialInstallmentPanel() {
    setLayout(ViewUtils.getFormLayout(2, 6));
    setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.paymentParams")));
    init();
  }

  public void update(Installment initialInstallment) {
    dateField.setValue(initialInstallment.date());

    sumField.setValue(initialInstallment.sumAll().with("explanation.sum"), CURRENCY_FORMAT);
    provisionField.setValue(initialInstallment.interest(), CURRENCY_FORMAT);
    otherCostsField.setValue(initialInstallment.capital(), CURRENCY_FORMAT);
    estateInsuranceField.setValue(initialInstallment.estateInsurance(), CURRENCY_FORMAT);
    lifeInsuranceField.setValue(initialInstallment.lifeInsurance(), CURRENCY_FORMAT);
  }

  private void init() {
    JLabel sumLabel = new JLabel(BUNDLE.getString("panel.paymentParams.sum"));
    JLabel dateLabel = new JLabel(BUNDLE.getString("panel.paymentParams.date"));
    JLabel provisionLabel = new JLabel(BUNDLE.getString("panel.paymentParams.provision"));
    JLabel otherCostsLabel = new JLabel(BUNDLE.getString("panel.paymentParams.otherCosts"));
    JLabel estateInsuranceLabel = new JLabel(BUNDLE.getString("panel.paymentParams.estateInsurance"));
    JLabel lifeInsuranceLabel = new JLabel(BUNDLE.getString("panel.paymentParams.lifeInsurance"));

    CellConstraints cc = new CellConstraints();

    add(sumLabel, cc.xy(1, 1));
    add(provisionLabel, cc.xy(1, 3));
    add(otherCostsLabel, cc.xy(1, 5));
    add(dateLabel, cc.xy(1, 7));
    add(estateInsuranceLabel, cc.xy(1, 9));
    add(lifeInsuranceLabel, cc.xy(1, 11));

    add(sumField, cc.xy(3, 1));
    add(provisionField, cc.xy(3, 3));
    add(otherCostsField, cc.xy(3, 5));
    add(dateField, cc.xy(3, 7));
    add(estateInsuranceField, cc.xy(3, 9));
    add(lifeInsuranceField, cc.xy(3, 11));
  }
}
