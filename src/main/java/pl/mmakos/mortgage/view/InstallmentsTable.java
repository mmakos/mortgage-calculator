package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.InstallmentsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static pl.mmakos.mortgage.model.InstallmentsTableModel.*;

public class InstallmentsTable extends JTable {
  public InstallmentsTable(InstallmentsTableModel tableModel) {
    super(tableModel);

    setRenderers();
  }

  private void setRenderers() {
    columnModel.getColumn(CAPITAL_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(INTEREST_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(CAPITAL_AND_INTEREST_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(SUP_2_PERCENT_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(SUP_2_INSTALLMENT_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(ESTATE_INSURANCE_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(LIFE_INSURANCE_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(INSURANCE_INSTALLMENT_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(EXCESS_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(INSTALLMENT_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(LEFT_COLUMN).setCellRenderer(currencyCellRenderer());
    columnModel.getColumn(MARGIN_COLUMN).setCellRenderer(percentageCellRenderer());
    columnModel.getColumn(RATE_COLUMN).setCellRenderer(percentageCellRenderer());
  }

  private TableCellRenderer currencyCellRenderer() {
    return formatCellRenderer(NumberFormat.getCurrencyInstance());
  }

  private TableCellRenderer percentageCellRenderer() {
    return formatCellRenderer(new DecimalFormat("#.##### %"));
  }

  private TableCellRenderer formatCellRenderer(NumberFormat numberFormat) {
    return new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setText(numberFormat.format(((Number) value).doubleValue()));
        return label;
      }
    };
  }
}
