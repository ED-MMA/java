package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This class extends the DefaultTableCellRenderer to provide a custom renderer for a table column.
 * It is used to display the value of a cell in a table in a custom way.
 * Specifically, it displays the number of kills left in a mission and a winged icon if the mission is shareable.
 */
public class MissionColumnRenderer extends DefaultTableCellRenderer {

    // A label used to display the value in the cell.
    private final DefaultLabel label = new DefaultLabel();

    /**
     * This method is overridden from DefaultTableCellRenderer.
     * It is called when a cell is rendered.
     * It sets the text of the label to the number of kills left in the mission and the icon to a winged icon if the mission is shareable.
     * It then returns the label as the component to be rendered.
     *
     * @param table The table containing the cell.
     * @param value The value of the cell (a Mission object).
     * @param isSelected Whether the cell is selected.
     * @param hasFocus Whether the cell has focus.
     * @param row The index of the row containing the cell.
     * @param column The index of the column containing the cell.
     * @return The component to be rendered (a label with the number of kills left in the mission and a winged icon if the mission is shareable).
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.setInnerLabel((JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
        Mission mission = (Mission) value;

        label.setText(String.valueOf(mission.getKillsLeft()));

        if (mission.isShareable())
            label.setIcon(Resources.getWingedIcon());

        return label;
    }
}