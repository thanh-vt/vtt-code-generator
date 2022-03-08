package vn.thanhvt;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.archive.Archive.EntryFilter;

/**
 * @author pysga
 * @created 3/8/2022 - 9:48 PM
 * @project db-data-to-dto-generator
 * @since 1.0
 **/
public abstract class ArchiveLoader extends Loader {

    private static final String START_CLASS_ATTRIBUTE = "Start-Class";
    protected static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = "Spring-Boot-Classpath-Index";
    private final Archive archive;
    private final CustomClassPathIndexFile classPathIndex;

    public ArchiveLoader() {
        try {
            this.archive = this.createArchive();
            this.classPathIndex = this.getClassPathIndex(this.archive);
        } catch (Exception var2) {
            throw new IllegalStateException(var2);
        }
    }

    protected ArchiveLoader(Archive archive) {
        try {
            this.archive = archive;
            this.classPathIndex = this.getClassPathIndex(this.archive);
        } catch (Exception var3) {
            throw new IllegalStateException(var3);
        }
    }

    protected CustomClassPathIndexFile getClassPathIndex(Archive archive) throws IOException {
        return null;
    }

    protected String getMainClass() throws Exception {
        Manifest manifest = this.archive.getManifest();
        String mainClass = null;
        if (manifest != null) {
            mainClass = manifest.getMainAttributes().getValue("Start-Class");
        }

        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        } else {
            return mainClass;
        }
    }

    protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(this.guessClassPathSize());

        while(archives.hasNext()) {
            urls.add(archives.next().getUrl());
        }

        if (this.classPathIndex != null) {
            urls.addAll(this.classPathIndex.getUrls());
        }

        return this.createClassLoader((URL[])urls.toArray(new URL[0]));
    }

    private int guessClassPathSize() {
        return this.classPathIndex != null ? this.classPathIndex.size() + 10 : 50;
    }

    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        EntryFilter searchFilter = this::isSearchCandidate;
        Iterator<Archive> archives = this.archive.getNestedArchives(searchFilter, (entry) -> {
            return this.isNestedArchive(entry) && !this.isEntryIndexed(entry);
        });
        if (this.isPostProcessingClassPathArchives()) {
            archives = this.applyClassPathArchivePostProcessing(archives);
        }

        return archives;
    }

    private boolean isEntryIndexed(Entry entry) {
        return this.classPathIndex != null && this.classPathIndex.containsEntry(entry.getName());
    }

    private Iterator<Archive> applyClassPathArchivePostProcessing(Iterator<Archive> archives) throws Exception {
        List<Archive> list = new ArrayList<>();

        while(archives.hasNext()) {
            list.add(archives.next());
        }

        this.postProcessClassPathArchives(list);
        return list.iterator();
    }

    protected boolean isSearchCandidate(Entry entry) {
        return true;
    }

    protected abstract boolean isNestedArchive(Entry entry);

    protected boolean isPostProcessingClassPathArchives() {
        return true;
    }

    protected void postProcessClassPathArchives(List<Archive> archives) throws Exception {
    }

    protected boolean isExploded() {
        return this.archive.isExploded();
    }

    protected final Archive getArchive() {
        return this.archive;
    }

}
