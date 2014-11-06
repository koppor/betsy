package betsy.bpel.engines.openesb

import betsy.bpel.model.BPELProcess
import betsy.bpel.engines.LocalEngine
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks

import java.nio.file.Path

class OpenEsbEngine extends LocalEngine {

    static final String CHECK_URL = "http://localhost:18181"

    @Override
    String getName() {
        "openesb"
    }

    @Override
    String getEndpointUrl(BPELProcess process) {
        "${CHECK_URL}/${process.name}TestInterface"
    }

    @Override
    void storeLogs(BPELProcess process) {
        Path logsFolder = process.targetPath.resolve("logs")
        FileTasks.mkdirs(logsFolder)
        ant.copy(todir: logsFolder) {
            ant.fileset(dir: glassfishHome.resolve("domains/domain1/logs/"))
        }
    }

    OpenEsbCLI getCli() {
        new OpenEsbCLI(getGlassfishHome())
    }

    protected Path getGlassfishHome() {
        serverPath.resolve("glassfish")
    }

    @Override
    void startup() {
        cli.startDomain()
        WaitTasks.waitForAvailabilityOfUrl(15_000,500, CHECK_URL)
    }

    @Override
    void shutdown() {
        cli.stopDomain()
    }

    @Override
    void install() {
        new OpenEsbInstaller().install()
    }

    @Override
    void deploy(BPELProcess process) {
        new OpenEsbDeployer(cli: cli,
                processName: process.name,
                packageFilePath: process.targetPackageCompositeFilePath,
                tmpFolder: process.targetPath).deploy()
    }

    @Override
    public void buildArchives(BPELProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        buildDeploymentDescriptor(process)
        ant.replace(file: process.targetProcessPath.resolve("TestInterface.wsdl"),
                token: "TestInterfaceService", value: "${process.name}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new OpenEsbCompositePackager(process: process).build()
    }

    void buildDeploymentDescriptor(BPELProcess process) {
        Path metaDir = process.targetProcessPath.resolve("META-INF")
        Path catalogFile = metaDir.resolve("catalog.xml")
        FileTasks.mkdirs(metaDir)
        FileTasks.createFile(catalogFile, """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog" prefer="system">
</catalog>""");
        FileTasks.createFile(metaDir.resolve("MANIFEST.MF"), "Manifest-Version: 1.0");
        ant.xslt(in: process.targetProcessFilePath, out: metaDir.resolve("jbi.xml"),
                style: xsltPath.resolve("create_jbi_from_bpel.xsl"))
    }


    @Override
    boolean isRunning() {
        return false;
    }

}
