package com.github.aklakina.edmma.humanInterface.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AggregationColumnRenderer implements TableCellRenderer {

    private final DefaultLabel label = new DefaultLabel();

    public int calculateValue(JTable table, int row) {
        int sum = 0;
        for (int i = 1; i < table.getColumnCount() - 1; i++) {
            if (table.getValueAt(row, i) != null) {
                sum += (int) table.getValueAt(row, i);
            }
        }
        return sum;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.setText(String.valueOf(calculateValue(table, row)));
        return label;
    }
}
