package com.fazlizekiqi.envar;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyToolWindowFactory extends DefaultActionGroup implements ToolWindowFactory {

    public static final String NEW_KEY = "<New-Key>";
    public static final String NEW_VARIABLE = "<New-Variable>";

    @Override
    public void createToolWindowContent(com.intellij.openapi.project.Project project, @NotNull ToolWindow toolWindow) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Us
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure components fill horizontally

        JBLabel label = new JBLabel("Current configuration");

        DefaultTableModel tableModel = new CustomTableModel(new Object[]{"Key", "Value"}, 0);
        JBTable envTable = new JBTable(tableModel);
        JBScrollPane scrollPane = new JBScrollPane(envTable);
        envTable.setFillsViewportHeight(true);
        envTable.getColumnModel().getColumn(0).setCellRenderer(new HighlightNewRowRenderer());
        envTable.getColumnModel().getColumn(1).setCellRenderer(new HighlightNewRowRenderer());

        JButton addButton = new JButton("Add Variable");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        addButton.setPreferredSize(new Dimension(200, 50)); // Set button's preferred height (adjust as needed)
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        addButton.addActionListener(e -> addRow(tableModel, envTable));

        modifyGbc(gbc, panel, label, scrollPane);
        panel.add(addButton, gbc);

        EnvVariableListener envVariableListener = project.getService(EnvVariableListener.class);
        envVariableListener.init(tableModel, label, envTable);

        SwingUtilities.invokeLater(envVariableListener::updateTableFromConfiguration);
        envTable.addKeyListener(deleteListener(envTable, envVariableListener));
        SimpleToolWindowPanel toolWindowPanel = new SimpleToolWindowPanel(false, true);
        toolWindowPanel.setContent(panel);
        toolWindow.getComponent().add(toolWindowPanel);
    }

    private static void modifyGbc(GridBagConstraints gbc, JPanel panel, JBLabel label, JBScrollPane scrollPane) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Horizontal space taken by the label
        panel.add(label, gbc);

        // Add the table at the top (with grid height of 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0; // Allow table to take as much space as possible
        gbc.fill = GridBagConstraints.BOTH; // Make the table expand in both directions
        panel.add(scrollPane, gbc);

        // Add the button at the bottom
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0; // No extra space for the button
        gbc.fill = GridBagConstraints.HORIZONTAL; // Button should take the full width
    }

    private void addRow(DefaultTableModel tableModel, JBTable envTable) {
        addEmptyRow(tableModel);
        SwingUtilities.invokeLater(() -> {
            int rowCount = tableModel.getRowCount();
            if (rowCount > 0) {
                Rectangle cellRect = envTable.getCellRect(rowCount - 1, 0, true);
                envTable.scrollRectToVisible(cellRect);
            }
        });
    }

    private void addEmptyRow(DefaultTableModel tableModel) {
        tableModel.addRow(new Object[]{NEW_KEY, NEW_VARIABLE});  // Adds a new row with empty values
    }

    private static @org.jetbrains.annotations.NotNull KeyAdapter deleteListener(JBTable envTable, EnvVariableListener envVariableListener) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    int selectedRow = envTable.getSelectedRow();
                    envVariableListener.deleteRow(selectedRow);
                }
            }
        };
    }

}