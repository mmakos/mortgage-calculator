package pl.mmakos.mortgage;

import lombok.extern.slf4j.Slf4j;
import pl.mmakos.mortgage.view.MortgageFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
public class MortgageCalculator {
  public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("pl.mmakos.mortgage.Bundle");
  public static final DecimalFormat CURRENCY_FORMAT = (DecimalFormat) NumberFormat.getCurrencyInstance();
  public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##### '%'");
  private static final String CONFIG_SAVE_FILE = ".mortgage-calculator.config";

  public static void main(String[] args) {
    setLookAndFeel();
    Locale.setDefault(new Locale("pl", "PL"));
    ToolTipManager.sharedInstance().setDismissDelay(60_000);

    Properties properties = new Properties();
    try (InputStream stream = Files.newInputStream(Path.of(System.getenv("APPDATA"), CONFIG_SAVE_FILE))) {
      properties.load(stream);
    } catch (IOException e) {
      log.warn("Nie mozna wczytac zapisanej konfiguracji");
    }
    MortgageFrame mainFrame = new MortgageFrame(MortgageCalculator::saveConfig);
    mainFrame.load(properties);

    mainFrame.setSize(1400, 900);
    mainFrame.setExtendedState(mainFrame.getExtendedState() | Frame.MAXIMIZED_BOTH);
    mainFrame.setVisible(true);
    mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  private static void saveConfig(Properties properties) {
    try (OutputStream outputStream = Files.newOutputStream(Path.of(System.getenv("APPDATA"), CONFIG_SAVE_FILE))) {
      properties.store(outputStream, "");
    } catch (IOException ex) {
      log.error("Nie mozna zapisac konfiguracji", ex);
    }
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
