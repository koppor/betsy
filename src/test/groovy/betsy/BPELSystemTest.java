package betsy;

import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BPELSystemTest extends AbstractSystemTest{

    @BeforeClass
    public static void disableSoapUIShutdown() {
        BPELMain.shutdownSoapUiAfterCompletion(false);
    }

    @AfterClass
    public static void tearDownSoapUI() {
        SoapUIShutdownHelper.shutdownSoapUIForReal();
    }

    @Test
    public void testBPMNEngine() throws IOException {
        testBPMNEngine("activiti");
    }

    @Test
    public void test_A_BpmnActiviti5170SequenceFlow() throws IOException {
        testBPMNEngine("activiti5170");
    }

    @Test
    public void test_A_BpmnCamunda700SequenceFlow() throws IOException {
        testBPMNEngine("camunda");
    }

    @Test
    public void test_A_BpmnCamunda710SequenceFlow() throws IOException {
        testBPMNEngine("camunda710");
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws IOException {
        testBPMNEngine("camunda720");
    }

    @Test
    public void test_A_BpmnCamunda730SequenceFlow() throws IOException {
        testBPMNEngine("camunda730");
    }

    @Test
    public void test_A_BpmnjBPMSequenceFlow() throws IOException {
        testBPMNEngine("jbpm");
    }

    @Test
    public void test_A_BpmnjBPM610SequenceFlow() throws IOException {
        testBPMNEngine("jbpm610");
    }

    @Test
    public void test_A_BpmnjBPM620SequenceFlow() throws IOException {
        testBPMNEngine("jbpm620");
    }

    private void testBPMNEngine(String engine) throws IOException {
        Main.main("bpmn", "-f", "test-" + engine, engine, "SequenceFlow");
        assertEquals("[SequenceFlow;" + engine + ";basics;1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

    @Test
    public void test_B1_BpelOdeSequence() throws IOException, InterruptedException {
        testBPELEngine("ode");
    }

    @Test
    public void test_B1_BpelOdeInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("ode-in-memory");
    }

    @Test
    public void test_B1_BpelOde136Sequence() throws IOException, InterruptedException {
        testBPELEngine("ode136");
    }

    @Test
    public void test_B1_BpelOde136InMemorySequence() throws IOException, InterruptedException {
        testBPELEngine("ode136-in-memory");
    }

    @Test
    public void test_B2_BpelOrchestraSequence() throws IOException, InterruptedException {
        testBPELEngine("orchestra");
    }

    @Test
    public void test_B3_BpelBpelgSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg");
    }

    @Test
    public void test_B3_BpelBpelgInvokeSync() throws IOException, InterruptedException {
        testBPELEngine("bpelg", "basic", "Invoke-Sync");
    }

    @Test
    public void test_B3_BpelBpelgInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg-in-memory");
    }

    @Test @Ignore("does not work on *nix when starting in the background as a deamon service")
    public void test_B4_BpelWso212Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v2_1_2");
    }

    @Test
    public void test_B4_BpelWso300Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_0_0");
    }

    @Test
    public void test_B4_BpelWso310Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_1_0");
    }

    @Test
    public void test_B4_BpelWso320Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_2_0");
    }

    @Test
    public void test_B5_A_BpelOpenesb301StandaloneSequence() throws IOException, InterruptedException {
        testBPELEngine("openesb301standalone");
    }

    @Test
    public void test_B5_B2_BpelOpenesb23Sequence() throws IOException, InterruptedException {
        testBPELEngine("openesb23");
    }

    @Test
    public void test_B5__B3_BpelOpenesb231Sequence() throws IOException, InterruptedException {
        testBPELEngine("openesb231");
    }

    @Test
    public void test_B5__B1_BpelOpenesbSequence() throws IOException, InterruptedException {
        testBPELEngine("openesb");
    }

    @Test
    public void test_B6_BpelActiveBpelSequence() throws IOException, InterruptedException {
        testBPELEngine("active-bpel");
    }

    @Test
    public void test_B7_BpelPetalsesbSequence() throws IOException, InterruptedException {
        testBPELEngine("petalsesb");
    }

    @Test
    public void test_B7_BpelPetalsesb41Sequence() throws IOException, InterruptedException {
        testBPELEngine("petalsesb41");
    }


    private void testBPELEngine(String engine) throws IOException {
        testBPELEngine(engine, "structured", "Sequence");
    }

    private void testBPELEngine(String engine, String group, String process) throws IOException {
        BPELMain.main(engine, process, "-f", "test-" + engine);
        assertEquals("[" + process + ";" + engine + ";" + group + ";1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}
