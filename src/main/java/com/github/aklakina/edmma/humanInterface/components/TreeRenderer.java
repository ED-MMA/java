package com.github.aklakina.edmma.humanInterface.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class TreeRenderer extends DefaultTreeCellRenderer {

    private final TitleLabel label = new TitleLabel();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        label.setText(value.toString());

        return label;
    }
}
