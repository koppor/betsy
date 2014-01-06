package betsy.virtual.host.engines;

import betsy.data.BetsyProcess;
import betsy.data.engines.orchestra.OrchestraEngine;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.FileMessage;
import betsy.virtual.host.ServiceAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static betsy.config.Configuration.get;
import static betsy.config.Configuration.getValueAsInteger;

public class VirtualOrchestraEngine extends VirtualEngine {

    public static final int HTTP_PORT = 8080;

    public VirtualOrchestraEngine() {
        super();
        this.defaultEngine = new OrchestraEngine();
    }

    @Override
    public String getName() {
        return "orchestra_v";
    }

    @Override
    public List<ServiceAddress> getVerifiableServiceAddresses() {
        List<ServiceAddress> saList = new LinkedList<>();
        saList.add(new ServiceAddress("http://localhost:" + HTTP_PORT + "/orchestra"));
        return saList;
    }

    @Override
    public Set<Integer> getRequiredPorts() {
        Set<Integer> portList = new HashSet<>();
        portList.add(HTTP_PORT);
        return portList;
    }

    @Override
    public String getEndpointUrl(BetsyProcess process) {
        return "http://localhost:" + HTTP_PORT + "/orchestra/" + process.getName() + "TestInterface";
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        // use default engine's operations
        defaultEngine.buildArchives(process);
    }

    @Override
    public Path getXsltPath() {
        return defaultEngine.getXsltPath();
    }

    @Override
    public DeployRequest buildDeployRequest(BetsyProcess process) throws IOException {
        DeployRequest operation = new DeployRequest();
        operation.setFileMessage(FileMessage.build(process.getTargetPackageFilePath("zip")));
        operation.setEngineName(getName());
        operation.setProcessName(process.getName());
        // deploymentLogFile unnecessary
        operation.setDeploymentDir(get("virtual.engines.orchestra_v.deploymentDir"));
        operation.setDeployTimeout(getValueAsInteger("virtual.engines.orchestra_v.deploymentTimeout"));

        return operation;
    }

    @Override
    public LogFilesRequest buildLogFilesRequest() {
        LogFilesRequest request = new LogFilesRequest();
        request.getPaths().add(get("virtual.engines.orchestra_v.bvmsDir") + "/log");
        request.getPaths().add(get("virtual.engines.orchestra_v.logfileDir"));
        return request;
    }
}
