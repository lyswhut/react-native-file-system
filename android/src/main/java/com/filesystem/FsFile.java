package com.filesystem;

import static com.filesystem.Utils.isContentUri;
import static com.filesystem.Utils.isTreeUri;
import static com.filesystem.Utils.parsePathToFile;

import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FsFile {
  final ReactApplicationContext context;
  private File file = null;
  private DocumentFile dFile = null;
  FsFile(ReactApplicationContext context, String path) {
    this.context = context;
    if (isContentUri(path)) {
      try {
        Uri uri = Uri.parse(path);
        DocumentFile df = isTreeUri(uri)
          ? DocumentFile.fromTreeUri(context, uri)
          : DocumentFile.fromSingleUri(context, uri);
        if (df != null) {
          this.dFile = df;
          return;
        }
      } catch (Exception ignored) {}
    }
    this.file = parsePathToFile(path);
  }
  FsFile(ReactApplicationContext context, File file) {
    this.context = context;
    this.file = file;
  }
  FsFile(ReactApplicationContext context, DocumentFile file) {
    this.context = context;
    this.dFile = file;
  }

  public boolean isDocFile() {
    return this.file == null;
  }

  public boolean isDirectory() {
    return isDocFile()
      ? this.dFile.isDirectory()
      : this.file.isDirectory();
  }

  public FsFile getParentFile() {
    return isDocFile()
      ? new FsFile(context, this.dFile.getParentFile())
      : new FsFile(context, this.file.getParentFile());
  }

  public boolean exists() {
    return isDocFile()
      ? this.dFile.exists()
      : this.file.exists();
  }

  public ArrayList<FsFile> ls() throws IOException {
    ArrayList<FsFile> list = new ArrayList<>();
    if (isDocFile()) {
      if (!this.dFile.canRead()) throw new IOException("permission denied");
      DocumentFile[] files = this.dFile.listFiles();
      for (DocumentFile f: files) list.add(new FsFile(this.context, f));
    } else {
      if (!this.file.canRead()) throw new IOException("permission denied");
      File[] files = this.file.listFiles();
      if (files != null) for (File f: files) list.add(new FsFile(this.context, f));
    }

    return list;
  }

  public void mkdir() {
    if (isDocFile()) {
      String name = this.dFile.getName();
      this.dFile.getParentFile().createDirectory(name);
    } else {
      this.file.mkdirs();
    }
  }

  public FsFile mkdir(String name) {
    if (isDocFile()) {
      return new FsFile(this.context, this.dFile.createDirectory(name));
    } else {
      File f = new File(this.file.getAbsolutePath() + "/" + name);
      f.mkdirs();
      return new FsFile(this.context, f);
    }
  }

  public boolean unlink() {
    return isDocFile()
      ? this.dFile.delete()
      : this.file.delete();
  }

  public boolean rename(String name) {
    return isDocFile()
      ? this.dFile.renameTo(name)
      : this.file.renameTo(new File(this.file.getParent() + "/" + name));
  }

  public void mv(String toPath) throws IOException {
    Uri uri = Utils.createDocumentFileUri(context, toPath);

    if (this.isDocFile()) {
      if (uri == null) {
        File desFile = new FsFile(this.context, toPath).file;
        try (InputStream inputStream = Utils.createInputStream(this.context, this.dFile);
             OutputStream outputStream = Utils.createOutputStream(desFile)) {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        }
      } else {
        try (InputStream inputStream = Utils.createInputStream(this.context, this.dFile);
             OutputStream outputStream = Utils.createOutputStream(this.context, uri)) {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        }
      }
      this.dFile.delete();
    } else {
      if (uri == null) {
        File desFile = new FsFile(this.context, toPath).file;
        this.file.renameTo(desFile);
      } else {
        try (InputStream inputStream = Utils.createInputStream(this.file);
             OutputStream outputStream = Utils.createOutputStream(this.context, uri)) {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        }
      }
      this.file.delete();
    }
  }

  public WritableMap buildFileInfo() {
    return isDocFile()
      ? Utils.buildDocumentFile(this.dFile)
      : Utils.buildFile(this.file);
  }
}
