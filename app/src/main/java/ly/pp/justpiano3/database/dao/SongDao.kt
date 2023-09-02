package ly.pp.justpiano3.database.dao

import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.*
import ly.pp.justpiano3.database.entity.Song


@Dao
interface SongDao {

    @Query("SELECT * FROM jp_data")
    fun getAllSongs(): List<Song>

    // 获取支持分页加载的数据源的查询方法
    @Query("SELECT * FROM jp_data")
    fun getAllSongsWithDataSource(): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM jp_data WHERE item IN (:category)")
    fun getSongsByCategoryWithDataSource(vararg category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM jp_data WHERE name LIKE '%' || :keyWord || '%'")
    fun getSongByNameKeywordsWithDataSource(keyWord: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM jp_data WHERE isfavo = 1")
    fun getFavoriteSong():  List<Song>

    @Query("SELECT * FROM jp_data WHERE isfavo = 1")
    fun getFavoriteSongWithDataSource():  DataSource.Factory<Int, Song>

    /**
     * 曲谱同步，传入新增的、更新的和删除的曲谱，事务批量执行
     */
    @Transaction
    fun syncSongs(insertSongs: List<Song>, updateSongs: List<Song>, deleteSongs: List<Song>) {
        insertSongs(*insertSongs.toTypedArray())
        updateSongs(*updateSongs.toTypedArray())
        deleteSongs(*deleteSongs.toTypedArray())
    }

    @Insert
    fun insertSongs(vararg songs: Song)

    @Update
    fun updateSongs(vararg songs: Song): Int

    @Delete
    fun deleteSongs(vararg songs: Song)

    @Query("SELECT * FROM jp_data WHERE name = :name")
    fun getSongByName(name: String): List<Song>

    @Query("SELECT * FROM jp_data WHERE path = :filePath")
    fun getSongByFilePath(filePath: String): List<Song>

    @Query("SELECT * FROM jp_data WHERE diff BETWEEN :startDegree AND :endDegree ORDER BY RANDOM() LIMIT 1")
    fun getSongByRightHandDegreeWithRandom(startDegree: Int, endDegree: Int): List<Song>

    @Query("SELECT * FROM jp_data WHERE isfavo = 1 ORDER BY RANDOM() LIMIT 1")
    fun getSongInFavoriteWithRandom(): List<Song>

    @Query("UPDATE jp_data SET isfavo = :isFavorite WHERE path = :filePath")
    fun updateFavoriteSong(filePath: String, isFavorite: Int): Int

    private val _config: PagedList.Config
        get() = PagedList.Config.Builder().setPageSize(100).setEnablePlaceholders(false).build()

    fun getPageListByDatasourceFactory(dataSourceFactory: DataSource.Factory<Int, Song>): PagedList<Song> {
        val pagedListLiveData = LivePagedListBuilder(dataSourceFactory, _config).build()
        return pagedListLiveData.value!!
    }
}