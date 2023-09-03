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

    @Query("SELECT * FROM song WHERE item = :category ORDER BY :orderBy")
    fun getOrderedSongsByCategoryWithDataSource(category: String, orderBy: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE isfavo = 1")
    fun getFavoriteSongs(): List<Song>

    @Query("SELECT * FROM song WHERE isfavo = 1")
    fun getFavoriteSongsWithDataSource(): DataSource.Factory<Int, Song>

    @Query("SELECT COUNT(1) AS totalCount, SUM(score) AS totalScore FROM song")
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