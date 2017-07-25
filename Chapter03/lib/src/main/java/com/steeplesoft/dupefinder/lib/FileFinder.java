package com.steeplesoft.dupefinder.lib;

import com.steeplesoft.dupefinder.lib.model.FileInfo;
import com.steeplesoft.dupefinder.lib.util.FindFileTask;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author jason
 */
public class FileFinder {
    private final Set<Path> sourcePaths = new HashSet<>();
    private final Set<String> patterns = new HashSet<>();
    private final ExecutorService es = Executors.newFixedThreadPool(5);
    private final Map<String, List<FileInfo>> duplicates = new HashMap<>();
    private final EntityManagerFactory factory;
    private List<PathMatcher> matchers;

    private static final String DUPLICATE_SQL = "SELECT f FROM FileInfo f, "
            + "(SELECT s.%FIELD% FROM FileInfo s GROUP BY s.%FIELD% HAVING (COUNT(s.%FIELD%) > 1)) g "
            + " where f.%FIELD% = g.%FIELD% and f.%FIELD% is not null ORDER BY f.fileName, f.path";

    public FileFinder() {
        Map<String, String> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url",
                "jdbc:sqlite:"
                + System.getProperty("user.home")
                + File.separator
                + ".dupfinder.db");

        factory = Persistence.createEntityManagerFactory("dupefinder", props);
        purgeExistingFileInfo();
    }

    public void addPattern(String pattern) {
        patterns.add(pattern);
    }

    public void addPath(String path) {
        sourcePaths.add(Paths.get(path));
    }

    public void find() {
        matchers = patterns.stream()
                .map(s -> !s.startsWith("**") ? "**/" + s : s)
                .map(p -> FileSystems.getDefault().getPathMatcher("glob:" + p))
                .collect(Collectors.toList());
        sourcePaths.stream()
                .map(p -> new FindFileTask(p, matchers, factory))
                .forEach(fft -> es.execute(fft));
        try {
            es.shutdown();
            es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        postProcessFiles();
        factory.close();
    }

    public Map<String, List<FileInfo>> getDuplicates() {
        return duplicates;
    }

    private void purgeExistingFileInfo() {
        final EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("delete from fileinfo").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private void postProcessFiles() {
        EntityManager em = factory.createEntityManager();

        List<FileInfo> files = getDuplicates(em, "fileName");
        files.addAll(getDuplicates(em, "size"));

        em.getTransaction().begin();
        files.forEach(f -> calculateHash(f));
        em.getTransaction().commit();

        getDuplicates(em, "hash").forEach(f -> coalesceDuplicates(f));
        em.close();
    }

    private void calculateHash(FileInfo file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");
            messageDigest.update(Files.readAllBytes(Paths.get(file.getPath())));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(messageDigest.digest());
            String hash = IntStream.generate(inputStream::read)
                    .limit(inputStream.available())
                    .mapToObj(i -> Integer.toHexString(i))
                    .map(s -> ("00" + s).substring(s.length()))
                    .collect(Collectors.joining());
            file.setHash(hash);
        } catch (NoSuchAlgorithmException | IOException ex) {
            // This algorithm is guaranteed to be there by the JDK, so we'll
            // wrap the checked exception in an unchecked exception so that we
            // don't have to expose it to consuming classes. This *should* never
            // actually run, but it's probably best to be cautious here.
            throw new RuntimeException(ex);
        }
    }

    private void coalesceDuplicates(FileInfo f) {
        String hash = f.getHash();
        List<FileInfo> dupes = duplicates.get(hash);
        if (dupes == null) {
            dupes = new ArrayList<>();
            duplicates.put(hash, dupes);
        }
        dupes.add(f);
    }

    private List<FileInfo> getDuplicates(EntityManager em, String fieldName) {
        List<FileInfo> files = em.createQuery(
                DUPLICATE_SQL.replace("%FIELD%", fieldName),
                FileInfo.class).getResultList();
        return files;
    }

}
