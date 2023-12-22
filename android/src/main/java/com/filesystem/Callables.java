package com.filesystem;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Callables {
  static class Ls implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    public Ls(ReactApplicationContext context, String filePath) {
      this.context = context;
      this.filePath = filePath;
    }
    @Override
    public WritableArray call() throws IOException {
      return FileSystem.ls(this.context, this.filePath);
    }
  }
  static class ReadFile implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    private final String encoding;
    public ReadFile(ReactApplicationContext context, String filePath, String encoding) {
      this.context = context;
      this.filePath = filePath;
      this.encoding = encoding;
    }
    @Override
    public String call() throws IOException {
      return FileSystem.readFile(this.context, this.filePath, this.encoding);
    }
  }
  static class WriteFile implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    private final String data;
    private final String encoding;
    public WriteFile(ReactApplicationContext context, String filePath, String data, String encoding) {
      this.context = context;
      this.filePath = filePath;
      this.data = data;
      this.encoding = encoding;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.writeFile(this.context, this.filePath, this.data, this.encoding);
      return null;
    }
  }
  static class AppendFile implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    private final String data;
    private final String encoding;
    public AppendFile(ReactApplicationContext context, String filePath, String data, String encoding) {
      this.context = context;
      this.filePath = filePath;
      this.data = data;
      this.encoding = encoding;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.appendFile(this.context, this.filePath, this.data, this.encoding);
      return null;
    }
  }
  static class Mkdir implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    public Mkdir(ReactApplicationContext context, String filePath) {
      this.context = context;
      this.filePath = filePath;
    }
    @Override
    public WritableMap call() {
      return FileSystem.mkdir(this.context, this.filePath);
    }
  }
  static class Stat implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    public Stat(ReactApplicationContext context, String filePath) {
      this.context = context;
      this.filePath = filePath;
    }
    @Override
    public WritableMap call() {
      return FileSystem.stat(this.context, this.filePath);
    }
  }
  static class Exists implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    public Exists(ReactApplicationContext context, String filePath) {
      this.context = context;
      this.filePath = filePath;
    }
    @Override
    public Object call() {
      return FileSystem.exists(this.context, this.filePath);
    }
  }
  static class Unlink implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    public Unlink(ReactApplicationContext context, String filePath) {
      this.context = context;
      this.filePath = filePath;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.unlink(this.context, this.filePath);
      return null;
    }
  }
  static class Cp implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String fromPath;
    private final String toPath;
    public Cp(ReactApplicationContext context, String fromPath, String toPath) {
      this.context = context;
      this.fromPath = fromPath;
      this.toPath = toPath;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.cp(this.context, this.fromPath, this.toPath);
      return null;
    }
  }
  static class Mv implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String fromPath;
    private final String toPath;
    public Mv(ReactApplicationContext context, String fromPath, String toPath) {
      this.context = context;
      this.fromPath = fromPath;
      this.toPath = toPath;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.mv(this.context, this.fromPath, this.toPath);
      return null;
    }
  }
  static class Rename implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    private final String name;
    public Rename(ReactApplicationContext context, String filePath, String name) {
      this.context = context;
      this.filePath = filePath;
      this.name = name;
    }
    @Override
    public Object call() {
      return FileSystem.rename(this.context, this.filePath, this.name);
    }
  }
  static class GzipFile implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String fromPath;
    private final String toPath;
    public GzipFile(ReactApplicationContext context, String fromPath, String toPath) {
      this.context = context;
      this.fromPath = fromPath;
      this.toPath = toPath;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.gzipFile(this.context, this.fromPath, this.toPath);
      return null;
    }
  }
  static class UnGzipFile implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String fromPath;
    private final String toPath;
    public UnGzipFile(ReactApplicationContext context, String fromPath, String toPath) {
      this.context = context;
      this.fromPath = fromPath;
      this.toPath = toPath;
    }
    @Override
    public Object call() throws IOException {
      FileSystem.unGzipFile(this.context, this.fromPath, this.toPath);
      return null;
    }
  }
  static class GzipString implements Callable<Object> {
    private final String data;
    private final String encoding;
    public GzipString(String data, String encoding) {
      this.data = data;
      this.encoding = encoding;
    }
    @Override
    public Object call() throws IOException {
      return FileSystem.gzipString(this.data, this.encoding);
    }
  }
  static class UnGzipString implements Callable<Object> {
    private final String data;
    private final String encoding;
    public UnGzipString(String data, String encoding) {
      this.data = data;
      this.encoding = encoding;
    }
    @Override
    public Object call() throws IOException {
      return FileSystem.unGzipString(this.data, this.encoding);
    }
  }
  static class Hash implements Callable<Object> {
    private final ReactApplicationContext context;
    private final String filePath;
    private final String algorithm;
    public Hash(ReactApplicationContext context, String filePath, String algorithm) {
      this.context = context;
      this.filePath = filePath;
      this.algorithm = algorithm;
    }
    @Override
    public Object call() throws Exception {
      return FileSystem.hash(this.context, this.filePath, this.algorithm);
    }
  }

}
