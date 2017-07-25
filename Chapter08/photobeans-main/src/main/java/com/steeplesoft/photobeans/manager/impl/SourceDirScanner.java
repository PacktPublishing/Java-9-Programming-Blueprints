package com.steeplesoft.photobeans.manager.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.openide.util.Exceptions;

/**
 *
 * @author jason
 */
public class SourceDirScanner implements Callable<List<Photo>>, FileVisitor<Path> {

    private static final String IMAGE_PATTERN
            = "([^\\s]+(\\.(?i)(jpg|png|gif|tiff|bmp))$)";
    private static final Pattern PATTERN = Pattern.compile(IMAGE_PATTERN);
    private final String sourceDir;
    private final List<Photo> photos = new ArrayList<>();
    private InputOutput io;

    public SourceDirScanner(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    @Override
    public List<Photo> call() throws Exception {
        try {
            io = IOProvider.getDefault().getIO("Scanning for photos", true);
            Files.walkFileTree(Paths.get(sourceDir), this);
            io.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return photos;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
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
        return PATTERN.matcher(file.toString()).matches();
    }

    private void addFile(Path file) {
        try {
            io.getOut().println(file.toString());
            Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory != null) {
                addPhoto(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL),
                        file);
            }
        } catch (ImageProcessingException | IOException | SQLException ex) {
            // Swallow
        }
    }

    private void addPhoto(final Date date, Path file) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date != null ? date : new Date(0));
        photos.add(new Photo(sourceDir, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, file.toString()));
    }

}
