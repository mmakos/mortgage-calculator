package pl.mmakos.mortgage.view;

import com.jgoodies.forms.layout.CellConstraints;
import pl.mmakos.mortgage.model.*;
import pl.mmakos.mortgage.utils.DateUtils;
import pl.mmakos.mortgage.utils.PropertiesUtils;
import pl.mmakos.mortgage.utils.ViewUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;
import static pl.mmakos.mortgage.view.TimePeriodPanel.bindTimePeriodSpinner;

@SuppressWarnings({"java:S1192", "java:S3358"})
public class MortgageOptionsPanel extends JPanel {
  private final Spinner.Currency loanValueSpinner = new Spinner.Currency();
  private final TimePeriodPanel loanTimePanel = new TimePeriodPanel();
  private final Spinner.Double marginSpinner = new Spinner.Double();
  private final Spinner.Double baseRateSpinner = new Spinner.Double();
  private final Spinner.Double provisionSpinner = new Spinner.Double();
  private final Spinner.Currency otherStartCostsSpinner = new Spinner.Currency();
  private final JComboBox<InstallmentType> installmentTypeComboBox = new JComboBox<>(InstallmentType.values());
  private final Spinner.Date paymentSpinner = new Spinner.Date();
  private final Spinner.Date firstInstallmentSpinner = new Spinner.Date();
  private final ActionCheckBox installmentOnlyInWorkDayCheckBox = new ActionCheckBox();

  private final Spinner.Double estateValueSpinner = new Spinner.Double();
  private final JComboBox<TimePeriod> estatePeriodComboBox = new JComboBox<>(TimePeriod.values());
  private final Spinner.Currency estateAmountSpinner = new Spinner.Currency();
  private final ActionCheckBox estateAmountEqualsMortgageValueCheckBox = new ActionCheckBox();

  private final ActionCheckBox lifeInsuranceCheckBox = new ActionCheckBox();
  private final Spinner.Double lifeValueSpinner = new Spinner.Double();
  private final JComboBox<TimePeriod> lifePeriodComboBox = new JComboBox<>(TimePeriod.values());
  private final Spinner.Currency lifeAmountSpinner = new Spinner.Currency();
  private final ActionCheckBox lifeAmountEqualsMortgageValueCheckBox = new ActionCheckBox();
  private final ActionCheckBox lifeInAdvanceCheckBox = new ActionCheckBox();
  private final TimePeriodPanel lifeInAdvanceTimePanel = new TimePeriodPanel();
  private final ActionCheckBox lifeValueInAdvanceCheckBox = new ActionCheckBox();
  private final Spinner.Double lifeValueInAdvanceValueSpinner = new Spinner.Double();

  private final JPanel promotionsPanel = new JPanel();

  private final ActionCheckBox loan2PercentCheckBox = new ActionCheckBox();
  private final Spinner.Double loan2PercentValueSpinner = new Spinner.Double();

  private final ActionCheckBox excessCheckBox = new ActionCheckBox();
  private final JRadioButton excessValueRadio = new JRadioButton();
  private final JRadioButton excessToValueRadio = new JRadioButton();
  private final TimePeriodPanel excessFromTimePanel = new TimePeriodPanel();
  private final Spinner.Currency excessValueSpinner = new Spinner.Currency();
  private final Spinner.Currency excessToValueSpinner = new Spinner.Currency();
  private final JComboBox<TimePeriod> excessValuePeriodComboBox = new JComboBox<>(TimePeriod.values());
  private JButton removeButton;

