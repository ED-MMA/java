package com.github.aklakina.edmma.humanInterface.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * This class extends the DefaultTableCellRenderer to provide a custom renderer for a table column.
 * It is used to aggregate the values of all cells in a row and display the sum in the cell rendered by this class.
 */
public class AggregationColumnRenderer extends DefaultTableCellRenderer {

    // A label used to display the aggregated value in the cell.
    private final DefaultLabel label = new DefaultLabel();

    /**
     * This method calculates the sum of all cell values in a row.
     * It iterates over all cells in the row, checks if the cell value is not null and adds it to the sum.
     *
     * @param table The table containing the row.
     * @param row The index of the row in the table.
     * @return The sum of all cell values in the row.
     */
    public int calculateValue(JTable table, int row) {
        int sum = 0;
        for (int i = 1; i < table.getColumnCount() - 1; i++) {
            if (table.getValueAt(row, i) != null) {
                sum += (int) table.getValueAt(row, i);
            }
        }
        return sum;
    }

    /**
     * This method is overridden from DefaultTableCellRenderer.
     * It is called when a cell is rendered.
     * It sets the text of the label to the sum of all cell values in the row and returns the label as the component to be rendered.
     *
     * @param table The table containing the cell.
     * @param value The value of the cell.
     * @param isSelected Whether the cell is selected.
     * @param hasFocus Whether the cell has focus.
     * @param row The index of the row containing the cell.
     * @param column The index of the column containing the cell.
     * @return The component to be rendered (a label with the sum of all cell values in the row).
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.setInnerLabel((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
        label.setText(String.valueOf(calculateValue(table, row)));
        return label;
    }
}