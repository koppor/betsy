package betsy.bpel.engines.petalsesb

import betsy.bpel.engines.LocalEngine
import betsy.bpel.model.BPELProcess
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.URLTasks
import org.apache.log4j.Logger

import java.nio.file.Files
import java.nio.file.Path

class PetalsEsbEngine extends LocalEngine {

    private static final Logger log = Logger.getLogger(PetalsEsbEngine)

    public static final String CHECK_URL = "http://localhost:8084"

    @Override
    String getName() {
        "petalsesb"
    }

    @Override
    String getEndpointUrl(BPELProcess process) {
        "$CHECK_URL/petals/services/${process.name}TestInterfaceService"
    }

    Path getPetalsFolder() {
        serverPath.resolve(getPetalsFolderName())
    }

    protected String getPetalsFolderName() {
        "petals-esb-4.0"
    }

    Path getPetalsLogsFolder() {
        petalsFolder.resolve("logs")
    }

    Path getPetalsLogFile() {
        petalsLogsFolder.resolve("petals.log")
    }

    Path getPetalsBinFolder() {
        petalsFolder.resolve("bin")
    }

    @Override
    void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        FileTasks.copyFileIntoFolder(petalsLogFile, process.targetLogsPath)
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(petalsBinFolder, "petals-esb.bat"))

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            and {
                resourcecontains(resource: petalsLogFile,
                        substring: "[Petals.Container.Components.petals-bc-soap] : Component started")
                resourcecontains(resource: petalsLogFile,
                        substring: "[Petals.Container.Components.petals-se-bpel] : Component started")
            }
        }

        try {
            ant.fail(message: "SOAP BC not installed correctly") {
                condition() {
                    resourcecontains(resource: petalsLogFile,
                            substring: "[Petals.AutoLoaderService] : Error during the auto- installation of a component")
                }
            }
        } catch (Exception ignore) {
            log.warn "SOAP BC Installation failed - shutdown, reinstall and start petalsesb again"
            shutdown()
            install()
            startup()
        }
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(path, "taskkill").values("/FI", "WINDOWTITLE eq OW2*"))
    }

    @Override
    void install() {
        new PetalsEsbInstaller().install()
    }

    @Override
    void deploy(BPELProcess process) {
        new PetalsEsbDeployer(processName: process.name,
                packageFilePath: process.targetPackageCompositeFilePath,
                logFilePath: petalsLogFile,
                deploymentDirPath: getInstallationDir()
        ).deploy()
    }

    Path getInstallationDir() {
        petalsFolder.resolve("install")
    }

    @Override
    void buildArchives(BPELProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        Path metaDir = process.targetProcessPath.resolve("META-INF")
        FileTasks.mkdirs(metaDir)
        ant.xslt(in: process.targetProcessFilePath, out: metaDir.resolve("jbi.xml"),
                style: xsltPath.resolve("create_jbi_from_bpel.xsl"))

        ant.replace(file: process.targetProcessPath.resolve("TestInterface.wsdl"),
                token: "TestInterfaceService",
                value: "${process.name}TestInterfaceService")


        Path testPartnerWsdl = process.targetProcessPath.resolve("TestPartner.wsdl")
        if (Files.exists(testPartnerWsdl)) {
            ant.replace(file: testPartnerWsdl, token: "TestService", value: "${process.name}TestService")
        }

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new PetalsEsbCompositePackager(process: process).build()
    }

    @Override
    boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL)
    }

}
