package vn.thanhvt.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pysga
 * @created 3/8/2022 - 9:51 PM
 * @project db-data-to-dto-generator
 * @since 1.0
 **/
public class CustomClassPathIndexFile {

    private final File root;
    private final List<String> lines;

    private CustomClassPathIndexFile(File root, List<String> lines) {
        this.root = root;
        this.lines = lines.stream().map(this::extractName).collect(Collectors.toList());
    }

    private String extractName(String line) {
        if (line.startsWith("- \"") && line.endsWith("\"")) {
            return line.substring(3, line.length() - 1);
        } else {
            throw new IllegalStateException("Malformed classpath index line [" + line + "]");
        }
    }

    int size() {
        return this.lines.size();
    }

    boolean containsEntry(String name) {
        return name != null && !name.isEmpty() && this.lines.contains(name);
    }

    List<URL> getUrls() {
        return Collections.unmodifiableList(this.lines.stream().map(this::asUrl).collect(Collectors.toList()));
    }

    private URL asUrl(String line) {
        try {
            return (new File(this.root, line)).toURI().toURL();
        } catch (MalformedURLException var3) {
            throw new IllegalStateException(var3);
        }
    }

    static CustomClassPathIndexFile loadIfPossible(URL root, String location) throws IOException {
        return loadIfPossible(asFile(root), location);
    }

    private static CustomClassPathIndexFile loadIfPossible(File root, String location) throws IOException {
        return loadIfPossible(root, new File(root, location));
    }

    private static CustomClassPathIndexFile loadIfPossible(File root, File indexFile) throws IOException {
        if (indexFile.exists() && indexFile.isFile()) {
            InputStream inputStream = new FileInputStream(indexFile);
            Throwable var3 = null;

            CustomClassPathIndexFile var4;
            try {
                var4 = new CustomClassPathIndexFile(root, loadLines(inputStream));
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (inputStream != null) {
                    if (var3 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }

            return var4;
        } else {
            return null;
        }
    }

    private static List<String> loadLines(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }

        return Collections.unmodifiableList(lines);
    }

    private static File asFile(URL url) {
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("URL does not reference a file");
        } else {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException var2) {
                return new File(url.getPath());
            }
        }
    }
}
