package betsy.virtual.host.virtualbox

import ant.tasks.AntUtil
import betsy.config.Configuration;
import org.apache.log4j.Logger

import java.nio.file.Path
/**
 * The {@link VirtualMachineImporter} offers several methods to prepare and
 * finally execute the import of a VirtualBox appliance.<br>
 * <br>
 * The default import procedure involves:
 * <ul>
 * <li>Download the VM</li>
 * <li>Extract the archive</li>
 * <li>Import the Appliance</li>
 * <li>Cleanup extraction path</li>
 * </ul>
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineImporter {

    private static final Logger log = Logger.getLogger(VirtualMachineImporter.class);

    private final VBoxController vbc;
    private final String vmName;
    private final String engineName;
    private final Path downloadPath;

    public VirtualMachineImporter(final String vmName, final String engineName,
                                  final Path downloadPath,
                                  final VBoxController vbc) {
        this.vbc = Objects.requireNonNull(vbc);
        this.downloadPath = Objects.requireNonNull(downloadPath);

        this.vmName = Objects.requireNonNull(vmName);
        this.engineName = Objects.requireNonNull(engineName);
    }

    public void makeVMAvailable() {
        log.info("Downloading VM " + vmName + " to " + downloadPath);
        AntUtil.builder().get(dest: downloadPath, src: downloadUrl, skipexisting: true);

        log.info("Importing VM " + vmName + " into VirtualBox");
        vbc.importVirtualMachine(vmName, engineName, getDownloadArchiveFile());

        log.info("Import finished");
    }

    private String getDownloadUrl() {
        return Configuration.get("virtual.engines." + engineName + ".download");
    }

    private Path getDownloadArchiveFile() {
        String url = getDownloadUrl();
        // get only the filename + extension without the directory structure
        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        return downloadPath.resolve(fileName);
    }

}
