package se.docflow.envar;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.messages.MessageBusConnection;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;


@Service(Service.Level.PROJECT)
public final class EnvVariableListener {
    private DefaultTableModel tableModel;
    private JBLabel label;
    private final Project project;
    private JTable envTable;

    public EnvVariableListener(Project project) {
        this.project = project;
    }


    public void init(DefaultTableModel tableModel, JBLabel label, JTable envTable) {
        this.tableModel = tableModel;
        this.label = label;
        this.envTable = envTable;

        subscribeToRunManager();
        addTableChangeListener(); // Add listener to detect table edits
    }


    private void subscribeToRunManager() {
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(RunManagerListener.TOPIC, new RunManagerListener() {
            @Override
            public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings) {
                updateTableFromConfiguration();
            }

            @Override
            public void runConfigurationSelected(@Nullable RunnerAndConfigurationSettings settings) {
                updateTableFromConfiguration();
            }
        });
    }

    void updateTableFromConfiguration() {
        SwingUtilities.invokeLater(() -> {
            RunManager instance = RunManager.getInstance(project);
            RunnerAndConfigurationSettings selectedConfiguration = instance.getSelectedConfiguration();

            if (selectedConfiguration != null) {
                RunConfiguration configuration = selectedConfiguration.getConfiguration();
                if (configuration instanceof CommonProgramRunConfigurationParameters) {
                    CommonProgramRunConfigurationParameters cmn = (CommonProgramRunConfigurationParameters) configuration;

                    Map<String, String> newEnvs = cmn.getEnvs();
                    updateTable(newEnvs);
                    label.setText("Current configuration : " + configuration.getName());
                }
            }
        });
    }

    private void updateTable(Map<String, String> newData) {
        tableModel.setRowCount(0); // Clear existing rows
        newData.forEach((key, value) -> tableModel.addRow(new Object[]{key, value}));
    }

    private void addTableChangeListener() {
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                updateRunConfigurationFromTable();
            }
        });
    }

    private void updateRunConfigurationFromTable() {
        SwingUtilities.invokeLater(() -> {
            RunManager instance = RunManager.getInstance(project);
            RunnerAndConfigurationSettings selectedConfiguration = instance.getSelectedConfiguration();

            if (selectedConfiguration != null) {
                RunConfiguration configuration = selectedConfiguration.getConfiguration();
                if (configuration instanceof CommonProgramRunConfigurationParameters) {
                    CommonProgramRunConfigurationParameters cmn =
                            (CommonProgramRunConfigurationParameters) configuration;

                    Map<String, String> updatedEnvs = new HashMap<>();
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String key = (String) tableModel.getValueAt(i, 0);
                        String value = (String) tableModel.getValueAt(i, 1);
                        updatedEnvs.put(key, value);
                    }

                    cmn.setEnvs(updatedEnvs);
                    instance.setTemporaryConfiguration(selectedConfiguration);
                }
            }
        });
    }
}
