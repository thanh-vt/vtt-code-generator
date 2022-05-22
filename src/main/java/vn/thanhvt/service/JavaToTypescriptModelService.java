package vn.thanhvt.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.OperationNotSupportedException;
import org.springframework.boot.loader.archive.JarFileArchive;
import vn.thanhvt.custom.JarLoader;
import vn.thanhvt.custom.SimpleJarLoader;
import vn.thanhvt.custom.WarLoader;

/**
 * @author pysga
 * @created 5/21/2022 - 9:18 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class JavaToTypescriptModelService {

    private ClassLoader currentClassLoader;

    private Set<String> allClassNames;

    public ClassLoader getCurrentClassLoader() {
        return currentClassLoader != null ? currentClassLoader : this.getClass().getClassLoader();
    }

    public Set<String> applyConfig(File f, boolean isSpring) throws Exception {
        Set<String> loadedSourceClassNames = new HashSet<>();
        Set<String> loadedPackageNames = new HashSet<>();
        if (isSpring) {
            if (f.getName().endsWith(".jar")) {
                this.currentClassLoader = new JarLoader(new JarFileArchive(f)).load(loadedSourceClassNames, loadedPackageNames);
            } else if (f.getName().endsWith(".war")) {
                this.currentClassLoader = new WarLoader(new JarFileArchive(f)).load(loadedSourceClassNames, loadedPackageNames);
            } else {
                throw new OperationNotSupportedException("This file extension is not supported");
            }
        } else {
            this.currentClassLoader = new SimpleJarLoader(f).load(loadedSourceClassNames, loadedPackageNames);
        }
        this.allClassNames = loadedSourceClassNames;
        return loadedPackageNames;
    }

    public void clearConfig() {
        this.currentClassLoader = null;
        this.allClassNames = null;
    }

    public Set<Class<?>> generate(String packageName) throws IOException, ClassNotFoundException {
        return this.allClassNames.stream()
            .filter(line -> line.startsWith(packageName))
            .map(line -> {
                try {
                    return Class.forName(line, false, this.currentClassLoader);
                } catch (ClassNotFoundException e) {
                    // handle the exception
                    System.err.println(e);
                    return null;
                }
            })
            .collect(Collectors.toSet());
    }

}
