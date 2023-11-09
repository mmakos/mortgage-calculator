package pl.mmakos.mortgage.view;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import pl.mmakos.mortgage.model.Summary;
import pl.mmakos.mortgage.utils.ViewUtils;

import javax.swing.*;

import static pl.mmakos.mortgage.MortgageCalculator.*;

public class SummaryInstallmentsPanel extends JPanel {
  private final ExplainedField sumField = new ExplainedField();
  private final ExplainedField costField = new ExplainedField();
  private final ExplainedField interestAnd2percentSumField = new ExplainedField();
  private final ExplainedField rrsoField = new ExplainedField();
  private final ExplainedField lastDateField = new ExplainedField();
  private final ExplainedField periodField = new ExplainedField();

  private final ExplainedField capitalSumField = new ExplainedField();
  private final ExplainedField interestSumField = new ExplainedField();
  private final ExplainedField sup2percentSumField = new ExplainedField();
  private final ExplainedField estateInsuranceSumField = new ExplainedField();
  private final ExplainedField lifeInsuranceSumField = new ExplainedField();
  private final ExplainedField excessesSumField = new ExplainedField();

  public SummaryInstallmentsPanel() {
    setLayout(new FormLayout("pref,5px,pref:grow,5px,pref,5px,pref:grow", ViewUtils.getNoGrowPref(6)));
    setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.summary")));

    init();
  }

  public void update(Summary summary) {
    sumField.setValue(summary.sum(), CURRENCY_FORMAT);
    costField.setValue(summary.cost(), CURRENCY_FORMAT);
    interestAnd2percentSumField.setValue(summary.interestAnd2percentSum(), CURRENCY_FORMAT);
    rrsoField.setValue(summary.rrso(), PERCENT_FORMAT, 100.);
    lastDateField.setValue(summary.lastDate());
    periodField.setValue(summary.period());

    capitalSumField.setValue(summary.capitalSum(), CURRENCY_FORMAT);
    interestSumField.setValue(summary.interestSum(), CURRENCY_FORMAT);
    sup2percentSumField.setValue(summary.sup2percentSum(), CURRENCY_FORMAT);
    estateInsuranceSumField.setValue(summary.estateInsuranceSum(), CURRENCY_FORMAT);
    lifeInsuranceSumField.setValue(summary.lifeInsuranceSum(), CURRENCY_FORMAT);
    excessesSumField.setValue(summary.excessesSum(), CURRENCY_FORMAT);
  }

  private void init() {
    JLabel sumLabel = new JLabel(BUNDLE.getString("panel.summary.sum"));
    JLabel costLabel = new JLabel(BUNDLE.getString("panel.summary.cost"));
    JLabel interestAnd2percentSumLabel = new JLabel(BUNDLE.getString("panel.summary.interestAnd2percentSum"));
    JLabel rrsoLabel = new JLabel(BUNDLE.getString("panel.summary.rrso"));
    JLabel lastDateLabel = new JLabel(BUNDLE.getString("panel.summary.lastDate"));
    JLabel periodLabel = new JLabel(BUNDLE.getString("panel.summary.period"));

    JLabel capitalSumLabel = new JLabel(BUNDLE.getString("panel.summary.capitalSum"));
    JLabel interestSumLabel = new JLabel(BUNDLE.getString("panel.summary.interestSum"));
    JLabel sup2percentSumLabel = new JLabel(BUNDLE.getString("panel.summary.sup2percentSum"));
    JLabel estateInsuranceSumLabel = new JLabel(BUNDLE.getString("panel.summary.estateInsuranceSum"));
    JLabel lifeInsuranceSumLabel = new JLabel(BUNDLE.getString("panel.summary.lifeInsuranceSum"));
    JLabel excessesSumLabel = new JLabel(BUNDLE.getString("panel.summary.excessesSum"));

    CellConstraints cc = new CellConstraints();
    add(sumLabel, cc.xy(1, 1));
    add(costLabel, cc.xy(1, 3));
    add(interestAnd2percentSumLabel, cc.xy(1, 5));
    add(rrsoLabel, cc.xy(1, 7));
    add(lastDateLabel, cc.xy(1, 9));
    add(periodLabel, cc.xy(1, 11));

    add(sumField, cc.xy(3, 1));
    add(costField, cc.xy(3, 3));
    add(interestAnd2percentSumField, cc.xy(3, 5));
    add(rrsoField, cc.xy(3, 7));
    add(lastDateField, cc.xy(3, 9));
    add(periodField, cc.xy(3, 11));

    add(capitalSumLabel, cc.xy(5, 1));
    add(interestSumLabel, cc.xy(5, 3));
    add(sup2percentSumLabel, cc.xy(5, 5));
    add(estateInsuranceSumLabel, cc.xy(5, 7));
    add(lifeInsuranceSumLabel, cc.xy(5, 9));
    add(excessesSumLabel, cc.xy(5, 11));

    add(capitalSumField, cc.xy(7, 1));
    add(interestSumField, cc.xy(7, 3));
    add(sup2percentSumField, cc.xy(7, 5));
    add(estateInsuranceSumField, cc.xy(7, 7));
    add(lifeInsuranceSumField, cc.xy(7, 9));
    add(excessesSumField, cc.xy(7, 11));
  }
}
