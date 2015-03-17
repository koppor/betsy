package betsy.bpel.cli;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;

import java.util.List;

public interface BPELCliParameter {

    List<AbstractBPELEngine> getEngines();
    List<BPELProcess> getProcesses();

    boolean openResultsInBrowser();
    boolean checkDeployment();
    boolean hasCustomPartnerAddress();
    boolean transformToCoreBpel();
    String getCoreBPELTransformations();
    String getCustomPartnerAddress();
    boolean useExternalPartnerService();
    boolean buildArtifactsOnly();

    boolean showHelp();

}
