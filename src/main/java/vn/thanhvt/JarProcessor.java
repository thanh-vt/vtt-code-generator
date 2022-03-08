package vn.thanhvt;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarProcessor {

    private ClassLoader currentClassLoader;

    private final Map<String, Class<?>> availableClasses;

    public Map<String, Class<?>> getAvailableClasses() {
        return availableClasses;
    }

    public JarProcessor(File f) throws Exception {
        this.availableClasses = new HashMap<>();

        List<String> classNames = getClassNamesFromJar(f);

        this.currentClassLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        for (String className : classNames) {
            Class<?> cc = this.currentClassLoader.loadClass(className);
            availableClasses.put(cc.getName(), cc);
        }
    }

    // Returns an arraylist of class names in a JarInputStream
    private List<String> getClassNamesFromJar(JarInputStream jarFile) throws Exception {
        List<String> classNames = new ArrayList<>();
        try {
            //JarInputStream jarFile = new JarInputStream(jarFileStream);
            JarEntry jar;

            //Iterate through the contents of the jar file
            while (true) {
                jar = jarFile.getNextJarEntry();
                if (jar == null) {
                    break;
                }
                //Pick file that has the extension of .class
                if ((jar.getName().endsWith(".class"))) {
                    String className = jar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    classNames.add(myClass);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while getting class names from jar", e);
        }
        return classNames;
    }

    // Returns an arraylist of class names in a JarInputStream
    // Calls the above function by converting the jar path to a stream
    private List<String> getClassNamesFromJar(File f) throws Exception {
        return getClassNamesFromJar(new JarInputStream(new FileInputStream(f)));
    }

    @Override
    public void finalize() {
        this.currentClassLoader = null;
        this.availableClasses.clear();
    }

}
