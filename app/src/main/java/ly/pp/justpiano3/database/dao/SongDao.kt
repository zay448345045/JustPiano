package ly.pp.justpiano3.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.*
import ly.pp.justpiano3.database.entity.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song WHERE online = 1")
    fun getAllSongs(): List<Song>

    @Query("SELECT * FROM song WHERE online = 1")
    fun getAllSongsWithDataSource(): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND item IN (:category)")
    fun getSongsByCategoriesWithDataSource(vararg category: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND name LIKE '%' || :keyWord || '%'")
    fun getSongsByNameKeywordsWithDataSource(keyWord: String): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 0")
    fun getLocalImportSongs(): DataSource.Factory<Int, Song>

    enum class Category(val code: Int, val desc: String) {
        CLASSICS(1, "经典乐章"), POP(2, "流行空间"), FILM(3, "影视剧场"), CHILD(4, "儿时回忆"),
        COMICS(5, "动漫原声"), GAME(6, "游戏主题"), RED(7, "红色歌谣"), ORIGINAL(8, "原创作品");
    }

    enum class Order {
        NAME_ASC, NAME_DESC, IS_NEW_ASC, DATE_DESC, DEGREE_ASC, DEGREE_DESC, SCORE_ASC, SCORE_DESC, LENGTH_ASC, LENGTH_DESC
    }

    fun getLocalSongsWithDataSource(categoryIndex: Int, orderByIndex: Int): DataSource.Factory<Int, Song> {
        val categoryEnum = enumValues<Category>().find { it.code == categoryIndex }
        val isFavorite = if (categoryIndex == 0) arrayOf(1) else arrayOf(0, 1)
        val category = categoryEnum?.let { arrayOf(it.desc) } ?: enumValues<Category>().map { it.desc }.toTypedArray()
        return when (orderByIndex) {
            // room库不支持直接拼接order by字段，只能分别走调用
            Order.NAME_ASC.ordinal -> getNameAscSongsWithDataSource(isFavorite, category)
            Order.NAME_DESC.ordinal -> getNameDescSongsWithDataSource(isFavorite, category)
            Order.IS_NEW_ASC.ordinal -> getIsNewAscSongsWithDataSource(isFavorite, category)
            Order.DATE_DESC.ordinal -> getDateDescSongsWithDataSource(isFavorite, category)
            Order.DEGREE_ASC.ordinal -> getDegreeAscSongsWithDataSource(isFavorite, category)
            Order.DEGREE_DESC.ordinal -> getDegreeDescSongsWithDataSource(isFavorite, category)
            Order.SCORE_ASC.ordinal -> getScoreAscSongsWithDataSource(isFavorite, category)
            Order.SCORE_DESC.ordinal -> getScoreDescSongsWithDataSource(isFavorite, category)
            Order.LENGTH_ASC.ordinal -> getLengthAscSongsWithDataSource(isFavorite, category)
            Order.LENGTH_DESC.ordinal -> getLengthDescSongsWithDataSource(isFavorite, category)
            else -> getNameAscSongsWithDataSource(isFavorite, category)
        }
    }

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY name ASC")
    fun getNameAscSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY name DESC")
    fun getNameDescSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY isnew ASC")
    fun getIsNewAscSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY date DESC")
    fun getDateDescSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY diff ASC")
    fun getDegreeAscSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY diff DESC")
    fun getDegreeDescSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY score ASC")
    fun getScoreAscSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY score DESC")
    fun getScoreDescSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY length ASC")
    fun getLengthAscSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo IN (:isFavorite) AND item IN (:category) ORDER BY length DESC")
    fun getLengthDescSongsWithDataSource(isFavorite: Array<Int>, category: Array<String>): DataSource.Factory<Int, Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo = 1")
    fun getFavoriteSongs(): List<Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo = 1")
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

    @Query("UPDATE song SET isfavo = :isFavorite, score = :rightHandHighScore, Lscore = :leftHandHeightScore WHERE online = 1 AND path = :path")
    fun updateSongInfoByPath(isFavorite: Int, rightHandHighScore: Int, leftHandHeightScore: Int, path: String): Int

    @Query("SELECT * FROM song WHERE online = 1 AND name = :name")
    fun getSongByName(name: String): List<Song>

    @Query("SELECT * FROM song WHERE online = 1 AND path = :filePath")
    fun getSongByFilePath(filePath: String): List<Song>

    @Query("SELECT * FROM song WHERE online = 1 AND diff BETWEEN :startDegree AND :endDegree ORDER BY RANDOM() LIMIT 1")
    fun getSongByRightHandDegreeWithRandom(startDegree: Int, endDegree: Int): List<Song>

    @Query("SELECT * FROM song WHERE online = 1 AND isfavo = 1 ORDER BY RANDOM() LIMIT 1")
    fun getSongInFavoriteWithRandom(): List<Song>

    @Query("UPDATE song SET isfavo = :isFavorite WHERE online = 1 AND path = :filePath")
    fun updateFavoriteSong(filePath: String, isFavorite: Int): Int

    private val _config: PagedList.Config
        get() = PagedList.Config.Builder().setPageSize(100).setEnablePlaceholders(false).build()

    fun getPageListByDatasourceFactory(dataSourceFactory: DataSource.Factory<Int, Song>): LiveData<PagedList<Song>> {
        return LivePagedListBuilder(dataSourceFactory, _config).build()
    }

    data class TotalSongInfo(var totalScore: Int = 0, var totalCount: Int = 0)
}