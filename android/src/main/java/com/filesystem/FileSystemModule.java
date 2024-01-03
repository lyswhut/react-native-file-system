package com.filesystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

@ReactModule(name = FileSystemModule.NAME)
public class FileSystemModule extends ReactContextBaseJavaModule {
  public static final String NAME = "FileSystemModule";
  private final ReactApplicationContext reactContext;
  ActivityEventListener activityEventListener;
  private final int REQUEST_CODE = 27867;

  private int listenerCount = 0;

  FileSystemEvent fileSystemEvent;

  FileSystemModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    fileSystemEvent = new FileSystemEvent(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }



  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("CacheDir", reactContext.getCacheDir().getAbsolutePath());
    constants.put("DatabaseDir", reactContext.getDatabasePath("FileAccessProbe").getParent());
    constants.put("DocumentDir", reactContext.getFilesDir().getAbsolutePath());
    constants.put("MainBundleDir", reactContext.getApplicationInfo().dataDir);
    constants.put("SDCardDir", Environment.getExternalStorageDirectory().getAbsolutePath());
    return constants;
  }

  @ReactMethod
  public void addListener(String eventName) {
    if (listenerCount == 0) {
      // Set up any upstream listeners or background tasks as necessary
    }

    listenerCount += 1;
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    listenerCount -= count;
    if (listenerCount == 0) {
      // Remove upstream listeners, stop unnecessary background tasks
    }
  }

  @ReactMethod
  public void ls(String path, Promise promise) {
    AsyncTask.runTask(new Callables.Ls(reactContext, path), promise);
  }

  @ReactMethod
  public void readFile(String path, String encoding, Promise promise) {
    AsyncTask.runTask(new Callables.ReadFile(reactContext, path, encoding), promise);
  }

  @ReactMethod
  public void writeFile(String path, String data, String encoding, Promise promise) {
    AsyncTask.runTask(new Callables.WriteFile(reactContext, path, data, encoding), promise);
  }

  @ReactMethod
  public void appendFile(String path, String data, String encoding, Promise promise) {
    AsyncTask.runTask(new Callables.AppendFile(reactContext, path, data, encoding), promise);
  }

  @ReactMethod
  public void mkdir(String path, Promise promise) {
    AsyncTask.runTask(new Callables.Mkdir(reactContext, path), promise);
  }

  @ReactMethod
  public void stat(String path, Promise promise) {
    AsyncTask.runTask(new Callables.Stat(reactContext, path), promise);
  }

  @ReactMethod
  public void exists(String path, Promise promise) {
    AsyncTask.runTask(new Callables.Exists(reactContext, path), promise);
  }

  @ReactMethod
  public void unlink(String path, Promise promise) {
    AsyncTask.runTask(new Callables.Unlink(reactContext, path), promise);
  }

  @ReactMethod
  public void cp(String fromPath, String toPath, Promise promise) {
    AsyncTask.runTask(new Callables.Cp(reactContext, fromPath, toPath), promise);
  }

  @ReactMethod
  public void mv(String fromPath, String toPath, Promise promise) {
    AsyncTask.runTask(new Callables.Mv(reactContext, fromPath, toPath), promise);
  }

  @ReactMethod
  public void rename(String fromPath, String name, Promise promise) {
    AsyncTask.runTask(new Callables.Rename(reactContext, fromPath, name), promise);
  }

  @ReactMethod
  public void gzipFile(String fromPath, String toPath, Promise promise) {
    AsyncTask.runTask(new Callables.GzipFile(reactContext, fromPath, toPath), promise);
  }

  @ReactMethod
  public void unGzipFile(String fromPath, String toPath, Promise promise) {
    AsyncTask.runTask(new Callables.UnGzipFile(reactContext, fromPath, toPath), promise);
  }

  @ReactMethod
  public void gzipString(String data, String encoding, Promise promise) {
    AsyncTask.runTask(new Callables.GzipString(data, encoding), promise);
  }

  @ReactMethod
  public void unGzipString(String data, String encoding, Promise promise) {
    AsyncTask.runTask(new Callables.UnGzipString(data, encoding), promise);
  }

  @ReactMethod
  public void hash(String path, String algorithm, Promise promise) {
    AsyncTask.runTask(new Callables.Hash(reactContext, path, algorithm), promise);
  }

  @ReactMethod
  public void getExternalStoragePaths(boolean is_removable, Promise promise) {
    WritableArray arr = Arguments.createArray();
    ArrayList<String> paths = Utils.getExternalStoragePaths(reactContext, is_removable);
    for (String p: paths) arr.pushString(p);
    promise.resolve(arr);
  }

  @ReactMethod
  public void getAllExternalStoragePaths(Promise promise) {
    WritableArray arr = Arguments.createArray();
    ArrayList<String> paths = Utils.getExternalStoragePaths(reactContext);
    for (String p: paths) arr.pushString(p);
    promise.resolve(arr);
  }

  @ReactMethod
  public void getPersistedUriPermissions(Promise promise) {
    // https://github.com/ammarahm-ed/react-native-scoped-storage/blob/a33b2078379e250eb18428fcb69ba8e477adfece/android/src/main/java/com/ammarahmed/scopedstorage/RNScopedStorageModule.java#L378
    List<UriPermission> uriList = reactContext.getContentResolver().getPersistedUriPermissions();
    WritableArray array = Arguments.createArray();
    if (uriList.size() != 0) {
      for (UriPermission uriPermission : uriList) {
        array.pushString(uriPermission.getUri().toString());
      }
    }
    promise.resolve(array);
  }

  @ReactMethod
  public void releasePersistableUriPermission(String uri) {
    // https://github.com/ammarahm-ed/react-native-scoped-storage/blob/a33b2078379e250eb18428fcb69ba8e477adfece/android/src/main/java/com/ammarahmed/scopedstorage/RNScopedStorageModule.java#L393
    Uri uriToRevoke = Uri.parse(uri);
    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
      | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    reactContext.getContentResolver().releasePersistableUriPermission(uriToRevoke, takeFlags);
  }

  @ReactMethod
  public void openDocumentTree(final boolean persist, final Promise promise) {
    // https://github.com/ammarahm-ed/react-native-scoped-storage/blob/a33b2078379e250eb18428fcb69ba8e477adfece/android/src/main/java/com/ammarahmed/scopedstorage/RNScopedStorageModule.java#L196
    try {
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);

      if (activityEventListener != null) reactContext.removeActivityEventListener(activityEventListener);
      activityEventListener = new ActivityEventListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
          if (requestCode != REQUEST_CODE) promise.reject("-1", "open document failed, code: " + requestCode);
          else if (resultCode == Activity.RESULT_CANCELED) promise.resolve(null);
          else if (resultCode != Activity.RESULT_OK) promise.reject("-1", "open document result failed: " + resultCode);
          else {
            if (data == null) promise.reject("-1", "data is null.");
            else {
              Uri uri = data.getData();
              if (uri == null) promise.reject("-1", "uri is null.");
              else {
                if (persist) {
                  final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                  reactContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                }
                DocumentFile dir = DocumentFile.fromTreeUri(reactContext, uri);
                if (dir == null) promise.reject("-1", "dir uri is null.");
                else promise.resolve(Utils.buildDocumentFile(dir));
              }
            }
          }
          reactContext.removeActivityEventListener(activityEventListener);
          activityEventListener = null;
        }

        @Override
        public void onNewIntent(Intent intent) {

        }
      };

      reactContext.addActivityEventListener(activityEventListener);
      Objects.requireNonNull(reactContext.getCurrentActivity()).startActivityForResult(intent, REQUEST_CODE);

    } catch (Exception e) {
      promise.reject("ERROR", e.getMessage());
    }
  }

  @ReactMethod
  public void openDocument(ReadableMap options, final Promise promise) {
    Bundle bundle = Arguments.toBundle(options);
    if (bundle == null) {
      promise.reject("-1", "options is null");
      return;
    }
    ReadableArray mimeTypesArr = options.getArray("mimeTypes");
    String[] mimeTypes;
    if (mimeTypesArr == null) {
      ReadableArray extTypesArr = options.getArray("extTypes");
      if (extTypesArr == null || extTypesArr.size() < 1) mimeTypes = new String[]{"*/*"};
      else {
        ArrayList<Object> extTypes = extTypesArr.toArrayList();
        ArrayList<String> mt = new ArrayList<>();
        for (Object e: extTypes) {
          String mime = Utils.getMimeTypeFromExt((String) e);
          if (mime != null) mt.add(mime);
        }
        mimeTypes = mt.size() > 0 ? mt.toArray(new String[0]) : new String[]{"*/*"};
      }
    } else mimeTypes = mimeTypesArr.toArrayList().toArray(new String[0]);

    boolean isMultiSelect = bundle.getBoolean("multi", false);
    String toPath = bundle.getString("toPath", null);
    String encoding = bundle.getString("encoding", "utf8");
    Log.d("FileSystem", "openDocument mimeTypes: " + Arrays.toString(mimeTypes));
    // https://github.com/ammarahm-ed/react-native-scoped-storage/blob/a33b2078379e250eb18428fcb69ba8e477adfece/android/src/main/java/com/ammarahmed/scopedstorage/RNScopedStorageModule.java#L312
    try {
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      if (mimeTypes.length == 1) {
        intent.setType(mimeTypes[0]);
      } else {
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
      }
      if (isMultiSelect) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);


      if (activityEventListener != null) reactContext.removeActivityEventListener(activityEventListener);
      activityEventListener = new ActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
          // Log.d("FileSystem", "openDocument requestCode: " + requestCode + " resultCode: " + resultCode);
          if (requestCode != REQUEST_CODE) promise.reject("-1", "open document failed, code: " + requestCode);
          else if (resultCode == Activity.RESULT_CANCELED) promise.resolve(null);
          else if (resultCode != Activity.RESULT_OK) promise.reject("-1", "open document result failed: " + resultCode);
          else {
            if (data == null) promise.reject("-1", "data is null.");
            else {
              Uri uri = data.getData();
              if (uri == null) promise.reject("-1", "uri is null.");
              else {
                DocumentFile fileUri = DocumentFile.fromSingleUri(reactContext, uri);
                if (fileUri == null) promise.reject("-1", "file uri is null.");
                else {
                  WritableMap params = Utils.buildDocumentFile(fileUri);
                  Callable<Object> callable;
                  if (toPath == null) callable = new Callables.ReadFile(reactContext, fileUri.getUri().toString(), encoding);
                  else {
                    String path = toPath + "/" + fileUri.getName();
                    Log.d("FileSystem", "openDocument toPath: " + path);
                    callable = new Callables.Cp(reactContext, fileUri.getUri().toString(), path);
                    params.putString("data", path);
                  }
                  AsyncTask.TaskRunner taskRunner = new AsyncTask.TaskRunner();
                  try {
                    taskRunner.executeAsync(callable, (Object result) -> {
                      taskRunner.shutdown();
                      if (result instanceof Exception) {
                        promise.reject("-1", ((Exception) result).getMessage());
                      } else {
                        if (toPath == null) params.putString("data", (String) result);
                        promise.resolve(params);
                      }
                    });
                  } catch (Exception err) {
                    promise.reject("-1", err.getMessage());
                  }
                }
              }
            }
          }
          reactContext.removeActivityEventListener(activityEventListener);
          activityEventListener = null;
        }

        @Override
        public void onNewIntent(Intent intent) {

        }
      };
      reactContext.addActivityEventListener(activityEventListener);
      Objects.requireNonNull(reactContext.getCurrentActivity()).startActivityForResult(intent, REQUEST_CODE);

    } catch (Exception e) {
      promise.reject("ERROR", e.getMessage());
    }
  }
}
