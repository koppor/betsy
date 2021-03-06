package betsy.bpmn.engines.activiti;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.PropertyTasks;

import java.util.Optional;

public class Activiti5151Engine extends ActivitiEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "activiti", "5.15.1");
    }

    @Override
    public void install() {
        ActivitiInstaller installer = new ActivitiInstaller();
        installer.setFileName("activiti-5.15.1.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.1.3.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
    }

}
