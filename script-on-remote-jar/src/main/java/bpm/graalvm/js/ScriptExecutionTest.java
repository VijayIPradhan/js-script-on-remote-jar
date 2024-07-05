package bpm.graalvm.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

public class ScriptExecutionTest {
    private static final String FILE_URL = "https://graalvmscript.s3.amazonaws.com/javascript-1.0-SNAPSHOT.jar";
    private static final Logger logger = Logger.getLogger(ScriptExecutionTest.class.getName());
    private String script = "";

    public static String getFileFromResources(String fileName, ScriptLoader scriptLoader) {
        InputStream stream = scriptLoader.getResourceAsStream(fileName);
        String text = null;
        assert stream != null;
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    private static String generateScript(String file, ScriptLoader scriptLoader) {
        return getFileFromResources(file, scriptLoader) +
                "const feature = 'exampleFeature';\n" +
                "const owner= 'owner';\n" +
                "const values = [1, 2, 3];    \n" +
                "const addCommand = new AddCommand(owner, feature, ...values);\n" +
                "console.log(addCommand.commandName);\n" +
                "const addPaneToFormCommand = new AddPaneToFormCommand();\n" +
                "console.log(addPaneToFormCommand.commandName);\n" +
                "const customPropertyDescriptorIns = new CustomPropertyDescriptorIns();\n" +
                "console.log(customPropertyDescriptorIns.hasDefaultValue);\n" +
                "const bomTypesRegistry = new BomTypesRegistry();\n" +
                "bomTypesRegistry.addClass(addCommand, { id: 'id123', attribute: 'values' });\n" +
                "console.log(JSON.stringify(bomTypesRegistry.allTypeNames, null, 2));";
    }

    public void run() {
        try {
            URL scriptUrl = new URL(FILE_URL);
            try (ScriptLoader scriptLoader = new ScriptLoader(new URL[]{scriptUrl}, getClass().getClassLoader())) {

                URL resourceURL = scriptLoader.getResource("index.mjs");

                if (resourceURL != null) {
                    if (resourceURL.getProtocol().equals("jar")) {

                        JarURLConnection jarConnection = (JarURLConnection) resourceURL.openConnection();
                        String entryName = jarConnection.getEntryName();
                        this.script = generateScript(entryName, scriptLoader);
                    } else {
                        File resourceFile = Paths.get(resourceURL.toURI()).toFile();
                        logger.warning("Resource file path: " + resourceFile.getAbsolutePath());
                    }
                } else {
                    logger.severe("Resource not found");
                }
            }
            Engine engine= Engine.newBuilder()
                    .option("engine.WarnInterpreterOnly","false")
                    .build();

            Context cx = Context.newBuilder("js")
                    .engine(engine)
                    .allowAllAccess(true)
                    .build();
            cx.eval(Source.newBuilder("js", script, "")
                    .mimeType("application/javascript+module")
                    .build());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