  public MortgageOptionsPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(getGeneralPanel());
    add(getEstateInsurancePanel());
    add(getLifeInsurancePanel());
    add(getPromotionsPanel());
    add(get2percentPanel());
    add(getExcessPaymentPanel());
  }

  public LoanParams getParams() {
    GeneralLoanParams general = new GeneralLoanParams(
            loanValueSpinner.doubleValue(),
            loanTimePanel.getMonthPeriod(),
            marginSpinner.doubleValue() / 100.,
            baseRateSpinner.doubleValue() / 100.,
            provisionSpinner.doubleValue() / 100.,
            otherStartCostsSpinner.doubleValue(),
            (InstallmentType) installmentTypeComboBox.getSelectedItem(),
            paymentSpinner.dateValue(),
            firstInstallmentSpinner.dateValue(),
            installmentOnlyInWorkDayCheckBox.isSelected()
    );

    EstateInsuranceParams estateInsurance = new EstateInsuranceParams(
            estateValueSpinner.doubleValue() / 100.,
            estateAmountEqualsMortgageValueCheckBox.isSelected() ?
                    loanValueSpinner.doubleValue() :
                    estateAmountSpinner.doubleValue(),
            (TimePeriod) estatePeriodComboBox.getSelectedItem()
    );

    LifeInsuranceParams lifeInsurance = !lifeInsuranceCheckBox.isSelected() ? null : new LifeInsuranceParams(
            lifeValueSpinner.doubleValue() / 100.,
            lifeAmountEqualsMortgageValueCheckBox.isSelected() ?
                    loanValueSpinner.doubleValue() :
                    lifeAmountSpinner.doubleValue(),
            (TimePeriod) lifePeriodComboBox.getSelectedItem(),
            lifeInAdvanceCheckBox.isSelected() ?
                    lifeInAdvanceTimePanel.getMonthPeriod() : 0,
            lifeValueInAdvanceCheckBox.isSelected() ?
                    lifeValueInAdvanceValueSpinner.doubleValue() / 100. :
                    lifeValueSpinner.doubleValue() / 100.
    );

    PromotionParams[] promotions = streamPromotionPanels()
            .map(PromotionPanel::getParams)
            .toArray(PromotionParams[]::new);

    Loan2PercentParams loan2Percent = !loan2PercentCheckBox.isSelected() ? null : new Loan2PercentParams(
            loan2PercentValueSpinner.doubleValue() / 100.
    );

    ExcessesParams excesses = !excessCheckBox.isSelected() ? null : new ExcessesParams(
            excessFromTimePanel.getMonthPeriod(),
            (TimePeriod) excessValuePeriodComboBox.getSelectedItem(),
            excessToValueRadio.isSelected() ? excessToValueSpinner.doubleValue() : excessValueSpinner.doubleValue(),
            excessToValueRadio.isSelected()
    );

    return new LoanParams(
            general,
            estateInsurance,
            lifeInsurance,
            promotions,
            loan2Percent,
            excesses
    );
  }

  private JPanel getGeneralPanel() {
    JPanel panel = new JPanel(ViewUtils.getFormLayout(2, 10));
    panel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.general")));

    JLabel loanValueLabel = new JLabel(BUNDLE.getString("panel.general.loanValue"));
    JLabel loanTimeLabel = new JLabel(BUNDLE.getString("panel.general.loanTime"));
    JLabel marginLabel = new JLabel(BUNDLE.getString("panel.general.margin"));
    JLabel baseRateLabel = new JLabel(BUNDLE.getString("panel.general.baseRate"));
    JLabel provisionLabel = new JLabel(BUNDLE.getString("panel.general.provision"));
    JLabel otherStartCostsLabel = new JLabel(BUNDLE.getString("panel.general.otherStartCosts"));
    JLabel installmentTypeLabel = new JLabel(BUNDLE.getString("panel.general.installmentType"));
    JLabel paymentDateLabel = new JLabel(BUNDLE.getString("panel.general.paymentDate"));
    JLabel firstInstallmentDateLabel = new JLabel(BUNDLE.getString("panel.general.firstInstallmentDate"));

    loanValueSpinner.setModel(new SpinnerNumberModel(500_000., 0., 10_000_000., 10_000.));
    loanTimePanel.setSpinnerNumberModel(new SpinnerNumberModel(25, 1, 35, 1));
    marginSpinner.setModel(new SpinnerNumberModel(2., 0., 100., .1));
    baseRateSpinner.setModel(new SpinnerNumberModel(5.67, 0., 100., .1));
    provisionSpinner.setModel(new SpinnerNumberModel(0.5, 0., 100., .1));
    otherStartCostsSpinner.setModel(new SpinnerNumberModel(0., 0., 10_000_000., 100.));

    LocalDate now = LocalDate.now();
    Date nowDate = DateUtils.toDate(now);
    Date nextMonth = DateUtils.toDate(now.plusMonths(1));

    paymentSpinner.setModel(new SpinnerDateModel(nowDate, new Date(0), new Date(Long.MAX_VALUE), Calendar.DAY_OF_MONTH));
    SpinnerDateModel firstInstallmentDateModel =
            new SpinnerDateModel(nextMonth, nowDate, new Date(Long.MAX_VALUE), Calendar.DAY_OF_MONTH);
    firstInstallmentSpinner.setModel(firstInstallmentDateModel);

    paymentSpinner.setEditor(new JSpinner.DateEditor(paymentSpinner, "dd.MM.yyyy"));
    firstInstallmentSpinner.setEditor(new JSpinner.DateEditor(firstInstallmentSpinner, "dd.MM.yyyy"));

    paymentSpinner.addChangeListener(e -> {
      Date date = (Date) paymentSpinner.getValue();
      Date firstInstallmentDate = firstInstallmentDateModel.getDate();
      if (firstInstallmentDate.before(date)) {
        firstInstallmentSpinner.setValue(date);
      }
      firstInstallmentDateModel.setStart(date);
    });

    installmentOnlyInWorkDayCheckBox.setText(BUNDLE.getString("panel.general.installmentOnlyInWorkDay"));

    CellConstraints cc = new CellConstraints();

    panel.add(loanValueLabel, cc.xy(1, 1));
    panel.add(loanTimeLabel, cc.xy(1, 3));
    panel.add(marginLabel, cc.xy(1, 5));
    panel.add(baseRateLabel, cc.xy(1, 7));
    panel.add(provisionLabel, cc.xy(1, 9));
    panel.add(otherStartCostsLabel, cc.xy(1, 11));
    panel.add(installmentTypeLabel, cc.xy(1, 13));
    panel.add(paymentDateLabel, cc.xy(1, 15));
    panel.add(firstInstallmentDateLabel, cc.xy(1, 17));
    panel.add(installmentOnlyInWorkDayCheckBox, cc.xy(1, 19));

    panel.add(loanValueSpinner, cc.xy(3, 1));
    panel.add(loanTimePanel, cc.xy(3, 3));
    panel.add(marginSpinner, cc.xy(3, 5));
    panel.add(baseRateSpinner, cc.xy(3, 7));
    panel.add(provisionSpinner, cc.xy(3, 9));
    panel.add(otherStartCostsSpinner, cc.xy(3, 11));
    panel.add(installmentTypeComboBox, cc.xy(3, 13));
    panel.add(paymentSpinner, cc.xy(3, 15));
    panel.add(firstInstallmentSpinner, cc.xy(3, 17));

    return panel;
  }

  private JPanel getEstateInsurancePanel() {
    JPanel panel = new JPanel(ViewUtils.getFormLayout(2, 4));
    panel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.estateInsurance")));

    JLabel valueLabel = new JLabel(BUNDLE.getString("panel.estateInsurance.value"));
    JLabel amountLabel = new JLabel(BUNDLE.getString("panel.estateInsurance.amount"));
    JLabel periodLabel = new JLabel(BUNDLE.getString("panel.estateInsurance.period"));

    estateValueSpinner.setModel(new SpinnerNumberModel(0.08, 0., 100., 0.01));
    estateAmountSpinner.setModel(new SpinnerNumberModel(500_000., 0., 10_000_000., 10_000.));
    estatePeriodComboBox.setRenderer(new CustomNameComboBoxRenderer<>(TimePeriod::getAdverb));

    estateAmountEqualsMortgageValueCheckBox.setText(BUNDLE.getString("panel.estateInsurance.amountEqualsMortgageValue"));
    estateAmountEqualsMortgageValueCheckBox.setSelected(true);
    estateAmountSpinner.setEnabled(false);
    estateAmountEqualsMortgageValueCheckBox.addActionListener(e ->
            estateAmountSpinner.setEnabled(!estateAmountEqualsMortgageValueCheckBox.isSelected()));

    CellConstraints cc = new CellConstraints();

    panel.add(valueLabel, cc.xy(1, 1));
    panel.add(amountLabel, cc.xy(1, 3));
    panel.add(periodLabel, cc.xy(1, 5));
    panel.add(estateAmountEqualsMortgageValueCheckBox, cc.xy(1, 7));

    panel.add(estateValueSpinner, cc.xy(3, 1));
    panel.add(estateAmountSpinner, cc.xy(3, 3));
    panel.add(estatePeriodComboBox, cc.xy(3, 5));

    return panel;
  }

  private JPanel getLifeInsurancePanel() {
    JPanel panel = new JPanel(ViewUtils.getFormLayout(2, 7));
    panel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.lifeInsurance")));

    lifeInsuranceCheckBox.setText(BUNDLE.getString("panel.lifeInsurance.enable"));
    JLabel valueLabel = new JLabel(BUNDLE.getString("panel.lifeInsurance.value"));
    JLabel amountLabel = new JLabel(BUNDLE.getString("panel.lifeInsurance.amount"));
    JLabel periodLabel = new JLabel(BUNDLE.getString("panel.lifeInsurance.period"));

    lifeValueSpinner.setModel(new SpinnerNumberModel(0.05, 0., 100., 0.01));
    lifeAmountSpinner.setModel(new SpinnerNumberModel(500_000., 0., 10_000_000., 10_000.));
    lifePeriodComboBox.setRenderer(new CustomNameComboBoxRenderer<>(TimePeriod::getAdverb));

    lifeAmountEqualsMortgageValueCheckBox.setText(BUNDLE.getString("panel.lifeInsurance.amountEqualsMortgageValue"));
    lifeAmountEqualsMortgageValueCheckBox.setSelected(true);
    lifeAmountSpinner.setEnabled(false);
    lifeAmountEqualsMortgageValueCheckBox.addActionListener(e ->
            lifeAmountSpinner.setEnabled(!lifeAmountEqualsMortgageValueCheckBox.isSelected()));

    lifeInAdvanceCheckBox.setText(BUNDLE.getString("panel.lifeInsurance.inAdvance"));
    lifeInAdvanceTimePanel.setSpinnerNumberModel(new SpinnerNumberModel(5, 1, 35, 1));
    lifeValueInAdvanceCheckBox.setText(BUNDLE.getString("panel.lifeInsurance.valueInAdvance"));
    lifeValueInAdvanceValueSpinner.setModel(new SpinnerNumberModel(0.05, 0., 100., 0.05));

    lifeInsuranceCheckBox.setSelected(true);
    lifeValueInAdvanceCheckBox.setEnabled(false);
    lifeInAdvanceTimePanel.setEnabled(false);
    lifeValueInAdvanceValueSpinner.setEnabled(false);

    lifeInsuranceCheckBox.addActionListener(e -> {
      boolean selected = lifeInsuranceCheckBox.isSelected();
      lifeValueSpinner.setEnabled(selected);
      lifeAmountSpinner.setEnabled(selected && !lifeAmountEqualsMortgageValueCheckBox.isSelected());
      lifePeriodComboBox.setEnabled(selected);
      lifeAmountEqualsMortgageValueCheckBox.setEnabled(selected);
      lifeInAdvanceCheckBox.setEnabled(selected);
      boolean inAdvanceSelected = lifeInAdvanceCheckBox.isSelected();
      lifeValueInAdvanceCheckBox.setEnabled(selected && inAdvanceSelected);
      lifeInAdvanceTimePanel.setEnabled(selected && inAdvanceSelected);
      lifeValueInAdvanceValueSpinner.setEnabled(selected && inAdvanceSelected && lifeValueInAdvanceCheckBox.isSelected());
    });

    lifeInAdvanceCheckBox.addActionListener(e -> {
      boolean selected = lifeInAdvanceCheckBox.isSelected();
      lifeValueInAdvanceCheckBox.setEnabled(selected);
      lifeInAdvanceTimePanel.setEnabled(selected);
      lifeValueInAdvanceValueSpinner.setEnabled(selected && lifeValueInAdvanceCheckBox.isSelected());
    });

    lifeValueInAdvanceCheckBox.addActionListener(e ->
            lifeValueInAdvanceValueSpinner.setEnabled(lifeValueInAdvanceCheckBox.isSelected()));

    CellConstraints cc = new CellConstraints();

    panel.add(lifeInsuranceCheckBox, cc.xy(1, 1));
    panel.add(valueLabel, cc.xy(1, 3));
    panel.add(amountLabel, cc.xy(1, 5));
    panel.add(periodLabel, cc.xy(1, 7));
    panel.add(lifeAmountEqualsMortgageValueCheckBox, cc.xy(1, 9));
    panel.add(lifeInAdvanceCheckBox, cc.xy(1, 11));
    panel.add(lifeValueInAdvanceCheckBox, cc.xy(1, 13));

    panel.add(lifeValueSpinner, cc.xy(3, 3));
    panel.add(lifeAmountSpinner, cc.xy(3, 5));
    panel.add(lifePeriodComboBox, cc.xy(3, 7));
    panel.add(lifeInAdvanceTimePanel, cc.xy(3, 11));
    panel.add(lifeValueInAdvanceValueSpinner, cc.xy(3, 13));

    return panel;
  }

  private JPanel getPromotionsPanel() {
    promotionsPanel.setLayout(new BoxLayout(promotionsPanel, BoxLayout.Y_AXIS));
    promotionsPanel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.promotions")));

    JButton addButton = new JButton("+");
    removeButton = new JButton("-");
    removeButton.setEnabled(false);

    addButton.addActionListener(e -> addPromotionPanel());
    removeButton.addActionListener(e -> {
      int compIndex = promotionsPanel.getComponentCount() - 2;
      promotionsPanel.remove(compIndex);
      promotionsPanel.revalidate();
      if (promotionsPanel.getComponentCount() < 2) {
        removeButton.setEnabled(false);
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);

    promotionsPanel.add(buttonPanel);

    return promotionsPanel;
  }

  private PromotionPanel addPromotionPanel() {
    PromotionPanel promotionPanel = new PromotionPanel(promotionsPanel.getComponentCount() < 2);
    promotionsPanel.add(promotionPanel, promotionsPanel.getComponentCount() - 1);
    promotionsPanel.revalidate();
    removeButton.setEnabled(true);
    return promotionPanel;
  }

  private JPanel get2percentPanel() {
    JPanel panel = new JPanel(ViewUtils.getFormLayout(3, 2));
    panel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.2percent")));

    JLabel loan2percentValueLabel = new JLabel(BUNDLE.getString("panel.2percent.value"));

    loan2PercentCheckBox.setText(BUNDLE.getString("panel.2percent.enable"));
    loan2PercentValueSpinner.setModel(new SpinnerNumberModel(7.14, 0., 100., 0.1));
    loan2PercentValueSpinner.setEnabled(false);

    loan2PercentCheckBox.addActionListener(e -> loan2PercentValueSpinner.setEnabled(loan2PercentCheckBox.isSelected()));

    CellConstraints cc = new CellConstraints();
    panel.add(loan2PercentCheckBox, cc.xy(1, 1));
    panel.add(loan2percentValueLabel, cc.xy(1, 3));
    panel.add(loan2PercentValueSpinner, cc.xy(3, 3));

    return panel;
  }

  private JPanel getExcessPaymentPanel() {
    JPanel panel = new JPanel(ViewUtils.getFormLayout(1, 4));
    panel.setBorder(ViewUtils.createTitledBorder(BUNDLE.getString("panel.excessPayment")));

    JLabel excessFromLabel = new JLabel(BUNDLE.getString("panel.excessPayment.from"));
    excessValueRadio.setText(BUNDLE.getString("panel.excessPayment.value"));
    excessToValueRadio.setText(BUNDLE.getString("panel.excessPayment.toValue"));
    excessCheckBox.setText(BUNDLE.getString("panel.excessPayment.enable"));

    excessFromTimePanel.setSpinnerNumberModel(new SpinnerNumberModel(37, 1, 420, 1));
    excessFromTimePanel.setPeriod(TimePeriod.MONTH);
    excessFromTimePanel.setComboBoxRenderer(new CustomNameComboBoxRenderer<>(TimePeriod::getSingular));

    excessValueSpinner.setModel(new SpinnerNumberModel(1000., 0., 100_000., 100.));
    bindTimePeriodSpinner(excessValuePeriodComboBox, excessValueSpinner);
    excessValuePeriodComboBox.setRenderer(new CustomNameComboBoxRenderer<>(TimePeriod::getAdverb));
    excessToValueSpinner.setModel(new SpinnerNumberModel(4000., 0., 100_000., 100.));

    ButtonGroup valueRadioGroup = new ButtonGroup();
    valueRadioGroup.add(excessValueRadio);
    valueRadioGroup.add(excessToValueRadio);

    excessValueRadio.setSelected(true);
    excessFromTimePanel.setEnabled(false);
    excessValueSpinner.setEnabled(false);
    excessValuePeriodComboBox.setEnabled(false);
    excessToValueSpinner.setEnabled(false);

    excessValueRadio.addActionListener(e -> {
      boolean excessSelected = excessCheckBox.isSelected();
      boolean selected = excessValueRadio.isSelected();
      excessValueSpinner.setEnabled(selected && excessSelected);
      excessValuePeriodComboBox.setEnabled(selected && excessSelected);
      excessToValueSpinner.setEnabled(!selected && excessSelected);
    });

    excessToValueRadio.addActionListener(e -> {
      boolean excessSelected = excessCheckBox.isSelected();
      boolean selected = excessToValueRadio.isSelected();
      excessValueSpinner.setEnabled(!selected && excessSelected);
      excessValuePeriodComboBox.setEnabled(!selected && excessSelected);
      excessToValueSpinner.setEnabled(selected && excessSelected);
    });

    excessCheckBox.addActionListener(e -> {
      boolean selected = excessCheckBox.isSelected();
      excessFromTimePanel.setEnabled(selected);
      excessValueSpinner.setEnabled(selected && excessValueRadio.isSelected());
      excessValuePeriodComboBox.setEnabled(selected && excessValueRadio.isSelected());
      excessToValueSpinner.setEnabled(selected && excessToValueRadio.isSelected());
      excessValueRadio.setEnabled(selected);
      excessToValueRadio.setEnabled(selected);
    });

    CellConstraints cc = new CellConstraints();
    JPanel excessFromPanel = new JPanel(ViewUtils.getFormLayout(2, 1));
    excessFromPanel.add(excessFromLabel, cc.xy(1, 1));
    excessFromPanel.add(excessFromTimePanel, cc.xy(3, 1));

    JPanel excessValuePanel = new JPanel(ViewUtils.getFormLayout(3, 1));
    excessValuePanel.add(excessValueRadio, cc.xy(1, 1));
    excessValuePanel.add(excessValueSpinner, cc.xy(3, 1));
    excessValuePanel.add(excessValuePeriodComboBox, cc.xy(5, 1));

    JPanel excessToValuePanel = new JPanel(ViewUtils.getFormLayout(2, 1));
    excessToValuePanel.add(excessToValueRadio, cc.xy(1, 1));
    excessToValuePanel.add(excessToValueSpinner, cc.xy(3, 1));

    panel.add(excessCheckBox, cc.xy(1, 1));
    panel.add(excessFromPanel, cc.xy(1, 3));
    panel.add(excessValuePanel, cc.xy(1, 5));
    panel.add(excessToValuePanel, cc.xy(1, 7));

    return panel;
  }

  private Stream<PromotionPanel> streamPromotionPanels() {
    return IntStream.range(0, promotionsPanel.getComponentCount() - 1)
            .mapToObj(promotionsPanel::getComponent)
            .filter(PromotionPanel.class::isInstance)
            .map(PromotionPanel.class::cast);
  }

  private static class PromotionPanel extends JPanel {
    private final Spinner.Double marginSpinner = new Spinner.Double();
    private final TimePeriodPanel timePeriodPanel = new TimePeriodPanel();

    private PromotionPanel(boolean isFirst) {
      JLabel marginLabel = new JLabel(BUNDLE.getString("panel.promotion.margin"));
      String timeTextKey = isFirst ? "panel.promotion.timeFirst" : "panel.promotion.timeNext";
      JLabel timeLabel = new JLabel(BUNDLE.getString(timeTextKey));

      marginSpinner.setModel(new SpinnerNumberModel(1.5, -100., 100., 0.1));
      timePeriodPanel.setSpinnerNumberModel(new SpinnerNumberModel(5, 1, 35, 1));

      add(marginLabel);
      add(marginSpinner);
      add(timeLabel);
      add(timePeriodPanel);
    }

    private PromotionParams getParams() {
      return new PromotionParams(
              timePeriodPanel.getMonthPeriod(),
              marginSpinner.doubleValue() / 100.
      );
    }

    private void load(Properties properties) {
      PropertiesUtils.loadDouble(properties, "margin").ifPresent(marginSpinner::setValue);
      PropertiesUtils.loadInt(properties, "time").ifPresent(timePeriodPanel::setTime);
      PropertiesUtils.loadEnum(properties, "period", TimePeriod::valueOf).ifPresent(timePeriodPanel::setPeriod);
    }

    private Properties store() {
      Properties properties = new Properties();

      properties.put("margin", marginSpinner.stringValue());
      properties.put("time", timePeriodPanel.getTimeString());
      PropertiesUtils.storeEnum(properties, "period", timePeriodPanel.getPeriod());

      return properties;
    }
  }

  public void load(Properties properties) {
    PropertiesUtils.loadDouble(properties, "loanValue").ifPresent(loanValueSpinner::setValue);
    PropertiesUtils.loadEnum(properties, "loanTimePeriod", TimePeriod::valueOf).ifPresent(loanTimePanel::setPeriod);
    PropertiesUtils.loadInt(properties, "loanTime").ifPresent(loanTimePanel::setTime);
    PropertiesUtils.loadDouble(properties, "margin").ifPresent(marginSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "baseRate").ifPresent(baseRateSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "provision").ifPresent(provisionSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "otherStartCosts").ifPresent(otherStartCostsSpinner::setValue);
    PropertiesUtils.loadEnum(properties, "installmentType", InstallmentType::valueOf).ifPresent(installmentTypeComboBox::setSelectedItem);
    PropertiesUtils.loadDate(properties, "payment").ifPresent(paymentSpinner::setValue);
    PropertiesUtils.loadDate(properties, "firstInstallment").ifPresent(firstInstallmentSpinner::setValue);
    PropertiesUtils.loadBoolean(properties, "installmentOnlyInWorkDay").ifPresent(installmentOnlyInWorkDayCheckBox::setSelected);

    PropertiesUtils.loadEnum(properties, "estatePeriod", TimePeriod::valueOf).ifPresent(estatePeriodComboBox::setSelectedItem);
    PropertiesUtils.loadDouble(properties, "estateValue").ifPresent(estateValueSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "estateAmount").ifPresent(estateAmountSpinner::setValue);
    PropertiesUtils.loadBoolean(properties, "estateAmountEqualsMortgageValue").ifPresent(estateAmountEqualsMortgageValueCheckBox::setSelected);

    PropertiesUtils.loadBoolean(properties, "lifeInsurance").ifPresent(lifeInsuranceCheckBox::setSelected);
    PropertiesUtils.loadEnum(properties, "lifePeriod", TimePeriod::valueOf).ifPresent(lifePeriodComboBox::setSelectedItem);
    PropertiesUtils.loadDouble(properties, "lifeValue").ifPresent(lifeValueSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "lifeAmount").ifPresent(lifeAmountSpinner::setValue);
    PropertiesUtils.loadBoolean(properties, "lifeAmountEqualsMortgageValue").ifPresent(lifeAmountEqualsMortgageValueCheckBox::setSelected);
    PropertiesUtils.loadBoolean(properties, "lifeInAdvance").ifPresent(lifeInAdvanceCheckBox::setSelected);
    PropertiesUtils.loadEnum(properties, "lifeInAdvanceTimePeriod", TimePeriod::valueOf).ifPresent(lifeInAdvanceTimePanel::setPeriod);
    PropertiesUtils.loadInt(properties, "lifeInAdvanceTime").ifPresent(lifeInAdvanceTimePanel::setTime);
    PropertiesUtils.loadBoolean(properties, "lifeValueInAdvance").ifPresent(lifeValueInAdvanceCheckBox::setSelected);
    PropertiesUtils.loadDouble(properties, "lifeValueInAdvanceValue").ifPresent(lifeValueInAdvanceValueSpinner::setValue);

    PropertiesUtils.loadBoolean(properties, "loan2Percent").ifPresent(loan2PercentCheckBox::setSelected);
    PropertiesUtils.loadDouble(properties, "loan2PercentValue").ifPresent(loan2PercentValueSpinner::setValue);

    PropertiesUtils.loadBoolean(properties, "excess").ifPresent(excessCheckBox::setSelected);
    PropertiesUtils.loadEnum(properties, "excessFromPeriod", TimePeriod::valueOf).ifPresent(excessFromTimePanel::setPeriod);
    PropertiesUtils.loadInt(properties, "excessFrom").ifPresent(excessFromTimePanel::setTime);
    PropertiesUtils.loadEnum(properties, "excessValuePeriod", TimePeriod::valueOf).ifPresent(excessValuePeriodComboBox::setSelectedItem);
    PropertiesUtils.loadDouble(properties, "excessValue").ifPresent(excessValueSpinner::setValue);
    PropertiesUtils.loadDouble(properties, "excessToValue").ifPresent(excessToValueSpinner::setValue);
    PropertiesUtils.loadBoolean(properties, "excessToValueSelected").ifPresent(excessToValueRadio::setSelected);

    PropertiesUtils.loadStream(properties, "promotions")
            .forEach(props -> addPromotionPanel().load(props));

    estateAmountEqualsMortgageValueCheckBox.fireActionPerformed();
    lifeInsuranceCheckBox.fireActionPerformed();
    lifeAmountEqualsMortgageValueCheckBox.fireActionPerformed();
    lifeInAdvanceCheckBox.fireActionPerformed();
    lifeValueInAdvanceCheckBox.fireActionPerformed();
    loan2PercentCheckBox.fireActionPerformed();
    excessCheckBox.fireActionPerformed();
  }

  public Properties store() {
    Properties properties = new Properties();

    properties.put("loanValue", loanValueSpinner.stringValue());
    properties.put("loanTime", loanTimePanel.getTimeString());
    PropertiesUtils.storeEnum(properties, "loanTimePeriod", loanTimePanel.getPeriod());
    properties.put("margin", marginSpinner.stringValue());
    properties.put("baseRate", baseRateSpinner.stringValue());
    properties.put("provision", provisionSpinner.stringValue());
    properties.put("otherStartCosts", otherStartCostsSpinner.stringValue());
    PropertiesUtils.storeEnum(properties, "installmentType", installmentTypeComboBox.getSelectedItem());
    properties.put("payment", paymentSpinner.stringValue());
    properties.put("firstInstallment", firstInstallmentSpinner.stringValue());
    properties.put("installmentOnlyInWorkDay", Boolean.toString(installmentOnlyInWorkDayCheckBox.isSelected()));

    properties.put("estateValue", estateValueSpinner.stringValue());
    PropertiesUtils.storeEnum(properties, "estatePeriod", estatePeriodComboBox.getSelectedItem());
    properties.put("estateAmount", estateAmountSpinner.stringValue());
    properties.put("estateAmountEqualsMortgageValue", Boolean.toString(estateAmountEqualsMortgageValueCheckBox.isSelected()));

    properties.put("lifeInsurance", Boolean.toString(lifeInsuranceCheckBox.isSelected()));
    properties.put("lifeValue", lifeValueSpinner.stringValue());
    PropertiesUtils.storeEnum(properties, "lifePeriod", lifePeriodComboBox.getSelectedItem());
    properties.put("lifeAmount", lifeAmountSpinner.stringValue());
    properties.put("lifeAmountEqualsMortgageValue", Boolean.toString(lifeAmountEqualsMortgageValueCheckBox.isSelected()));
    properties.put("lifeInAdvance", Boolean.toString(lifeInAdvanceCheckBox.isSelected()));
    properties.put("lifeInAdvanceTime", lifeInAdvanceTimePanel.getTimeString());
    PropertiesUtils.storeEnum(properties, "lifeInAdvanceTimePeriod", lifeInAdvanceTimePanel.getPeriod());
    properties.put("lifeValueInAdvance", Boolean.toString(lifeValueInAdvanceCheckBox.isSelected()));
    properties.put("lifeValueInAdvanceValue", lifeValueInAdvanceValueSpinner.stringValue());

    PropertiesUtils.storeStream(streamPromotionPanels().map(PromotionPanel::store), properties, "promotions");

    properties.put("loan2Percent", Boolean.toString(loan2PercentCheckBox.isSelected()));
    properties.put("loan2PercentValue", loan2PercentValueSpinner.stringValue());

    properties.put("excess", Boolean.toString(excessCheckBox.isSelected()));
    properties.put("excessFrom", excessFromTimePanel.getTimeString());
    PropertiesUtils.storeEnum(properties, "excessFromPeriod", excessFromTimePanel.getPeriod());
    properties.put("excessValue", excessValueSpinner.stringValue());
    PropertiesUtils.storeEnum(properties, "excessValuePeriod", excessValuePeriodComboBox.getSelectedItem());
    properties.put("excessToValue", excessToValueSpinner.stringValue());
    properties.put("excessToValueSelected", Boolean.toString(excessToValueRadio.isSelected()));

    return properties;
  }
}
