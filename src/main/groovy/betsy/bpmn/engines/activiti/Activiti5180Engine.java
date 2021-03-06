package betsy.bpmn.engines.activiti;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.tasks.FileTasks;

import java.util.Optional;

/**
 * Activiti 5.18.0
 *
 */
public class Activiti5180Engine extends ActivitiEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "activiti", "5.18.0");
    }

    @Override
    public void install() {
        ActivitiInstaller installer = new ActivitiInstaller();
        installer.setFileName("activiti-5.18.0.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.4.3.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"<!--","");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"-->","");
    }
}

