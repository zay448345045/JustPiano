package ly.pp.justpiano3.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ly.pp.justpiano3.BuildConfig
import ly.pp.justpiano3.database.dao.SongDao
import ly.pp.justpiano3.database.entity.Song

@Database(entities = [Song::class], exportSchema = false, version = BuildConfig.VERSION_CODE)
abstract class SongDatabase : RoomDatabase() {

    /**
     * 曲谱DAO
     */
    abstract fun songDao(): SongDao
}
