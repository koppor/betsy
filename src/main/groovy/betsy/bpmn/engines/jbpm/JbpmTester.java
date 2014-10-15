package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.camunda.JsonHelper;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestCaseVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.json.JSONObject;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class JbpmTester {
    /**
     * Runs a single test
     */
    public void runTest() {
        //make bin dir
        FileTasks.mkdirs(testBin);
        FileTasks.mkdirs(reportPath);

        // TODO check for deployment errors and omit test execution if process is not deployed

        if (!testCase.getSelfStarting()) {
            //setup variables and start process
            String baseUrl = "http://localhost:8080/jbpm-console/rest/runtime/" + deploymentId + "/process/" + name + "/start";

            Map<String, Object> variables = new HashMap<>();
            for (BPMNTestCaseVariable variable : testCase.getVariables()) {
                variables.put(variable.getName(), variable.getValue());
            }


            StringJoiner joiner = new StringJoiner(",", "?", "");
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
            }


            String requestUrl = baseUrl + joiner.toString();
            try {
                JsonHelper.postStringWithAuth(requestUrl, new JSONObject(), 200, user, password);

                //delay for timer intermediate event
                WaitTasks.sleep(testCase.getDelay());

            } catch (RuntimeException ignored) {
                BPMNTester.appendToFile(getFileName(), "runtimeException");
            }


        } else {
            //delay for self starting
            WaitTasks.sleep(testCase.getDelay());
        }


        BPMNTester.setupPathToToolsJarForJavacAntTask(this);
        BPMNTester.compileTest(testSrc, testBin);
        BPMNTester.executeTest(testSrc, testBin, reportPath);
    }

    private Path getFileName() {
        return logDir.resolve("log" + testCase.getNumber() + ".txt");
    }

    public BPMNTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(BPMNTestCase testCase) {
        this.testCase = testCase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Path getReportPath() {
        return reportPath;
    }

    public void setReportPath(Path reportPath) {
        this.reportPath = reportPath;
    }

    public Path getTestBin() {
        return testBin;
    }

    public void setTestBin(Path testBin) {
        this.testBin = testBin;
    }

    public Path getTestSrc() {
        return testSrc;
    }

    public void setTestSrc(Path testSrc) {
        this.testSrc = testSrc;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private BPMNTestCase testCase;
    private String name;
    private String deploymentId;
    private URL baseUrl;
    private Path reportPath;
    private Path testBin;
    private Path testSrc;
    private Path logDir;
    private String user = "admin";
    private String password = "admin";
}