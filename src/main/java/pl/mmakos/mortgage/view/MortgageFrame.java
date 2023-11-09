package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.Installment;
import pl.mmakos.mortgage.model.InstallmentsTableModel;
import pl.mmakos.mortgage.model.LoanParams;
import pl.mmakos.mortgage.model.Summary;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;
import static pl.mmakos.mortgage.utils.ViewUtils.c;

public class MortgageFrame extends JFrame {
  private final MortgageOptionsPanel mortgageOptionsPanel = new MortgageOptionsPanel();
  private final InitialInstallmentPanel initialInstallmentPanel = new InitialInstallmentPanel();
  private final SummaryInstallmentsPanel summaryInstallmentsPanel = new SummaryInstallmentsPanel();
  private final InstallmentsTableModel tableModel = new InstallmentsTableModel();

  public MortgageFrame(Consumer<Properties> configSave) {
    super(BUNDLE.getString("mortgage.calculator"));

    JPanel buttonPanel = getButtonPanel(configSave);

    JPanel leftPanel = new JPanel(new BorderLayout());
    JPanel rightPanel = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel(new GridBagLayout());

    JScrollPane scrollPane = new JScrollPane(mortgageOptionsPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    leftPanel.add(scrollPane, BorderLayout.CENTER);
    leftPanel.add(buttonPanel, BorderLayout.SOUTH);

    topPanel.add(initialInstallmentPanel, c(1, 1, 1, 0, 0));
    topPanel.add(summaryInstallmentsPanel, c(2, 1, 1, 5, 0));

    InstallmentsTable installmentsTable = new InstallmentsTable(tableModel);
    rightPanel.add(topPanel, BorderLayout.NORTH);
    rightPanel.add(new JScrollPane(installmentsTable), BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(leftPanel);
    splitPane.setRightComponent(rightPanel);
    splitPane.setDividerLocation(leftPanel.getPreferredSize().width + 10);

    setContentPane(splitPane);
  }

  public void load(Properties properties) {
    if (properties.isEmpty()) return;
    mortgageOptionsPanel.load(properties);
    proceed();
  }

  private JPanel getButtonPanel(Consumer<Properties> configSave) {
    JButton applyButton = new JButton(BUNDLE.getString("button.apply"));
    applyButton.addActionListener(e -> proceed());
    getRootPane().setDefaultButton(applyButton);

    JButton saveButton = new JButton(BUNDLE.getString("button.save"));
    saveButton.addActionListener(e -> configSave.accept(mortgageOptionsPanel.store()));

    JPanel panel = new JPanel();
    panel.add(applyButton);
    panel.add(saveButton);
    return panel;
  }

  private void proceed() {
    LoanParams params = mortgageOptionsPanel.getParams();
    Installment initial = Installment.initial(params);
    List<Installment> installments = new ArrayList<>();
    initial.forEach(installments::add);
    initialInstallmentPanel.update(initial);
    summaryInstallmentsPanel.update(Summary.fromInstallments(initial, installments));
    tableModel.setInstallments(installments);
  }
}
