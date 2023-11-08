package pl.mmakos.mortgage.view;

import pl.mmakos.mortgage.model.ExplainedValue;
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
    columnModel.getColumn(DATE_COLUMN).setCellRenderer(new ExplainedCellRenderer());
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
    columnModel.getColumn(TYPE_COLUMN).setCellRenderer(new ExplainedCellRenderer());
  }

  private TableCellRenderer currencyCellRenderer() {
    return formatCellRenderer(NumberFormat.getCurrencyInstance());
  }

  private TableCellRenderer percentageCellRenderer() {
    return formatCellRenderer(new DecimalFormat("#.##### %"));
  }

  private TableCellRenderer formatCellRenderer(NumberFormat numberFormat) {
    return new ExplainedCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof ExplainedValue<?> explainedValue) {
          label.setText(numberFormat.format(((Number) explainedValue.value()).doubleValue()));
        } else {
          label.setText(numberFormat.format(((Number) value).doubleValue()));
        }
        return label;
      }
    };
  }

  private static class ExplainedCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      ExplainedValue<?> explainedValue = (ExplainedValue<?>) value;
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, explainedValue.value(), isSelected, hasFocus, row, column);
      String explanation = explainedValue.explanation();
      if (explanation != null) {
        label.setToolTipText(explanation);
      }
      return label;
    }
  }
}
