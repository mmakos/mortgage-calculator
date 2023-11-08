package pl.mmakos.mortgage;

import lombok.extern.slf4j.Slf4j;
import pl.mmakos.mortgage.model.Installment;
import pl.mmakos.mortgage.model.LoanParams;
import pl.mmakos.mortgage.view.MortgageOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
public class MortgageCalculator {
  public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("pl.mmakos.mortgage.Bundle");
  private static final String CONFIG_SAVE_FILE = ".mortgage-calculator.config";

  public static void main(String[] args) {
    setLookAndFeel();

    JFrame mainFrame = new JFrame(BUNDLE.getString("mortgage.calculator"));
    MortgageOptionsPanel mortgageOptionsPanel = new MortgageOptionsPanel();
    try (InputStream stream = Files.newInputStream(Path.of(System.getenv("APPDATA"), CONFIG_SAVE_FILE))) {
      Properties properties = new Properties();
      properties.load(stream);
      mortgageOptionsPanel.load(properties);
    } catch (IOException e) {
      log.warn("Nie mozna wczytac zapisanej konfiguracji");
    }

    JPanel contentPane = new JPanel(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(mortgageOptionsPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(10);
    contentPane.add(scrollPane, BorderLayout.CENTER);
    JPanel buttonPanel = getButtonPanel(mortgageOptionsPanel);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    mainFrame.getRootPane().setDefaultButton((JButton) buttonPanel.getComponent(0));
    mainFrame.setContentPane(contentPane);
    mainFrame.setSize(600, 900);
    mainFrame.setVisible(true);
  }

  private static JPanel getButtonPanel(MortgageOptionsPanel mortgageOptionsPanel) {
    JButton applyButton = new JButton(BUNDLE.getString("button.apply"));
    applyButton.addActionListener(e -> {
      LoanParams params = mortgageOptionsPanel.getParams();
      System.err.println(params);
      System.err.println();
      Installment initial = Installment.initial(params);
      for (Installment installment : initial) {
        System.err.println(installment);
      }
    });

    JButton saveButton = new JButton(BUNDLE.getString("button.save"));
    saveButton.addActionListener(e -> {
      try (OutputStream outputStream = Files.newOutputStream(Path.of(System.getenv("APPDATA"), CONFIG_SAVE_FILE))) {
        mortgageOptionsPanel.store().store(outputStream, "");
      } catch (IOException ex) {
        log.error("Nie mozna zapisac konfiguracji", ex);
      }
    });

    JPanel panel = new JPanel();
    panel.add(applyButton);
    panel.add(saveButton);
    return panel;
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
    } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException |
             ClassNotFoundException exception) {
      log.error("Nie udalo sie ustawic look and feel FlatDarculaLaf", exception);
    }
  }
}
