package com.fireflylabz.tiktech.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class ArticleDao_Impl implements ArticleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ArticleEntity> __insertionAdapterOfArticleEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBookmark;

  public ArticleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfArticleEntity = new EntityInsertionAdapter<ArticleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `articles` (`id`,`title`,`sourceName`,`sourceUrl`,`category`,`depthLevel`,`tldr`,`takeawaysJson`,`codeSnippet`,`fullAnalysis`,`githubRepoName`,`githubRepoUrl`,`githubStars`,`metricLabel`,`metricValue`,`entityTagsJson`,`publishedAt`,`isBookmarked`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ArticleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getSourceName());
        statement.bindString(4, entity.getSourceUrl());
        statement.bindString(5, entity.getCategory());
        statement.bindString(6, entity.getDepthLevel());
        statement.bindString(7, entity.getTldr());
        statement.bindString(8, entity.getTakeawaysJson());
        if (entity.getCodeSnippet() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCodeSnippet());
        }
        if (entity.getFullAnalysis() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFullAnalysis());
        }
        if (entity.getGithubRepoName() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getGithubRepoName());
        }
        if (entity.getGithubRepoUrl() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getGithubRepoUrl());
        }
        if (entity.getGithubStars() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getGithubStars());
        }
        if (entity.getMetricLabel() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getMetricLabel());
        }
        if (entity.getMetricValue() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getMetricValue());
        }
        if (entity.getEntityTagsJson() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getEntityTagsJson());
        }
        statement.bindString(17, entity.getPublishedAt());
        final int _tmp = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(18, _tmp);
      }
    };
    this.__preparedStmtOfUpdateBookmark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE articles SET isBookmarked = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertArticles(final List<ArticleEntity> articles,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfArticleEntity.insert(articles);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBookmark(final String id, final boolean isBookmarked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBookmark.acquire();
        int _argIndex = 1;
        final int _tmp = isBookmarked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateBookmark.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ArticleEntity>> getAllArticles() {
    final String _sql = "SELECT * FROM articles ORDER BY publishedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"articles"}, new Callable<List<ArticleEntity>>() {
      @Override
      @NonNull
      public List<ArticleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUrl");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDepthLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "depthLevel");
          final int _cursorIndexOfTldr = CursorUtil.getColumnIndexOrThrow(_cursor, "tldr");
          final int _cursorIndexOfTakeawaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "takeawaysJson");
          final int _cursorIndexOfCodeSnippet = CursorUtil.getColumnIndexOrThrow(_cursor, "codeSnippet");
          final int _cursorIndexOfFullAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "fullAnalysis");
          final int _cursorIndexOfGithubRepoName = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoName");
          final int _cursorIndexOfGithubRepoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoUrl");
          final int _cursorIndexOfGithubStars = CursorUtil.getColumnIndexOrThrow(_cursor, "githubStars");
          final int _cursorIndexOfMetricLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "metricLabel");
          final int _cursorIndexOfMetricValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metricValue");
          final int _cursorIndexOfEntityTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entityTagsJson");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final List<ArticleEntity> _result = new ArrayList<ArticleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ArticleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpSourceUrl;
            _tmpSourceUrl = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDepthLevel;
            _tmpDepthLevel = _cursor.getString(_cursorIndexOfDepthLevel);
            final String _tmpTldr;
            _tmpTldr = _cursor.getString(_cursorIndexOfTldr);
            final String _tmpTakeawaysJson;
            _tmpTakeawaysJson = _cursor.getString(_cursorIndexOfTakeawaysJson);
            final String _tmpCodeSnippet;
            if (_cursor.isNull(_cursorIndexOfCodeSnippet)) {
              _tmpCodeSnippet = null;
            } else {
              _tmpCodeSnippet = _cursor.getString(_cursorIndexOfCodeSnippet);
            }
            final String _tmpFullAnalysis;
            if (_cursor.isNull(_cursorIndexOfFullAnalysis)) {
              _tmpFullAnalysis = null;
            } else {
              _tmpFullAnalysis = _cursor.getString(_cursorIndexOfFullAnalysis);
            }
            final String _tmpGithubRepoName;
            if (_cursor.isNull(_cursorIndexOfGithubRepoName)) {
              _tmpGithubRepoName = null;
            } else {
              _tmpGithubRepoName = _cursor.getString(_cursorIndexOfGithubRepoName);
            }
            final String _tmpGithubRepoUrl;
            if (_cursor.isNull(_cursorIndexOfGithubRepoUrl)) {
              _tmpGithubRepoUrl = null;
            } else {
              _tmpGithubRepoUrl = _cursor.getString(_cursorIndexOfGithubRepoUrl);
            }
            final String _tmpGithubStars;
            if (_cursor.isNull(_cursorIndexOfGithubStars)) {
              _tmpGithubStars = null;
            } else {
              _tmpGithubStars = _cursor.getString(_cursorIndexOfGithubStars);
            }
            final String _tmpMetricLabel;
            if (_cursor.isNull(_cursorIndexOfMetricLabel)) {
              _tmpMetricLabel = null;
            } else {
              _tmpMetricLabel = _cursor.getString(_cursorIndexOfMetricLabel);
            }
            final String _tmpMetricValue;
            if (_cursor.isNull(_cursorIndexOfMetricValue)) {
              _tmpMetricValue = null;
            } else {
              _tmpMetricValue = _cursor.getString(_cursorIndexOfMetricValue);
            }
            final String _tmpEntityTagsJson;
            if (_cursor.isNull(_cursorIndexOfEntityTagsJson)) {
              _tmpEntityTagsJson = null;
            } else {
              _tmpEntityTagsJson = _cursor.getString(_cursorIndexOfEntityTagsJson);
            }
            final String _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getString(_cursorIndexOfPublishedAt);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            _item = new ArticleEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpSourceUrl,_tmpCategory,_tmpDepthLevel,_tmpTldr,_tmpTakeawaysJson,_tmpCodeSnippet,_tmpFullAnalysis,_tmpGithubRepoName,_tmpGithubRepoUrl,_tmpGithubStars,_tmpMetricLabel,_tmpMetricValue,_tmpEntityTagsJson,_tmpPublishedAt,_tmpIsBookmarked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ArticleEntity>> getArticlesByCategory(final String category) {
    final String _sql = "SELECT * FROM articles WHERE category = ? ORDER BY publishedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"articles"}, new Callable<List<ArticleEntity>>() {
      @Override
      @NonNull
      public List<ArticleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUrl");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDepthLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "depthLevel");
          final int _cursorIndexOfTldr = CursorUtil.getColumnIndexOrThrow(_cursor, "tldr");
          final int _cursorIndexOfTakeawaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "takeawaysJson");
          final int _cursorIndexOfCodeSnippet = CursorUtil.getColumnIndexOrThrow(_cursor, "codeSnippet");
          final int _cursorIndexOfFullAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "fullAnalysis");
          final int _cursorIndexOfGithubRepoName = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoName");
          final int _cursorIndexOfGithubRepoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoUrl");
          final int _cursorIndexOfGithubStars = CursorUtil.getColumnIndexOrThrow(_cursor, "githubStars");
          final int _cursorIndexOfMetricLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "metricLabel");
          final int _cursorIndexOfMetricValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metricValue");
          final int _cursorIndexOfEntityTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entityTagsJson");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final List<ArticleEntity> _result = new ArrayList<ArticleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ArticleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpSourceUrl;
            _tmpSourceUrl = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDepthLevel;
            _tmpDepthLevel = _cursor.getString(_cursorIndexOfDepthLevel);
            final String _tmpTldr;
            _tmpTldr = _cursor.getString(_cursorIndexOfTldr);
            final String _tmpTakeawaysJson;
            _tmpTakeawaysJson = _cursor.getString(_cursorIndexOfTakeawaysJson);
            final String _tmpCodeSnippet;
            if (_cursor.isNull(_cursorIndexOfCodeSnippet)) {
              _tmpCodeSnippet = null;
            } else {
              _tmpCodeSnippet = _cursor.getString(_cursorIndexOfCodeSnippet);
            }
            final String _tmpFullAnalysis;
            if (_cursor.isNull(_cursorIndexOfFullAnalysis)) {
              _tmpFullAnalysis = null;
            } else {
              _tmpFullAnalysis = _cursor.getString(_cursorIndexOfFullAnalysis);
            }
            final String _tmpGithubRepoName;
            if (_cursor.isNull(_cursorIndexOfGithubRepoName)) {
              _tmpGithubRepoName = null;
            } else {
              _tmpGithubRepoName = _cursor.getString(_cursorIndexOfGithubRepoName);
            }
            final String _tmpGithubRepoUrl;
            if (_cursor.isNull(_cursorIndexOfGithubRepoUrl)) {
              _tmpGithubRepoUrl = null;
            } else {
              _tmpGithubRepoUrl = _cursor.getString(_cursorIndexOfGithubRepoUrl);
            }
            final String _tmpGithubStars;
            if (_cursor.isNull(_cursorIndexOfGithubStars)) {
              _tmpGithubStars = null;
            } else {
              _tmpGithubStars = _cursor.getString(_cursorIndexOfGithubStars);
            }
            final String _tmpMetricLabel;
            if (_cursor.isNull(_cursorIndexOfMetricLabel)) {
              _tmpMetricLabel = null;
            } else {
              _tmpMetricLabel = _cursor.getString(_cursorIndexOfMetricLabel);
            }
            final String _tmpMetricValue;
            if (_cursor.isNull(_cursorIndexOfMetricValue)) {
              _tmpMetricValue = null;
            } else {
              _tmpMetricValue = _cursor.getString(_cursorIndexOfMetricValue);
            }
            final String _tmpEntityTagsJson;
            if (_cursor.isNull(_cursorIndexOfEntityTagsJson)) {
              _tmpEntityTagsJson = null;
            } else {
              _tmpEntityTagsJson = _cursor.getString(_cursorIndexOfEntityTagsJson);
            }
            final String _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getString(_cursorIndexOfPublishedAt);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            _item = new ArticleEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpSourceUrl,_tmpCategory,_tmpDepthLevel,_tmpTldr,_tmpTakeawaysJson,_tmpCodeSnippet,_tmpFullAnalysis,_tmpGithubRepoName,_tmpGithubRepoUrl,_tmpGithubStars,_tmpMetricLabel,_tmpMetricValue,_tmpEntityTagsJson,_tmpPublishedAt,_tmpIsBookmarked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ArticleEntity>> searchArticles(final String query) {
    final String _sql = "SELECT * FROM articles WHERE title LIKE '%' || ? || '%' OR tldr LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"articles"}, new Callable<List<ArticleEntity>>() {
      @Override
      @NonNull
      public List<ArticleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUrl");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDepthLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "depthLevel");
          final int _cursorIndexOfTldr = CursorUtil.getColumnIndexOrThrow(_cursor, "tldr");
          final int _cursorIndexOfTakeawaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "takeawaysJson");
          final int _cursorIndexOfCodeSnippet = CursorUtil.getColumnIndexOrThrow(_cursor, "codeSnippet");
          final int _cursorIndexOfFullAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "fullAnalysis");
          final int _cursorIndexOfGithubRepoName = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoName");
          final int _cursorIndexOfGithubRepoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoUrl");
          final int _cursorIndexOfGithubStars = CursorUtil.getColumnIndexOrThrow(_cursor, "githubStars");
          final int _cursorIndexOfMetricLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "metricLabel");
          final int _cursorIndexOfMetricValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metricValue");
          final int _cursorIndexOfEntityTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entityTagsJson");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final List<ArticleEntity> _result = new ArrayList<ArticleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ArticleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpSourceUrl;
            _tmpSourceUrl = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDepthLevel;
            _tmpDepthLevel = _cursor.getString(_cursorIndexOfDepthLevel);
            final String _tmpTldr;
            _tmpTldr = _cursor.getString(_cursorIndexOfTldr);
            final String _tmpTakeawaysJson;
            _tmpTakeawaysJson = _cursor.getString(_cursorIndexOfTakeawaysJson);
            final String _tmpCodeSnippet;
            if (_cursor.isNull(_cursorIndexOfCodeSnippet)) {
              _tmpCodeSnippet = null;
            } else {
              _tmpCodeSnippet = _cursor.getString(_cursorIndexOfCodeSnippet);
            }
            final String _tmpFullAnalysis;
            if (_cursor.isNull(_cursorIndexOfFullAnalysis)) {
              _tmpFullAnalysis = null;
            } else {
              _tmpFullAnalysis = _cursor.getString(_cursorIndexOfFullAnalysis);
            }
            final String _tmpGithubRepoName;
            if (_cursor.isNull(_cursorIndexOfGithubRepoName)) {
              _tmpGithubRepoName = null;
            } else {
              _tmpGithubRepoName = _cursor.getString(_cursorIndexOfGithubRepoName);
            }
            final String _tmpGithubRepoUrl;
            if (_cursor.isNull(_cursorIndexOfGithubRepoUrl)) {
              _tmpGithubRepoUrl = null;
            } else {
              _tmpGithubRepoUrl = _cursor.getString(_cursorIndexOfGithubRepoUrl);
            }
            final String _tmpGithubStars;
            if (_cursor.isNull(_cursorIndexOfGithubStars)) {
              _tmpGithubStars = null;
            } else {
              _tmpGithubStars = _cursor.getString(_cursorIndexOfGithubStars);
            }
            final String _tmpMetricLabel;
            if (_cursor.isNull(_cursorIndexOfMetricLabel)) {
              _tmpMetricLabel = null;
            } else {
              _tmpMetricLabel = _cursor.getString(_cursorIndexOfMetricLabel);
            }
            final String _tmpMetricValue;
            if (_cursor.isNull(_cursorIndexOfMetricValue)) {
              _tmpMetricValue = null;
            } else {
              _tmpMetricValue = _cursor.getString(_cursorIndexOfMetricValue);
            }
            final String _tmpEntityTagsJson;
            if (_cursor.isNull(_cursorIndexOfEntityTagsJson)) {
              _tmpEntityTagsJson = null;
            } else {
              _tmpEntityTagsJson = _cursor.getString(_cursorIndexOfEntityTagsJson);
            }
            final String _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getString(_cursorIndexOfPublishedAt);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            _item = new ArticleEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpSourceUrl,_tmpCategory,_tmpDepthLevel,_tmpTldr,_tmpTakeawaysJson,_tmpCodeSnippet,_tmpFullAnalysis,_tmpGithubRepoName,_tmpGithubRepoUrl,_tmpGithubStars,_tmpMetricLabel,_tmpMetricValue,_tmpEntityTagsJson,_tmpPublishedAt,_tmpIsBookmarked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ArticleEntity>> getBookmarkedArticles() {
    final String _sql = "SELECT * FROM articles WHERE isBookmarked = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"articles"}, new Callable<List<ArticleEntity>>() {
      @Override
      @NonNull
      public List<ArticleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUrl");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDepthLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "depthLevel");
          final int _cursorIndexOfTldr = CursorUtil.getColumnIndexOrThrow(_cursor, "tldr");
          final int _cursorIndexOfTakeawaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "takeawaysJson");
          final int _cursorIndexOfCodeSnippet = CursorUtil.getColumnIndexOrThrow(_cursor, "codeSnippet");
          final int _cursorIndexOfFullAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "fullAnalysis");
          final int _cursorIndexOfGithubRepoName = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoName");
          final int _cursorIndexOfGithubRepoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "githubRepoUrl");
          final int _cursorIndexOfGithubStars = CursorUtil.getColumnIndexOrThrow(_cursor, "githubStars");
          final int _cursorIndexOfMetricLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "metricLabel");
          final int _cursorIndexOfMetricValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metricValue");
          final int _cursorIndexOfEntityTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entityTagsJson");
          final int _cursorIndexOfPublishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedAt");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final List<ArticleEntity> _result = new ArrayList<ArticleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ArticleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpSourceUrl;
            _tmpSourceUrl = _cursor.getString(_cursorIndexOfSourceUrl);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDepthLevel;
            _tmpDepthLevel = _cursor.getString(_cursorIndexOfDepthLevel);
            final String _tmpTldr;
            _tmpTldr = _cursor.getString(_cursorIndexOfTldr);
            final String _tmpTakeawaysJson;
            _tmpTakeawaysJson = _cursor.getString(_cursorIndexOfTakeawaysJson);
            final String _tmpCodeSnippet;
            if (_cursor.isNull(_cursorIndexOfCodeSnippet)) {
              _tmpCodeSnippet = null;
            } else {
              _tmpCodeSnippet = _cursor.getString(_cursorIndexOfCodeSnippet);
            }
            final String _tmpFullAnalysis;
            if (_cursor.isNull(_cursorIndexOfFullAnalysis)) {
              _tmpFullAnalysis = null;
            } else {
              _tmpFullAnalysis = _cursor.getString(_cursorIndexOfFullAnalysis);
            }
            final String _tmpGithubRepoName;
            if (_cursor.isNull(_cursorIndexOfGithubRepoName)) {
              _tmpGithubRepoName = null;
            } else {
              _tmpGithubRepoName = _cursor.getString(_cursorIndexOfGithubRepoName);
            }
            final String _tmpGithubRepoUrl;
            if (_cursor.isNull(_cursorIndexOfGithubRepoUrl)) {
              _tmpGithubRepoUrl = null;
            } else {
              _tmpGithubRepoUrl = _cursor.getString(_cursorIndexOfGithubRepoUrl);
            }
            final String _tmpGithubStars;
            if (_cursor.isNull(_cursorIndexOfGithubStars)) {
              _tmpGithubStars = null;
            } else {
              _tmpGithubStars = _cursor.getString(_cursorIndexOfGithubStars);
            }
            final String _tmpMetricLabel;
            if (_cursor.isNull(_cursorIndexOfMetricLabel)) {
              _tmpMetricLabel = null;
            } else {
              _tmpMetricLabel = _cursor.getString(_cursorIndexOfMetricLabel);
            }
            final String _tmpMetricValue;
            if (_cursor.isNull(_cursorIndexOfMetricValue)) {
              _tmpMetricValue = null;
            } else {
              _tmpMetricValue = _cursor.getString(_cursorIndexOfMetricValue);
            }
            final String _tmpEntityTagsJson;
            if (_cursor.isNull(_cursorIndexOfEntityTagsJson)) {
              _tmpEntityTagsJson = null;
            } else {
              _tmpEntityTagsJson = _cursor.getString(_cursorIndexOfEntityTagsJson);
            }
            final String _tmpPublishedAt;
            _tmpPublishedAt = _cursor.getString(_cursorIndexOfPublishedAt);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            _item = new ArticleEntity(_tmpId,_tmpTitle,_tmpSourceName,_tmpSourceUrl,_tmpCategory,_tmpDepthLevel,_tmpTldr,_tmpTakeawaysJson,_tmpCodeSnippet,_tmpFullAnalysis,_tmpGithubRepoName,_tmpGithubRepoUrl,_tmpGithubStars,_tmpMetricLabel,_tmpMetricValue,_tmpEntityTagsJson,_tmpPublishedAt,_tmpIsBookmarked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM articles";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
}
