package com.media.guardian.database;

import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.media.guardian.data.MediaItem;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TagDao_Impl implements TagDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Tag> __insertionAdapterOfTag;

  private final EntityInsertionAdapter<MediaItemTagCrossRef> __insertionAdapterOfMediaItemTagCrossRef;

  private final EntityInsertionAdapter<MediaItem> __insertionAdapterOfMediaItem;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<MediaItemTagCrossRef> __deletionAdapterOfMediaItemTagCrossRef;

  public TagDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTag = new EntityInsertionAdapter<Tag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `Tag` (`name`) VALUES (?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Tag entity) {
        statement.bindString(1, entity.getName());
      }
    };
    this.__insertionAdapterOfMediaItemTagCrossRef = new EntityInsertionAdapter<MediaItemTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `MediaItemTagCrossRef` (`mediaItemId`,`tagName`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItemTagCrossRef entity) {
        statement.bindLong(1, entity.getMediaItemId());
        statement.bindString(2, entity.getTagName());
      }
    };
    this.__insertionAdapterOfMediaItem = new EntityInsertionAdapter<MediaItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `MediaItem` (`id`,`uri`,`displayName`,`size`,`dateAdded`,`mimeType`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItem entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.uriToString(entity.getUri());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        statement.bindString(3, entity.getDisplayName());
        statement.bindLong(4, entity.getSize());
        statement.bindLong(5, entity.getDateAdded());
        statement.bindString(6, entity.getMimeType());
      }
    };
    this.__deletionAdapterOfMediaItemTagCrossRef = new EntityDeletionOrUpdateAdapter<MediaItemTagCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `MediaItemTagCrossRef` WHERE `mediaItemId` = ? AND `tagName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItemTagCrossRef entity) {
        statement.bindLong(1, entity.getMediaItemId());
        statement.bindString(2, entity.getTagName());
      }
    };
  }

  @Override
  public Object insertTag(final Tag tag, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTag.insert(tag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMediaItemTagCrossRef(final MediaItemTagCrossRef crossRef,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaItemTagCrossRef.insert(crossRef);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMediaItem(final MediaItem mediaItem,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaItem.insert(mediaItem);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMediaItemTagCrossRef(final MediaItemTagCrossRef crossRef,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMediaItemTagCrossRef.handle(crossRef);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Tag>> getAllTags() {
    final String _sql = "SELECT * FROM Tag";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"Tag"}, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
          try {
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final Tag _item;
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              _item = new Tag(_tmpName);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<MediaItemWithTags> getTagsForMediaItem(final long mediaItemId) {
    final String _sql = "SELECT * FROM MediaItem WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mediaItemId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"MediaItemTagCrossRef", "Tag",
        "MediaItem"}, new Callable<MediaItemWithTags>() {
      @Override
      @NonNull
      public MediaItemWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
            final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
            final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
            final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
            final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipTagAscomMediaGuardianDatabaseTag(_collectionTags);
            final MediaItemWithTags _result;
            if (_cursor.moveToFirst()) {
              final MediaItem _tmpMediaItem;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final Uri _tmpUri;
              final String _tmp;
              if (_cursor.isNull(_cursorIndexOfUri)) {
                _tmp = null;
              } else {
                _tmp = _cursor.getString(_cursorIndexOfUri);
              }
              final Uri _tmp_1 = __converters.fromString(_tmp);
              if (_tmp_1 == null) {
                throw new IllegalStateException("Expected NON-NULL 'android.net.Uri', but it was NULL.");
              } else {
                _tmpUri = _tmp_1;
              }
              final String _tmpDisplayName;
              _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
              final long _tmpSize;
              _tmpSize = _cursor.getLong(_cursorIndexOfSize);
              final long _tmpDateAdded;
              _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
              final String _tmpMimeType;
              _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
              _tmpMediaItem = new MediaItem(_tmpId,_tmpUri,_tmpDisplayName,_tmpSize,_tmpDateAdded,_tmpMimeType);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _result = new MediaItemWithTags(_tmpMediaItem,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MediaItemWithTags>> getMediaItemsWithTag(final String tagName) {
    final String _sql = "SELECT * FROM MediaItem WHERE id IN (SELECT mediaItemId FROM MediaItemTagCrossRef WHERE tagName = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tagName);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"MediaItemTagCrossRef", "Tag",
        "MediaItem"}, new Callable<List<MediaItemWithTags>>() {
      @Override
      @NonNull
      public List<MediaItemWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
            final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
            final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
            final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
            final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionTags.containsKey(_tmpKey)) {
                _collectionTags.put(_tmpKey, new ArrayList<Tag>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipTagAscomMediaGuardianDatabaseTag(_collectionTags);
            final List<MediaItemWithTags> _result = new ArrayList<MediaItemWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final MediaItemWithTags _item;
              final MediaItem _tmpMediaItem;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final Uri _tmpUri;
              final String _tmp;
              if (_cursor.isNull(_cursorIndexOfUri)) {
                _tmp = null;
              } else {
                _tmp = _cursor.getString(_cursorIndexOfUri);
              }
              final Uri _tmp_1 = __converters.fromString(_tmp);
              if (_tmp_1 == null) {
                throw new IllegalStateException("Expected NON-NULL 'android.net.Uri', but it was NULL.");
              } else {
                _tmpUri = _tmp_1;
              }
              final String _tmpDisplayName;
              _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
              final long _tmpSize;
              _tmpSize = _cursor.getLong(_cursorIndexOfSize);
              final long _tmpDateAdded;
              _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
              final String _tmpMimeType;
              _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
              _tmpMediaItem = new MediaItem(_tmpId,_tmpUri,_tmpDisplayName,_tmpSize,_tmpDateAdded,_tmpMimeType);
              final ArrayList<Tag> _tmpTagsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              _item = new MediaItemWithTags(_tmpMediaItem,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMediaItemIdsForTag(final String tagName,
      final Continuation<? super List<Long>> $completion) {
    final String _sql = "SELECT mediaItemId FROM MediaItemTagCrossRef WHERE tagName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tagName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            _item = _cursor.getLong(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipTagAscomMediaGuardianDatabaseTag(
      @NonNull final LongSparseArray<ArrayList<Tag>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipTagAscomMediaGuardianDatabaseTag(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `Tag`.`name` AS `name`,_junction.`mediaItemId` FROM `MediaItemTagCrossRef` AS _junction INNER JOIN `Tag` ON (_junction.`tagName` = `Tag`.`name`) WHERE _junction.`mediaItemId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.mediaItemId;
      final int _itemKeyIndex = 1;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfName = 0;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Tag> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Tag _item_1;
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          _item_1 = new Tag(_tmpName);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
