package vn.thanhvt.custom;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.loader.archive.Archive.Entry;

/**
 * @author pysga
 * @created 3/18/2022 - 12:15 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleJarLoader extends Loader {

    private final File jarFile;

    public SimpleJarLoader(File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public ClassLoader load(Set<String> loadedSourceClassNames, Set<String> loadedPackageNames) throws Exception {
        try (JarFile jarFile = new JarFile(this.jarFile)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                    loadedSourceClassNames.add(className);
                } else {
                    String packageName = jarEntry.getName()
                        .replace("/", ".")
                        .replaceAll(".$", "");
                    loadedPackageNames.add(packageName);
                }
            }
        }
        return new URLClassLoader(new URL[]{this.jarFile.toURI().toURL()});
    }

    protected EntryClassifier checkIsSourceClass(Entry entry) {
//        if (entry.getName().endsWith(".class")) {
//            return entry.getName()
//                .replace("/", ".")
//                .replace(".class", "");
//        }
        return null;
    }

    @Override
    protected String getMainClass() {
        throw new UnsupportedOperationException("Unsupported method!");
    }

}
