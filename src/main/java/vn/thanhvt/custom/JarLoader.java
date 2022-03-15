package vn.thanhvt.custom;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.archive.Archive.EntryFilter;
import org.springframework.boot.loader.archive.ExplodedArchive;

/**
 * @author pysga
 * @created 3/8/2022 - 9:49 PM
 * @project db-data-to-dto-generator
 * @since 1.0
 **/
public class JarLoader extends ArchiveLoader {

    private static final String DEFAULT_CLASSPATH_INDEX_LOCATION = "BOOT-INF/classpath.idx";
    static final EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
        return entry.isDirectory() ? entry.getName().equals("BOOT-INF/classes/") : entry.getName().startsWith("BOOT-INF/lib/");
    };

    public JarLoader() {
    }

    public JarLoader(Archive archive) {
        super(archive);
    }

    protected CustomClassPathIndexFile getClassPathIndex(Archive archive) throws IOException {
        if (archive instanceof ExplodedArchive) {
            String location = this.getClassPathIndexFileLocation(archive);
            return CustomClassPathIndexFile.loadIfPossible(archive.getUrl(), location);
        } else {
            return super.getClassPathIndex(archive);
        }
    }

    private String getClassPathIndexFileLocation(Archive archive) throws IOException {
        Manifest manifest = archive.getManifest();
        Attributes attributes = manifest != null ? manifest.getMainAttributes() : null;
        String location = attributes != null ? attributes.getValue("Spring-Boot-Classpath-Index") : null;
        return location != null ? location : "BOOT-INF/classpath.idx";
    }

    protected boolean isPostProcessingClassPathArchives() {
        return false;
    }

    protected boolean isSearchCandidate(Entry entry) {
        return entry.getName().startsWith("BOOT-INF/");
    }

    protected boolean isNestedArchive(Entry entry) {
        return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
    }

}
