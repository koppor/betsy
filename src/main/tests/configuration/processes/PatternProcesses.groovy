package configuration.processes

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation

/**
 * Created with IntelliJ IDEA.
 * User: joerg
 * Date: 18.03.13
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
class PatternProcesses {

    private Process buildPatternProcess(String name, List<TestCase> testCases) {
        new Process(bpel: "patterns/control-flow/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    public final Process SEQUENCE_PATTERN = buildPatternProcess(
            "SequencePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final List<Process> CONTROL_FLOW_PATTERNS = [
           SEQUENCE_PATTERN
    ].flatten() as List<Process>


}
