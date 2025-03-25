package com.fazlizekiqi.envar;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ShowMyCustomToolWindowAction extends DefaultActionGroup {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get the current project
        Project project = e.getProject();

        RunManager instance = RunManager.getInstance(project);
        RunnerAndConfigurationSettings selectedConfiguration = instance.getSelectedConfiguration();

        if (selectedConfiguration != null) {
            RunConfiguration configuration = selectedConfiguration.getConfiguration();
            if (configuration instanceof CommonProgramRunConfigurationParameters) {
                CommonProgramRunConfigurationParameters cmn = (CommonProgramRunConfigurationParameters) configuration;

                Map<String, String> newEnvs = cmn.getEnvs();
                if (project != null && !newEnvs.isEmpty()) {
                    // Access the ToolWindowManager
                    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                    // Show or hide the tool window

                    toolWindowManager.getToolWindow("Environment Variables").show();


                }
            }
        }


    }
}
