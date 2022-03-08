package vn.thanhvt;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.MainMethodRunner;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

/**
 * @author pysga
 * @created 3/8/2022 - 9:55 PM
 * @project db-data-to-dto-generator
 * @since 1.0
 **/
public abstract class Loader {

    private static final String JAR_MODE_LAUNCHER = "org.springframework.boot.loader.jarmode.JarModeLauncher";

    public Loader() {
    }

    protected ClassLoader load() throws Exception {
        if (!this.isExploded()) {
            JarFile.registerUrlProtocolHandler();
        }

        //        String jarMode = System.getProperty("jarmode");
//        String launchClass = jarMode != null && !jarMode.isEmpty() ? "org.springframework.boot.loader.jarmode.JarModeLauncher" : this.getMainClass();
//        this.launch(args, launchClass, classLoader);
        return this.createClassLoader(this.getClassPathArchivesIterator());
    }

    /** @deprecated */
    @Deprecated
    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
        return this.createClassLoader(archives.iterator());
    }

    protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(50);

        while(archives.hasNext()) {
            urls.add(archives.next().getUrl());
        }

        return this.createClassLoader(urls.toArray(new URL[0]));
    }

    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new LaunchedURLClassLoader(this.isExploded(), this.getArchive(), urls, this.getClass().getClassLoader());
    }

    protected void launch(String[] args, String launchClass, ClassLoader classLoader) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        this.createMainMethodRunner(launchClass, args, classLoader).run();
    }

    protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
        return new MainMethodRunner(mainClass, args);
    }

    protected abstract String getMainClass() throws Exception;

    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        return this.getClassPathArchives().iterator();
    }

    protected List<Archive> getClassPathArchives() throws Exception {
        throw new IllegalStateException("Unexpected call to getClassPathArchives()");
    }

    protected final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource != null ? codeSource.getLocation().toURI() : null;
        String path = location != null ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        } else {
            File root = new File(path);
            if (!root.exists()) {
                throw new IllegalStateException("Unable to determine code source archive from " + root);
            } else {
                return (Archive)(root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
            }
        }
    }

    protected boolean isExploded() {
        return false;
    }

    protected Archive getArchive() {
        return null;
    }
}
