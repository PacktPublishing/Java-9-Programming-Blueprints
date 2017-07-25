package com.steeplesoft.photobeans.manager.impl;

import com.steeplesoft.photobeans.manager.PhotoManager;
import com.steeplesoft.photobeans.manager.reload.ReloadCookie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jason
 */
@ServiceProvider(service = PhotoManager.class)
public class PhotoManagerImpl implements PhotoManager {

    private final List<String> sourceDirs = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Connection connection;
    private final Lookup lookup;
    private final InstanceContent instanceContent;

    private static final String JDBC_URL = "jdbc:sqlite:photobeans.db";

    public PhotoManagerImpl() throws ClassNotFoundException {
        Preferences prefs = NbPreferences.forModule(PhotoManager.class);
        setupDatabase();
        setSourceDirs(prefs.get("sourceDirs", ""));
        scanSourceDirs();

        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);

        prefs.addPreferenceChangeListener(evt -> {
            if (evt.getKey().equals("sourceDirs")) {
                setSourceDirs(evt.getNewValue());
                scanSourceDirs();
            }
        });
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public final void scanSourceDirs() {
        RequestProcessor.getDefault().execute(() -> {
            List<Future<List<Photo>>> futures = new ArrayList<>();
            sourceDirs.stream()
                    .map(d -> new SourceDirScanner(d))
                    .forEach(sds -> futures.add((Future<List<Photo>>) executorService.submit(sds)));

            futures.forEach(f -> {
                try {
                    final List<Photo> list = f.get();
                    processPhotos(list);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            instanceContent.add(new ReloadCookie());
        });
    }

    @Override
    public List<String> getYears() {
        List<String> years = new ArrayList<>();
        try (Statement yearStmt = connection.createStatement();
                ResultSet rs = yearStmt.executeQuery("SELECT DISTINCT year FROM images ORDER BY year")) {
            while (rs.next()) {
                years.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return years;
    }

    @Override
    public List<String> getMonths(int year) {
        List<String> months = new ArrayList<>();
        PreparedStatement monthStmt = null;
        ResultSet rs = null;

        try {
            monthStmt = connection.prepareStatement("SELECT DISTINCT month FROM images WHERE year = ? ORDER BY month");
            monthStmt.setInt(1, year);
            rs = monthStmt.executeQuery();
            while (rs.next()) {
                months.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
            close(rs);
            close(monthStmt);
        }

        return months;
    }

    @Override
    public List<String> getPhotos(int year, int month) {
        List<String> photos = new ArrayList<>();
        PreparedStatement photoStmt = null;
        ResultSet rs = null;

        try {
            photoStmt = connection.prepareStatement("SELECT DISTINCT image FROM images WHERE year = ? and month = ? ORDER BY image");
            photoStmt.setInt(1, year);
            photoStmt.setInt(2, month);
            rs = photoStmt.executeQuery();
            while (rs.next()) {
                photos.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
            close(rs);
            close(photoStmt);
        }

        return photos;
    }

    private void processPhotos(List<Photo> photos) {
        photos.stream()
                .filter(p -> !isImageRecorded(p))
                .forEach(p -> insertImage(p));
    }

    private boolean isImageRecorded(Photo photo) {
        boolean there = false;
        try (PreparedStatement imageExistStatement = connection.prepareStatement("SELECT 1 FROM images WHERE image = ?")) {
            imageExistStatement.setString(1, photo.getImage());
            final ResultSet rs = imageExistStatement.executeQuery();
            there = rs.next();
            close(rs);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return there;
    }

    private void insertImage(Photo photo) {
        try (PreparedStatement insertStatement
                = connection.prepareStatement("INSERT INTO images (imageSource, year, month, image) VALUES (?, ?, ?, ?);")) {
            insertStatement.setString(1, photo.getSourceDir());
            insertStatement.setInt(2, photo.getYear());
            insertStatement.setInt(3, photo.getMonth());
            insertStatement.setString(4, photo.getImage());
            insertStatement.executeUpdate();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void setupDatabase() {
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            if (!doesTableExist()) {
                createTable();
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private boolean doesTableExist() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("select 1 from images");
            rs.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE images (imageSource VARCHAR2(4096), "
                    + " year int, month int, image VARCHAR2(4096));");
            stmt.execute("CREATE UNIQUE INDEX uniq_img ON images(image);");
        } catch (SQLException e) {
            Exceptions.printStackTrace(e);
        }
    }

    private void setSourceDirs(String dirs) {
        sourceDirs.addAll(Arrays.asList(dirs.split(";")));
    }

    private void close(AutoCloseable ac) {
        try {
            if (ac != null) {
                ac.close();
            }
        } catch (Exception ex1) {
            Exceptions.printStackTrace(ex1);
        }
    }
}
