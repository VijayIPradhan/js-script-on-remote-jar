package bpm.graalvm.js;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Logger;

public class GraalVmTestWithGVS {
    private static final Logger logger = Logger.getLogger(GraalVmTestWithGVS.class.getName());
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String JAVA_VENDOR = System.getProperty("java.vendor");

    public static void main(String[] args) {
        logger.info("Java " + JAVA_VENDOR + "-" + JAVA_VERSION);
        GraalVmTestWithGVS graalVmTestWithGVS = new GraalVmTestWithGVS();
        graalVmTestWithGVS.run();
    }

    public void run() {
        try {
            try (ScriptLoader scriptLoader = new ScriptLoader(new URL[]{}, ScriptExecutionTest.class.getClassLoader())) {

                Class<?> scriptClass = scriptLoader.loadClass("bpm.graalvm.js.ScriptExecutionTest");
                ScriptExecutionTest scriptInstance = (ScriptExecutionTest) scriptClass.getDeclaredConstructor().newInstance();

                Method method = scriptClass.getMethod("run");
                method.invoke(scriptInstance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
