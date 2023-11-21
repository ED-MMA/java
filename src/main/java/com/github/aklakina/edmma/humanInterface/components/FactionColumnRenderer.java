package com.github.aklakina.edmma.humanInterface.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FactionColumnRenderer implements TableCellRenderer {

    private final DefaultLabel label = new DefaultLabel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        label.setText((String) value);
        return label;
    }
}
