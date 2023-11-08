package pl.mmakos.mortgage.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

import static pl.mmakos.mortgage.MortgageCalculator.BUNDLE;

public class InstallmentsTableModel extends AbstractTableModel {
  public static final int NUMBER_COLUMN = 0;
  public static final int DATE_COLUMN = 1;
  public static final int CAPITAL_COLUMN = 2;
  public static final int INTEREST_COLUMN = 3;
  public static final int CAPITAL_AND_INTEREST_COLUMN = 4;
  public static final int SUP_2_PERCENT_COLUMN = 5;
  public static final int SUP_2_INSTALLMENT_COLUMN = 6;
  public static final int ESTATE_INSURANCE_COLUMN = 7;
  public static final int LIFE_INSURANCE_COLUMN = 8;
  public static final int INSURANCE_INSTALLMENT_COLUMN = 9;
  public static final int EXCESS_COLUMN = 10;
  public static final int INSTALLMENT_COLUMN = 11;
  public static final int LEFT_COLUMN = 12;
  public static final int MARGIN_COLUMN = 13;
  public static final int RATE_COLUMN = 14;
  public static final int TYPE_COLUMN = 15;

  private static final String[] COLUMNS = new String[]{
          BUNDLE.getString("table.column.numberColumn"),
          BUNDLE.getString("table.column.dateColumn"),
          BUNDLE.getString("table.column.capitalColumn"),
          BUNDLE.getString("table.column.interestColumn"),
          BUNDLE.getString("table.column.capitalAndInterestColumn"),
          BUNDLE.getString("table.column.sup2PercentColumn"),
          BUNDLE.getString("table.column.sup2InstallmentColumn"),
          BUNDLE.getString("table.column.estateInsuranceColumn"),
          BUNDLE.getString("table.column.lifeInsuranceColumn"),
          BUNDLE.getString("table.column.insuranceInstallmentColumn"),
          BUNDLE.getString("table.column.excessColumn"),
          BUNDLE.getString("table.column.installmentColumn"),
          BUNDLE.getString("table.column.leftColumn"),
          BUNDLE.getString("table.column.marginColumn"),
          BUNDLE.getString("table.column.rateColumn"),
          BUNDLE.getString("table.column.typeColumn")
  };

  private final List<Installment> installments = new ArrayList<>();

  public void setInstallments(Iterable<Installment> installments) {
    this.installments.clear();
    installments.forEach(this.installments::add);
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    return installments.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Installment installment = installments.get(rowIndex);
    return switch (columnIndex) {
      case NUMBER_COLUMN -> installment.number();
      case DATE_COLUMN -> installment.date();
      case CAPITAL_COLUMN -> installment.capital();
      case INTEREST_COLUMN -> installment.interest();
      case CAPITAL_AND_INTEREST_COLUMN -> installment.capitalAndInterest();
      case SUP_2_PERCENT_COLUMN -> installment.sup2percent();
      case SUP_2_INSTALLMENT_COLUMN -> installment.sumTo2Percent();
      case ESTATE_INSURANCE_COLUMN -> installment.estateInsurance();
      case LIFE_INSURANCE_COLUMN -> installment.lifeInsurance();
      case INSURANCE_INSTALLMENT_COLUMN -> installment.sumToInsurances();
      case EXCESS_COLUMN -> installment.excess();
      case INSTALLMENT_COLUMN -> installment.sumAll();
      case LEFT_COLUMN -> installment.capitalLeft();
      case MARGIN_COLUMN -> installment.margin();
      case RATE_COLUMN -> installment.getRate();
      case TYPE_COLUMN -> installment.installmentType();
      default -> null;
    };
  }
}
