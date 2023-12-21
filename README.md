# react-native-file-system

React native android async file system

based on:

- <https://github.com/ammarahm-ed/react-native-scoped-storage>
- <https://github.com/alpha0010/react-native-file-access>

## Installation

```sh
npm install github:lyswhut/react-native-file-system
```

<!-- ## Usage

```js
import { multiply } from 'react-native-file-system';

// ...

const result = await multiply(3, 7);
``` -->

## Doc

```ts

export declare const Dirs: {
    /**
     * Temporary files. System/user may delete these if device storage is low.
     */
    CacheDir: string;
    /**
     * System recommended location for SQLite files.
     *
     * Android only.
     */
    DatabaseDir?: string;
    /**
     * Persistent data. Generally user created content.
     */
    DocumentDir: string;
    /**
     * App's default root directory.
     */
    MainBundleDir: string;
    /**
     * Root path to removable media. Prefer `cpExternal()` when possible, as
     * Android discourages this access method.
     *
     * Android only.
     */
    SDCardDir: string;
};
export declare const getExternalStoragePaths: (is_removable?: boolean) => Promise<string[]>;
export declare const AndroidScoped: {
    getPersistedUriPermissions(): Promise<string[]>;
    releasePersistableUriPermission(): any;
    openDocumentTree(isPersist: boolean): Promise<FileType>;
    openDocument(options: OpenDocumentOptions): Promise<FileType>;
};
export declare const FileSystem: {
  /**
   * Copy a file.
   */
  cp(source: string, target: string): Promise<void>;
  /**
   * Check if a path exists.
   */
  exists(path: string): Promise<boolean>;
  /**
   * List files in a directory.
   */
  ls(path: string): Promise<FileType[]>;
  /**
   * Make a new directory.
   *
   * Returns path of the created directory.
   */
  mkdir(path: string): Promise<FileType>;
  /**
   * move a file.
   */
  mv(source: string, target: string): Promise<boolean>;
  /**
   * rename a file.
   */
  rename(source: string, name: string): Promise<boolean>;
  /**
   * Read the content of a file.
   */
  readFile(path: string, encoding?: Encoding): Promise<string>;
  /**
   * Read file metadata.
   */
  stat(path: string): Promise<FileType>;
  /**
   * Delete a file.
   */
  unlink(path: string): Promise<boolean>;
  /**
   * Write content to a file.
   *
   * Default encoding of `data` is assumed utf8.
   */
  writeFile(path: string, data: string, encoding?: Encoding): Promise<void>;
  /**
   * Append content to a file.
   *
   * Default encoding of `data` is assumed utf8.
   */
  appendFile(path: string, data: string, encoding?: Encoding): Promise<void>;
  /**
   * Gzip to a file.
   */
  gzipFile(source: string, target: string): Promise<void>;
  /**
   * UnGzip to a file.
   */
  unGzipFile(source: string, target: string): Promise<void>;
  /**
   * Gzip string.
   *
   * Default encoding of `data` is assumed utf8.
   */
  gzipString(data: string, encoding?: Encoding): Promise<string>;
  /**
   * UnGzip string.
   *
   * Default encoding of `data` is assumed utf8.
   */
  unGzipString(data: string, encoding?: Encoding): Promise<string>;
};


```

## License

MIT
