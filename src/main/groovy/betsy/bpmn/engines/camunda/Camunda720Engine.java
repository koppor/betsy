package betsy.bpmn.engines.camunda;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;

public class Camunda720Engine extends Camunda710Engine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.2.0");
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-7.0.50";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.2.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }

}
