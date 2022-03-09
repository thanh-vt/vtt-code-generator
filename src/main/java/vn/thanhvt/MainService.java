package vn.thanhvt;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
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
//
//        Class<?> clazz = Class.forName("vn.etc.customs.ektt.dto.KtBangkeNhantuKhobacDto", true, this.currentClassLoader);
//
//        ImmutableSet<ClassInfo> classes = ClassPath.from(this.currentClassLoader).getAllClasses();
//        Iterator<ClassPath.ClassInfo> iterator = classes.stream().iterator();
//        while (iterator.hasNext()) {
//            ClassInfo classInfo = iterator.next();
//            System.out.println(classInfo.getName());
//        }
//        System.out.println(clazz.getSimpleName());
    }

    public void clearConfig() {
        this.currentClassLoader = null;
    }

}
