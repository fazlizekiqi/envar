package com.fazlizekiqi.envar;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.fazlizekiqi.envar.MyToolWindowFactory.NEW_KEY;

public class HighlightNewRowRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Check if the row contains "<New-Key>" in the first column
        String keyValue = (String) table.getValueAt(row, 0); // Column 0 is the "Key" column
        if (NEW_KEY.equals(keyValue)) {
            c.setBackground(JBColor.GRAY); // Set a light gray background for new rows
        } else {
            c.setBackground(table.getBackground()); // Reset to default background (white) for other rows
        }

        return c;
    }
}