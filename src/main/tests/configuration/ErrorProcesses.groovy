package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.tasks.FileTasks
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ErrorProcesses {

    public static final Map<String, String> inputToErrorCode = [
            "0": "happy-path",
            "11001": "content-empty",
            "12001": "content-simple-text",
            "12002": "content-simple-stackTrace",
            "13101": "content-xml-notWellFormed-missingClosingTag",
            "13102": "content-xml-notWellFormed-twoRootElements",
            "13103": "content-xml-notWellFormed-missingAttributeClosing",
            "13211": "content-xml-wrongFormat-elementWithoutContent-emptyElementShort",
            "13212": "content-xml-wrongFormat-elementWithoutContent-emptyElementLong",
            "13221": "content-xml-wrongFormat-dataFormats-stringInstreadOfInteger",
            "13222": "content-xml-wrongFormat-dataFormats-doubleInsteadOfInteger",
            "13223": "content-xml-wrongFormat-dataFormats-doubleWithCommaInsteadOfInteger",
            "13231": "content-xml-wrongFormat-namespace-noNamespace",
            "13232": "content-xml-wrongFormat-namespace-partiallyUsageOfNamespace",
            "13233": "content-xml-wrongFormat-namespace-wrongNamespace",
            "13234": "content-xml-wrongFormat-namespace-unusedPrefix",
            "13241": "content-xml-wrongFormat-additionalContent-element",
            "13242": "content-xml-wrongFormat-additionalContent-attribute"
    ]

    public static List<BetsyProcess> getProcesses() {

        Path errorsDir = Paths.get("src/main/tests/files/errors")
        FileTasks.deleteDirectory(errorsDir)
        FileTasks.mkdirs(errorsDir)

        List<BetsyProcess> result = new LinkedList<>(); ;

        result.addAll(createTestsForInvokeSync(errorsDir))
        result.addAll(createTestsForInvokeCatchAll(errorsDir))

        result.sort() // make sure the happy path is the first test
    }

    private static List<BetsyProcess> createTestsForInvokeSync(Path errorsDir) {
        BetsyProcess baseProcess = BasicActivityProcesses.INVOKE_SYNC

        List<BetsyProcess> result = new LinkedList<>();
        for (Map.Entry<String, String> entry : inputToErrorCode) {

            BetsyProcess process = cloneErrorBetsyProcess(baseProcess, entry, errorsDir)

            int number = Integer.parseInt(entry.getKey())
            process.testCases = [new TestCase().checkDeployment().sendSync(number, number)]

            result.add(process)
        }

        result
    }

    private
    static BetsyProcess cloneErrorBetsyProcess(BetsyProcess baseProcess, Map.Entry<String, String> entry, Path errorsDir) {
        BetsyProcess process = (BetsyProcess) baseProcess.clone()

        // copy file
        String filename = "ERR${entry.getKey()}_${entry.getValue()}"
        Path newPath = errorsDir.resolve("${filename}.bpel")
        Files.copy(process.bpel, newPath, StandardCopyOption.REPLACE_EXISTING)

        // update filename
        GPathResult root = new XmlSlurper(false, false).parse(process.bpelFilePath.toFile())
        root.@name = filename
        root.@targetNamespace = filename
        writeXmlFile(newPath, root)

        process.bpel = newPath
        process
    }

    private static List<BetsyProcess> createTestsForInvokeCatchAll(Path errorsDir) {
        BetsyProcess baseProcess = BasicActivityProcesses.INVOKE_CATCHALL

        List<BetsyProcess> result = new LinkedList<>();
        for (Map.Entry<String, String> entry : inputToErrorCode) {

            BetsyProcess process = cloneErrorBetsyProcess(baseProcess, entry, errorsDir)

            int number = Integer.parseInt(entry.getKey())
            process.testCases = [new TestCase().checkDeployment().sendSync(number, 0)]

            result.add(process)
        }

        result
    }

    private static void writeXmlFile(Path path, GPathResult root) {
        path.toFile().withPrintWriter { out ->
            out.print(XmlUtil.serialize(root))
        }
    }
}
