package betsy.data.engines.activebpel

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.data.engines.tomcat.Tomcat

/*
* Currently using in-memory mode for the engine
 */
class ActiveBpelEngine extends LocalEngine {

    @Override
    String getName() {
        return "active-bpel"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/active-bpel/services/${process.name}TestInterfaceService"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath, tomcatName: "apache-tomcat-5.5.36")
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/bpr"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
        ant.copy(file: getAeDeploymentLog(), todir: "${process.targetPath}/logs")
    }

    private static String getAeDeploymentLog() {
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.endsWith(File.separator) ?: homeDir + File.separator;

        "$homeDir/AeBpelEngine/deployment-logs/aeDeployment.log"
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    void install() {
        new ActiveBpelInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        new ActiveBpelDeployer(deploymentDirPath: getDeploymentDir(),
                logFilePath: getAeDeploymentLog(),
                processName: process.name,
                packageFilePath: process.getTargetPackageFilePath("bpr")).deploy()
    }

    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // create deployment descriptor
        String metaDir = process.targetBpelPath + "/META-INF"
        ant.echo file: "$metaDir/MANIFEST.MF", message: "Manifest-Version: 1.0"
        ant.xslt(in: process.bpelFilePath, out: "$metaDir/${process.name}.pdd", style: "${getXsltPath()}/active-bpel_to_deploy_xml.xsl")
        ant.xslt(in: process.bpelFilePath, out: "$metaDir/catalog.xml", style: "${getXsltPath()}/active-bpel_to_catalog.xsl")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        // create bpr file
        ant.move(file: process.targetPackageFilePath, toFile: process.getTargetPackageFilePath("bpr"))
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

}
