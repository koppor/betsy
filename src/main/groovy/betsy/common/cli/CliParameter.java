package betsy.common.cli;

public interface CliParameter {

    String getTestFolderName();

    boolean openResultsInBrowser();

    boolean buildArtifactsOnly();

    boolean showHelp();

    boolean useInstalledEngine();

    boolean keepEngineRunning();

}
