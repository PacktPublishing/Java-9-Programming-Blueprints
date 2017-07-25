package com.steeplesoft.dupefinder.lib.util;

import com.steeplesoft.dupefinder.lib.model.FileInfo;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 *
 * @author jason
 */
public class FindFileTask implements Runnable, FileVisitor<Path> {

    private final Path startDir;
    private final EntityManager em;
    private final List<PathMatcher> matchers;

    public FindFileTask(Path startDir, List<PathMatcher> matchers,
            EntityManagerFactory factory) {
        this.startDir = startDir;
        this.em = factory.createEntityManager();
        this.matchers = matchers;
    }

    @Override
    public void run() {
        final EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Files.walkFileTree(startDir, this);
            transaction.commit();
        } catch (IOException ex) {
            transaction.rollback();
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        return Files.isReadable(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (Files.isReadable(file) && isMatch(file)) {
            addFile(file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private boolean isMatch(final Path file) {
        return matchers.isEmpty() ? true : matchers.stream().anyMatch((m) -> m.matches(file));
    }

    private void addFile(Path file) throws IOException {
        FileInfo info = new FileInfo();
        info.setFileName(file.getFileName().toString());
        info.setPath(file.toRealPath().toString());
        info.setSize(file.toFile().length());
        em.persist(info);
    }

}
