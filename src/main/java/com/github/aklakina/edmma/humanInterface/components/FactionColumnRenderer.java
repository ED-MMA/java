package com.github.aklakina.edmma.humanInterface.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This class extends the DefaultTableCellRenderer to provide a custom renderer for a table column.
 * It is used to display the value of a cell in a table in a custom way.
 */
public class FactionColumnRenderer extends DefaultTableCellRenderer {

    // A label used to display the value in the cell.
    private final DefaultLabel label = new DefaultLabel();

    /**
     * This method is overridden from DefaultTableCellRenderer.
     * It is called when a cell is rendered.
     * It sets the text of the label to the value of the cell and returns the label as the component to be rendered.
     *
     * @param table The table containing the cell.
     * @param value The value of the cell.
     * @param isSelected Whether the cell is selected.
     * @param hasFocus Whether the cell has focus.
     * @param row The index of the row containing the cell.
     * @param column The index of the column containing the cell.
     * @return The component to be rendered (a label with the value of the cell).
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.setInnerLabel((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
        label.setText((String) value);
        return label;
    }
}