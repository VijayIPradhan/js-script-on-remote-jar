package bpm.graalvm.js;

import java.net.URL;
import java.net.URLClassLoader;

public class ScriptLoader extends URLClassLoader {
    public ScriptLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void add(URL url) {
        super.addURL(url);
    }
}
