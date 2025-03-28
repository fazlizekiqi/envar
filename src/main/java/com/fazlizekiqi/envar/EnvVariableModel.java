package com.fazlizekiqi.envar;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class EnvVariableModel {
    private DefaultTableModel tableModel;
    private Map<String, String> envVariables = new HashMap<>();

    public EnvVariableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void setEnvVariables(Map<String, String> envVariables) {
        this.envVariables = envVariables;
        updateTable();
    }

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }

    private void updateTable() {
        tableModel.setRowCount(0);  // Clear existing rows
        envVariables.forEach((key, value) -> tableModel.addRow(new Object[]{key, value}));
    }

    public void addEmptyRow() {
        envVariables.put("", "");  // Add empty entry for new row
        updateTable();
    }

    public void updateEnvVariable(String key, String value) {
        envVariables.put(key, value);
        updateTable();
    }
}

