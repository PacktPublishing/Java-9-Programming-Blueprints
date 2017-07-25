package com.steeplesoft.dupefinder.lib.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author jason
 */
@Entity
public class FileInfo implements Serializable {
    @GeneratedValue
    @Id
    private int id;
    private String fileName;
    private String path;
    private long size;
    private String hash;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (id != fileInfo.id) return false;
        if (size != fileInfo.size) return false;
        if (!fileName.equals(fileInfo.fileName)) return false;
        if (!path.equals(fileInfo.path)) return false;
        return hash != null ? hash.equals(fileInfo.hash) : fileInfo.hash == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + fileName.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        return result;
    }
}
