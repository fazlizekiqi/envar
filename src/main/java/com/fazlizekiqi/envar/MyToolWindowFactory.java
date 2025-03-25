package com.fazlizekiqi.envar;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MyToolWindowFactory extends DefaultActionGroup implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(com.intellij.openapi.project.Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JBLabel label = new JBLabel("Current configuration:");
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Key", "Value"}, 0) {};
        JTable envTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(envTable);
        panel.add(label);
        panel.add(scrollPane);


        EnvVariableListener envVariableListener = project.getService(EnvVariableListener.class);
        envVariableListener.init(tableModel, label, envTable);

        SwingUtilities.invokeLater(envVariableListener::updateTableFromConfiguration);
        SimpleToolWindowPanel toolWindowPanel = new SimpleToolWindowPanel(false, true);
        toolWindowPanel.setContent(panel);
        toolWindow.getComponent().add(toolWindowPanel);
    }

}