package com.filesystem;

import android.content.Context;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import androidx.documentfile.provider.DocumentFile;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Utils {
  public static boolean isContentUri(String path) {
    return path.startsWith("content://");
  }
  public static boolean isTreeUri(Uri uri) {
    return "tree".equals(uri.getPathSegments().get(0));
  }
  public static String encodeBase64(byte[] data) {
    return new String(Base64.encode(data, Base64.NO_WRAP), StandardCharsets.UTF_8);
  }
  public static byte[] decodeBase64(String data) {
    return Base64.decode(data, Base64.DEFAULT);
  }
  public static WritableMap buildDocumentFile(DocumentFile file) {
    WritableMap fileMap = Arguments.createMap();
    fileMap.putString("path", file.getUri().toString());
    fileMap.putString("name", file.getName());
    fileMap.putBoolean("isDirectory", file.isDirectory());
    boolean isFile = file.isFile();
    fileMap.putBoolean("isFile", isFile);
    if (isFile) {
      fileMap.putString("mimeType", file.getType());
      fileMap.putDouble("size", file.length());
    } else {
      fileMap.putString("mimeType", null);
      fileMap.putDouble("size", 0);
    }
    fileMap.putDouble("lastModified", file.lastModified());
    fileMap.putBoolean("canRead", file.canRead());
    return fileMap;
  }
  public static WritableMap buildFile(File file) {
    WritableMap fileMap = Arguments.createMap();
    fileMap.putString("path", file.getAbsolutePath());
    fileMap.putString("name", file.getName());
    fileMap.putBoolean("isDirectory", file.isDirectory());
    boolean isFile = file.isFile();
    fileMap.putBoolean("isFile", isFile);
    if (isFile) {
      fileMap.putString("mimeType", getMimeTypeFromFileName(file.getName()));
      fileMap.putDouble("size", file.length());
    } else {
      fileMap.putString("mimeType", null);
      fileMap.putDouble("size", 0);
    }
    fileMap.putDouble("lastModified", file.lastModified());
    fileMap.putBoolean("canRead", file.canRead());
    return fileMap;
  }
  public static File parsePathToFile(String path) {
    if (path.contains("://")) {
      try {
        Uri pathUri = Uri.parse(path);
        return new File(pathUri.getPath());
      } catch (Throwable e) {
        return new File(path);
      }
    }
    return new File(path);
  }
  public static InputStream createInputStream(ReactApplicationContext context, String path) throws FileNotFoundException {
    return path.startsWith("content://")
      ? context.getContentResolver().openInputStream(Uri.parse(path))
      : new FileInputStream(parsePathToFile(path));
  }
  public static InputStream createInputStream(File file) throws FileNotFoundException {
    return new FileInputStream(file);
  }
  public static InputStream createInputStream(ReactApplicationContext context, DocumentFile file) throws FileNotFoundException {
    return context.getContentResolver().openInputStream(file.getUri());
  }

  public static OutputStream createOutputStream(ReactApplicationContext context, Uri uri, boolean append) throws IOException {
    DocumentFile pFile = DocumentFile.fromSingleUri(context, uri).getParentFile();
    if (pFile != null && !pFile.exists()) new FsFile(context, pFile).mkdir();
    return context.getContentResolver().openOutputStream(uri, append ? "wa" : "w");
  }

  public static OutputStream createOutputStream(File file, boolean append) throws FileNotFoundException {
    File pFile = file.getParentFile();
    if (pFile != null && !pFile.exists()) pFile.mkdirs();
    return new FileOutputStream(file, append);
  }
  public static OutputStream createOutputStream(ReactApplicationContext context, String path, boolean append) throws IOException {
    return isContentUri(path)
      ? createOutputStream(context, createDocumentFileUri(context, path), append)
      : createOutputStream(parsePathToFile(path), append);
  }

  public static OutputStream createOutputStream(ReactApplicationContext context, String path) throws IOException {
    return createOutputStream(context, path, false);
  }
  public static OutputStream createOutputStream(ReactApplicationContext context, Uri uri) throws IOException {
    return createOutputStream(context, uri, false);
  }
  public static OutputStream createOutputStream(File file) throws FileNotFoundException {
    return createOutputStream(file, false);
  }
  //  public static OutputStream createOutputStream(ReactApplicationContext context, DocumentFile file, boolean append) throws IOException {
  //    return createOutputStream(context, file.getUri(), append);
  //  }
  //  public static OutputStream createOutputStream(ReactApplicationContext context, DocumentFile file) throws IOException {
  //    return createOutputStream(context, file.getUri(), false);
  //  }




//  public static DocumentFile createDocumentFile(ReactApplicationContext context, String path) {
//    if (isContentUri(path)) {
//      try {
//        Uri uri = Uri.parse(path);
//        DocumentFile df = isTreeUri(uri)
//          ? DocumentFile.fromTreeUri(context, uri)
//          : DocumentFile.fromSingleUri(context, uri);
//        if (df != null) return df;
//      } catch (Exception ignored) {}
//    }
//    return DocumentFile.fromFile(parsePathToFile(path));
//  }

  public static String getMimeTypeFromExt(String ext) {
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getMimeTypeFromExtension(ext);
  }
  public static String getMimeTypeFromFileName(String name) {
    return getMimeTypeFromExt(getFileExtension(name));
  }
  public static String getExtFromMimeType(String mimeType) {
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(getFileExtension(mimeType));
  }
  public static String getName(String fileName) {
    int dotIndex = fileName.lastIndexOf(".");
    if (dotIndex != -1) {
      return fileName.substring(0, dotIndex);
    } else {
      return fileName;
    }
  }
  public static String getFileExtension(String fileName) {
    int dotIndex = fileName.lastIndexOf(".");
    if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
      return fileName.substring(dotIndex + 1);
    } else {
      return "";
    }
  }
  public static DocumentFile createDocumentFileFromUri(ReactApplicationContext context, Uri uri) {
    return isTreeUri(uri)
      ? DocumentFile.fromTreeUri(context, uri)
      : DocumentFile.fromSingleUri(context, uri);
  }
  public static Uri createDocumentFileUri(ReactApplicationContext context, String path) throws IOException {
    Uri uri = null;
    if (Utils.isContentUri(path)) {
      int index = path.lastIndexOf("/");
      String distName = path.substring(0, index);
      String distPath = path.substring(index + 1);
      DocumentFile destinationDirectory = DocumentFile.fromTreeUri(context, Uri.parse(distPath));
      if (!destinationDirectory.exists()) throw new IOException("dest dir not exists.");
      uri = DocumentsContract.createDocument(context.getContentResolver(), destinationDirectory.getUri(), getMimeTypeFromFileName(distName), getName(distName));
    }
    return uri;
  }
  public static DocumentFile createDocumentFile(ReactApplicationContext context, String path) throws IOException {
    Uri uri = createDocumentFileUri(context, path);
    return uri == null ? null : createDocumentFileFromUri(context, uri);
  }


  // https://gist.github.com/PauloLuan/4bcecc086095bce28e22?permalink_comment_id=2591001#gistcomment-2591001
  public static ArrayList<String> getExternalStoragePaths(ReactApplicationContext mContext, boolean is_removable) {
    StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    Class<?> storageVolumeClazz;
    ArrayList<String> paths = new ArrayList<>();
    try {
      storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
      Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
      Method getPath = storageVolumeClazz.getMethod("getPath");
      Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
      Object result = getVolumeList.invoke(mStorageManager);
      final int length = Array.getLength(result);
      for (int i = 0; i < length; i++) {
        Object storageVolumeElement = Array.get(result, i);
        String path = (String) getPath.invoke(storageVolumeElement);
        boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
        if (is_removable == removable) {
          paths.add(path);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return paths;
  }
  public static ArrayList<String> getExternalStoragePaths(ReactApplicationContext mContext) {
    StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    Class<?> storageVolumeClazz;
    ArrayList<String> paths = new ArrayList<>();
    try {
      storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
      Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
      Method getPath = storageVolumeClazz.getMethod("getPath");
      Object result = getVolumeList.invoke(mStorageManager);
      final int length = Array.getLength(result);
      for (int i = 0; i < length; i++) {
        Object storageVolumeElement = Array.get(result, i);
        String path = (String) getPath.invoke(storageVolumeElement);
        paths.add(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return paths;
  }
}
