package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MissionColumnRenderer implements TableCellRenderer {

    private final DefaultLabel label = new DefaultLabel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Mission mission = (Mission) value;

        label.setText(String.valueOf(mission.getKillsLeft()));

        if (mission.isShareable())
            label.setIcon(Resources.getWingedIcon());

        return label;
    }
}
