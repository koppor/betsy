package betsy.data.engines.activebpel

class ActiveBpelDeployer {

    AntBuilder ant = new AntBuilder()

    String packageFilePath
    String deploymentDirPath
    String logFilePath
    String processName

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        // define custom condition
        ant.typedef (name:"httpcontains", classname:"ant.tasks.HttpContains")

        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                and {
                    available file: "${deploymentDirPath}/work/ae_temp_${processName}_bpr/META-INF/catalog.xml"
                    resourcecontains(resource: logFilePath,
                            substring: "[${processName}.pdd]")
                    httpcontains(contains: "Running", url: "http://localhost:8080/BpelAdmin/")
                }
            }
        }
    }
}