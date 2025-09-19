package com.media.guardian.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TagDao _tagDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `MediaItem` (`id` INTEGER NOT NULL, `uri` TEXT NOT NULL, `displayName` TEXT NOT NULL, `size` INTEGER NOT NULL, `dateAdded` INTEGER NOT NULL, `mimeType` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Tag` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `MediaItemTagCrossRef` (`mediaItemId` INTEGER NOT NULL, `tagName` TEXT NOT NULL, PRIMARY KEY(`mediaItemId`, `tagName`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_MediaItemTagCrossRef_tagName` ON `MediaItemTagCrossRef` (`tagName`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '69682867d26181dc44fd6c23c4e33009')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `MediaItem`");
        db.execSQL("DROP TABLE IF EXISTS `Tag`");
        db.execSQL("DROP TABLE IF EXISTS `MediaItemTagCrossRef`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMediaItem = new HashMap<String, TableInfo.Column>(6);
        _columnsMediaItem.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItem.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItem.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItem.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItem.put("dateAdded", new TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItem.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMediaItem = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMediaItem = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMediaItem = new TableInfo("MediaItem", _columnsMediaItem, _foreignKeysMediaItem, _indicesMediaItem);
        final TableInfo _existingMediaItem = TableInfo.read(db, "MediaItem");
        if (!_infoMediaItem.equals(_existingMediaItem)) {
          return new RoomOpenHelper.ValidationResult(false, "MediaItem(com.media.guardian.data.MediaItem).\n"
                  + " Expected:\n" + _infoMediaItem + "\n"
                  + " Found:\n" + _existingMediaItem);
        }
        final HashMap<String, TableInfo.Column> _columnsTag = new HashMap<String, TableInfo.Column>(1);
        _columnsTag.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTag = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTag = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTag = new TableInfo("Tag", _columnsTag, _foreignKeysTag, _indicesTag);
        final TableInfo _existingTag = TableInfo.read(db, "Tag");
        if (!_infoTag.equals(_existingTag)) {
          return new RoomOpenHelper.ValidationResult(false, "Tag(com.media.guardian.database.Tag).\n"
                  + " Expected:\n" + _infoTag + "\n"
                  + " Found:\n" + _existingTag);
        }
        final HashMap<String, TableInfo.Column> _columnsMediaItemTagCrossRef = new HashMap<String, TableInfo.Column>(2);
        _columnsMediaItemTagCrossRef.put("mediaItemId", new TableInfo.Column("mediaItemId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaItemTagCrossRef.put("tagName", new TableInfo.Column("tagName", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMediaItemTagCrossRef = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMediaItemTagCrossRef = new HashSet<TableInfo.Index>(1);
        _indicesMediaItemTagCrossRef.add(new TableInfo.Index("index_MediaItemTagCrossRef_tagName", false, Arrays.asList("tagName"), Arrays.asList("ASC")));
        final TableInfo _infoMediaItemTagCrossRef = new TableInfo("MediaItemTagCrossRef", _columnsMediaItemTagCrossRef, _foreignKeysMediaItemTagCrossRef, _indicesMediaItemTagCrossRef);
        final TableInfo _existingMediaItemTagCrossRef = TableInfo.read(db, "MediaItemTagCrossRef");
        if (!_infoMediaItemTagCrossRef.equals(_existingMediaItemTagCrossRef)) {
          return new RoomOpenHelper.ValidationResult(false, "MediaItemTagCrossRef(com.media.guardian.database.MediaItemTagCrossRef).\n"
                  + " Expected:\n" + _infoMediaItemTagCrossRef + "\n"
                  + " Found:\n" + _existingMediaItemTagCrossRef);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "69682867d26181dc44fd6c23c4e33009", "827a579a0a39a715a4256bbbb83eecb2");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "MediaItem","Tag","MediaItemTagCrossRef");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `MediaItem`");
      _db.execSQL("DELETE FROM `Tag`");
      _db.execSQL("DELETE FROM `MediaItemTagCrossRef`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }
}
