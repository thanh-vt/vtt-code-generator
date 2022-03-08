package vn.thanhvt;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import javax.naming.OperationNotSupportedException;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;

public class MainService {

    private ClassLoader currentClassLoader;

    public ClassLoader getCurrentClassLoader() {
        return currentClassLoader != null ? currentClassLoader : this.getClass().getClassLoader();
    }

    public void applyConfig(File f, boolean isSpring) throws Exception {
        if (isSpring) {
            if (f.getName().endsWith(".jar")) {
                this.currentClassLoader = new JarLoader(new JarFileArchive(f)).load();
            } else if (f.getName().endsWith(".war")) {
                this.currentClassLoader = new WarLoader(new ExplodedArchive(f)).load();
            } else {
                throw new OperationNotSupportedException("This file extension is not supported");
            }
        } else {
            this.currentClassLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        }
    }

    public void clearConfig() {
        this.currentClassLoader = null;
    }

}
