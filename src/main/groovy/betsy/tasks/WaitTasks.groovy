package betsy.tasks

import ant.tasks.AntUtil
import org.apache.log4j.Logger

class WaitTasks {

    private static final Logger log = Logger.getLogger(WaitTasks.class)

    public static void sleep(int milliseconds) {
        log.info("Sleep for ${milliseconds} ms NOW")
        AntUtil.builder().sleep(milliseconds: milliseconds)
    }

}
