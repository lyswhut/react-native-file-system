package com.filesystem;

import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileSystem {
  public static WritableArray ls(ReactApplicationContext reactContext, String path) throws IOException {
    FsFile dir = new FsFile(reactContext, path);
    ArrayList<FsFile> files = dir.ls();
    WritableArray array = Arguments.createArray();
    for (FsFile file : files) array.pushMap(file.buildFileInfo());
    return array;
  }

  public static String readFile(ReactApplicationContext reactContext, String path, String encoding) throws IOException {
    try (InputStream inputStream = Utils.createInputStream(reactContext, path);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
      return encoding.equals("base64")
        ? Utils.encodeBase64(byteArrayOutputStream.toByteArray())
        : new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }
  }

  public static void writeFile(ReactApplicationContext reactContext, String path, String data, String encoding) throws IOException {
    Uri uri = Utils.createDocumentFileUri(reactContext, path);
    try (OutputStream outputStream = uri == null
        ? Utils.createOutputStream(Utils.parsePathToFile(path))
        : Utils.createOutputStream(reactContext, uri)) {
      byte[] dataByte = encoding.equals("base64") ? Utils.decodeBase64(data) : data.getBytes();
      outputStream.write(dataByte);
    }
  }

  public static void appendFile(ReactApplicationContext reactContext, String path, String data, String encoding) throws IOException {
    Uri uri = Utils.createDocumentFileUri(reactContext, path);
    try (OutputStream outputStream = uri == null
        ? Utils.createOutputStream(Utils.parsePathToFile(path), true)
        : Utils.createOutputStream(reactContext, uri, true)) {
      byte[] dataByte = encoding.equals("base64") ? Utils.decodeBase64(data) : data.getBytes();
      outputStream.write(dataByte);
    }
  }

  public static WritableMap mkdir(ReactApplicationContext reactContext, String path) {
    int index = path.lastIndexOf("/");
    String dir = path.substring(0, index);
    String name = path.substring(index + 1);
    FsFile file = new FsFile(reactContext, dir);
    return file.mkdir(name).buildFileInfo();
  }

  public static WritableMap stat(ReactApplicationContext reactContext, String path) {
    FsFile file = new FsFile(reactContext, path);
    return file.buildFileInfo();
  }

  public static boolean exists(ReactApplicationContext reactContext, String path) {
    FsFile file = new FsFile(reactContext, path);
    return file.exists();
  }

  private static void deleteRecursive(FsFile fileOrDirectory) throws IOException {
    if (fileOrDirectory.isDirectory()) {
      for (FsFile child : Objects.requireNonNull(fileOrDirectory.ls())) {
        deleteRecursive(child);
      }
    }
    fileOrDirectory.unlink();
  }
  public static void unlink(ReactApplicationContext reactContext, String path) throws IOException {
    deleteRecursive(new FsFile(reactContext, path));
  }

  public static void cp(ReactApplicationContext reactContext, String fromPath, String toPath) throws IOException {
    try (InputStream inputStream = Utils.createInputStream(reactContext, fromPath);
         OutputStream outputStream = Utils.createOutputStream(reactContext, toPath)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    }
  }

  public static void mv(ReactApplicationContext reactContext, String fromPath, String toPath) throws IOException {
    FsFile file = new FsFile(reactContext, fromPath);
    file.mv(toPath);
  }

  public static boolean rename(ReactApplicationContext reactContext, String fromPath, String name) {
    FsFile file = new FsFile(reactContext, fromPath);
    return file.rename(name);
  }

  public static void gzipFile(ReactApplicationContext reactContext, String fromPath, String toPath) throws IOException {
    try (InputStream inputStream = Utils.createInputStream(reactContext, fromPath);
         OutputStream outputStream = Utils.createOutputStream(reactContext, toPath);
          GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        gzipOutputStream.write(buffer, 0, length);
      }
    }
  }

  public static void unGzipFile(ReactApplicationContext reactContext, String fromPath, String toPath) throws IOException {
    try (InputStream inputStream = Utils.createInputStream(reactContext, fromPath);
         OutputStream outputStream = Utils.createOutputStream(reactContext, toPath);
         GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
      byte[] buffer = new byte[4096];
      int length;
      while ((length = gzipInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    }
  }

  public static String gzipString(String rawData, String encoding) throws IOException {
    byte[] data = encoding.equals("base64") ? Utils.decodeBase64(rawData) : rawData.getBytes(StandardCharsets.UTF_8);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
         GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
      gzipOutputStream.write(data);
      return Utils.encodeBase64(outputStream.toByteArray());
    }
  }

  public static String unGzipString(String data, String encoding) throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Utils.decodeBase64(data));
         GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
      return encoding.equals("base64")
        ? Utils.encodeBase64(byteArrayOutputStream.toByteArray())
        : new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }
  }

  public static String hash(ReactApplicationContext reactContext, String filepath, String algorithm) throws Exception {
    // https://github.com/itinance/react-native-fs/blob/64aa755cc1d37f59fa205bf2d52dd71a7d691504/android/src/main/java/com/rnfs/RNFSManager.java#L318
    Map<String, String> algorithms = new HashMap<>();
    algorithms.put("md5", "MD5");
    algorithms.put("sha1", "SHA-1");
    algorithms.put("sha224", "SHA-224");
    algorithms.put("sha256", "SHA-256");
    algorithms.put("sha384", "SHA-384");
    algorithms.put("sha512", "SHA-512");
    if (!algorithms.containsKey(algorithm)) throw new Exception("Invalid hash algorithm");

    MessageDigest md = MessageDigest.getInstance(algorithms.get(algorithm));

    try (InputStream inputStream = Utils.createInputStream(reactContext, filepath)) {
      byte[] buffer = new byte[1024 * 10]; // 10 KB Buffer

      int read;
      while ((read = inputStream.read(buffer)) != -1) {
        md.update(buffer, 0, read);
      }

      StringBuilder hexString = new StringBuilder();
      for (byte digestByte : md.digest())
        hexString.append(String.format("%02x", digestByte));

      return hexString.toString();
    }
  }
}
