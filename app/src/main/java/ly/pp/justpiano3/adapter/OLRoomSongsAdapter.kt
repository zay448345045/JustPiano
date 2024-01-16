package ly.pp.justpiano3.adapter

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.R
import ly.pp.justpiano3.activity.online.OLPlayRoom
import ly.pp.justpiano3.constant.Consts
import ly.pp.justpiano3.database.entity.Song

class OLRoomSongsAdapter(
    private val olPlayRoom: OLPlayRoom,
    private val songsListView: RecyclerView
) :
    PagedListAdapter<Song, OLRoomSongsAdapter.SongViewHolder>(Consts.SONG_DIFF_UTIL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.olroom_songview, parent, false)
        itemView.setBackgroundResource(R.drawable.selector_list_c)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        // 绑定数据到ViewHolder
        getItem(position)?.let { holder.bindData(it) }
    }

    override fun onCurrentListChanged(
        previousList: PagedList<Song>?,
        currentList: PagedList<Song>?
    ) {
        super.onCurrentListChanged(previousList, currentList)
        // 当数据列表发生更改时，滚动到第一个位置
        songsListView.scrollToPosition(0)
    }

    inner class SongViewHolder(songView: View) : RecyclerView.ViewHolder(songView) {
        private val songNameTextView: TextView
        private val timeTextView: TextView
        private val rightHandDegreeTextView: TextView
        private val leftHandDegreeTextView: TextView
        private val songFavorImageView: ImageView
        private val rightHandDegreeRatingBar: RatingBar
        private val leftHandDegreeRatingBar: RatingBar

        init {
            songNameTextView = songView.findViewById(R.id.song_name)
            songFavorImageView = songView.findViewById(R.id.song_favor)
            rightHandDegreeRatingBar = songView.findViewById(R.id.song_degree)
            leftHandDegreeRatingBar = songView.findViewById(R.id.song_degree2)
            timeTextView = songView.findViewById(R.id.ol_sound_time)
            rightHandDegreeTextView = songView.findViewById(R.id.nandu_1)
            leftHandDegreeTextView = songView.findViewById(R.id.nandu_2)
            songView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let {
                        olPlayRoom.currentPlaySongPath = it.filePath
                        olPlayRoom.updateNewSongPlay(it.filePath)
                    }
                }
            }
        }

        fun bindData(song: Song) {
            songNameTextView.text = song.name
            songNameTextView.movementMethod = ScrollingMovementMethod.getInstance()
            songNameTextView.setHorizontallyScrolling(true)
            songNameTextView.setOnClickListener {
                olPlayRoom.currentPlaySongPath = song.filePath
                olPlayRoom.updateNewSongPlay(song.filePath)
            }
            songFavorImageView.setImageResource(if (song.isFavorite == 0) R.drawable.favor_1 else R.drawable.favor)
            songFavorImageView.setOnClickListener {
                val songDao = JPApplication.getSongDatabase().songDao()
                val favoriteSongList = songDao.getFavoriteSongsWithDataSource()
                olPlayRoom.pagedListLiveData.removeObservers(olPlayRoom)
                songDao.updateFavoriteSong(song.filePath, if (song.isFavorite == 0) 1 else 0)
                olPlayRoom.pagedListLiveData =
                    songDao.getPageListByDatasourceFactory(favoriteSongList)
                olPlayRoom.pagedListLiveData.observe(olPlayRoom) { pagedList: PagedList<Song>? ->
                    this@OLRoomSongsAdapter.submitList(
                        pagedList
                    )
                }
            }
            rightHandDegreeTextView.text = " 右手:" + song.rightHandDegree
            leftHandDegreeTextView.text = " 左手:" + song.leftHandDegree
            timeTextView.text = (if (song.length / 60 < 10) "0" else "") +
                    song.length / 60 + ":" + (if (song.length % 60 < 10) "0" else "") + song.length % 60
            rightHandDegreeRatingBar.numStars = 5
            rightHandDegreeRatingBar.isClickable = false
            rightHandDegreeRatingBar.rating = song.rightHandDegree / 2
            leftHandDegreeRatingBar.numStars = 5
            leftHandDegreeRatingBar.isClickable = false
            leftHandDegreeRatingBar.rating = song.leftHandDegree / 2
        }
    }
}