package ly.pp.justpiano3.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.*
import ly.pp.justpiano3.database.entity.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun getAllSongs(): List<Song>

    // 获取支持分页加载的数据源的查询方法
    @Query("SELECT * FROM song")
    fun getAllSongsWithDataSource(): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item IN (:category)")
    fun getSongsByCategoriesWithDataSource(vararg category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE name LIKE '%' || :keyWord || '%'")
    fun getSongsByNameKeywordsWithDataSource(keyWord: String): DataSource.Factory<Int, Song>

    enum class ORDER {
        NAME_ASC, NAME_DESC, IS_NEW_ASC, DATE_DESC, DEGREE_ASC, DEGREE_DESC, SCORE_ASC, SCORE_DESC, LENGTH_ASC, LENGTH_DESC
    }

    fun getOrderedSongsByCategoryWithDataSource(category: String, orderByIndex: Int): DataSource.Factory<Int, Song> {
        return when (orderByIndex) {
            ORDER.NAME_ASC.ordinal -> getNameAscOrderedSongsByCategoryWithDataSource(category)
            ORDER.NAME_DESC.ordinal -> getNameDescOrderedSongsByCategoryWithDataSource(category)
            ORDER.IS_NEW_ASC.ordinal -> getIsNewAscOrderedSongsByCategoryWithDataSource(category)
            ORDER.DATE_DESC.ordinal -> getDateDescOrderedSongsByCategoryWithDataSource(category)
            ORDER.DEGREE_ASC.ordinal -> getDegreeAscOrderedSongsByCategoryWithDataSource(category)
            ORDER.DEGREE_DESC.ordinal -> getDegreeDescOrderedSongsByCategoryWithDataSource(category)
            ORDER.SCORE_ASC.ordinal -> getScoreAscOrderedSongsByCategoryWithDataSource(category)
            ORDER.SCORE_DESC.ordinal -> getScoreDescOrderedSongsByCategoryWithDataSource(category)
            ORDER.LENGTH_ASC.ordinal -> getLengthAscOrderedSongsByCategoryWithDataSource(category)
            ORDER.LENGTH_DESC.ordinal -> getLengthDescOrderedSongsByCategoryWithDataSource(category)
            else -> getNameAscOrderedSongsByCategoryWithDataSource(category)
        }
    }

    @Query("SELECT * FROM song WHERE item = :category ORDER BY name ASC")
    fun getNameAscOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY name DESC")
    fun getNameDescOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY isnew ASC")
    fun getIsNewAscOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY date DESC")
    fun getDateDescOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY diff ASC")
    fun getDegreeAscOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY diff DESC")
    fun getDegreeDescOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY score ASC")
    fun getScoreAscOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY score DESC")
    fun getScoreDescOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY length ASC")
    fun getLengthAscOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE item = :category ORDER BY length DESC")
    fun getLengthDescOrderedSongsByCategoryWithDataSource(category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE isfavo = 1")
    fun getFavoriteSongs(): List<Song>

    @Query("SELECT * FROM song WHERE isfavo = 1")
    fun getFavoriteSongsWithDataSource(): DataSource.Factory<Int, Song>

    @Query("SELECT COUNT(1) AS totalCount, (SUM(score) + SUM(Lscore)) AS totalScore FROM song")
    fun getAllSongsCountAndScore(): List<TotalSongInfo>

    /**
     * 曲谱同步，传入新增的、更新的和删除的曲谱，事务批量执行
     */
    @Transaction
    fun syncSongs(insertSongs: List<Song>, updateSongs: List<Song>, deleteSongs: List<Song>) {
        if (insertSongs.isNotEmpty()) {
            insertSongs(insertSongs)
        }
        if (updateSongs.isNotEmpty()) {
            updateSongs(updateSongs)
        }
        if (deleteSongs.isNotEmpty()) {
            deleteSongs(deleteSongs)
        }
    }

    @Insert
    fun insertSongs(insertSongs: List<Song>)

    @Update
    fun updateSongs(updateSongs: List<Song>): Int

    @Delete
    fun deleteSongs(deleteSongs: List<Song>)

    @Query("UPDATE song SET isfavo = :isFavorite, score = :rightHandHighScore, Lscore = :leftHandHeightScore WHERE path = :path")
    fun updateSongInfoByPath(isFavorite: Int, rightHandHighScore: Int, leftHandHeightScore: Int, path: String): Int

    @Query("SELECT * FROM song WHERE name = :name")
    fun getSongByName(name: String): List<Song>

    @Query("SELECT * FROM song WHERE path = :filePath")
    fun getSongByFilePath(filePath: String): List<Song>

    @Query("SELECT * FROM song WHERE diff BETWEEN :startDegree AND :endDegree ORDER BY RANDOM() LIMIT 1")
    fun getSongByRightHandDegreeWithRandom(startDegree: Int, endDegree: Int): List<Song>

    @Query("SELECT * FROM song WHERE isfavo = 1 ORDER BY RANDOM() LIMIT 1")
    fun getSongInFavoriteWithRandom(): List<Song>

    @Query("UPDATE song SET isfavo = :isFavorite WHERE path = :filePath")
    fun updateFavoriteSong(filePath: String, isFavorite: Int): Int

    private val _config: PagedList.Config
        get() = PagedList.Config.Builder().setPageSize(100).setEnablePlaceholders(false).build()

    fun getPageListByDatasourceFactory(dataSourceFactory: DataSource.Factory<Int, Song>): LiveData<PagedList<Song>> {
        return LivePagedListBuilder(dataSourceFactory, _config).build()
    }

    data class TotalSongInfo(var totalScore: Int = 0, var totalCount: Int = 0)
}