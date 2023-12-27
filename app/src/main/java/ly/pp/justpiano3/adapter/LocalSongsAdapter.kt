package ly.pp.justpiano3.adapter

import android.content.Intent
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.R
import ly.pp.justpiano3.activity.local.MelodySelect
import ly.pp.justpiano3.activity.local.WaterfallActivity
import ly.pp.justpiano3.constant.Consts
import ly.pp.justpiano3.database.entity.Song
import ly.pp.justpiano3.listener.LocalSongsStartPlayClick
import ly.pp.justpiano3.thread.SongPlay
import ly.pp.justpiano3.view.ScrollText
import java.io.File

class LocalSongsAdapter(
    private val melodySelect: MelodySelect,
    private val songsListView: RecyclerView
) :
    PagedListAdapter<Song, LocalSongsAdapter.SongViewHolder>(Consts.SONG_DIFF_UTIL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.lo_songview, parent, false)
        itemView.setBackgroundResource(R.drawable.selector_list_c)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        // 绑定数据到ViewHolder
        getItem(position)?.let { holder.bindData(position, it) }
    }

    override fun onCurrentListChanged(
        previousList: PagedList<Song>?,
        currentList: PagedList<Song>?
    ) {
        super.onCurrentListChanged(previousList, currentList)
        // 当数据列表发生更改时，滚动到第一个位置
        songsListView.scrollToPosition(0)
    }

    fun getSongByPosition(position: Int): Song? {
        return getItem(position)
    }

    inner class SongViewHolder(songView: View) : RecyclerView.ViewHolder(songView) {
        private val listenImageView: ImageView
        private val waterFallImageView: ImageView
        private val favorImageView: ImageView
        private val isNewImageView: ImageView
        private val playImageView: ImageView
        private val highScoreTextView: TextView
        private val soundTimeTextView: TextView
        private val rightHandDegreeTextView: TextView
        private val leftHandDegreeTextView: TextView
        private val rightHandDegreeRatingBar: RatingBar
        private val leftHandDegreeRatingBar: RatingBar
        private val songNameScrollText: ScrollText

        fun bindData(position: Int, song: Song) {
            playImageView.setOnClickListener(LocalSongsStartPlayClick(melodySelect, song))
            listenImageView.setOnClickListener {
                if (song.filePath == melodySelect.songsPath && SongPlay.isPlaying()) {
                    melodySelect.songsPath = ""
                    SongPlay.stopPlay()
                    return@setOnClickListener
                }
                melodySelect.songsPath = song.filePath
                melodySelect.songsPositionInListView = position
                SongPlay.startPlay(melodySelect, song.filePath, 0)
                Toast.makeText(melodySelect, "开始播放:《" + song.name + "》", Toast.LENGTH_SHORT)
                    .show()
            }
            waterFallImageView.setOnClickListener {
                SongPlay.stopPlay()
                val intent = Intent()
                intent.putExtra("songPath", song.filePath)
                intent.setClass(melodySelect, WaterfallActivity::class.java)
                melodySelect.startActivity(intent)
            }
            if (song.category == Consts.items[Consts.items.size - 1]) {
                favorImageView.setImageResource(R.drawable.dele)
                favorImageView.setOnClickListener {
                    val songDao = JPApplication.getSongDatabase().songDao()
                    songDao.deleteSongs(listOf(song))
                    File(melodySelect.filesDir.absolutePath + "/ImportSongs/" + song.name + ".pm").delete()
                    Toast.makeText(melodySelect, song.name + ":已删除", Toast.LENGTH_SHORT).show()
                }
            } else {
                favorImageView.setImageResource(if (song.isFavorite == 0) R.drawable.favor_1 else R.drawable.favor)
                favorImageView.setOnClickListener {
                    val songDao = JPApplication.getSongDatabase().songDao()
                    melodySelect.pagedListLiveData.removeObservers(melodySelect)
                    songDao.updateFavoriteSong(song.filePath, if (song.isFavorite == 0) 1 else 0)
                    val songsDataSource =
                        songDao.getLocalSongsWithDataSource(0, melodySelect.orderPosition)
                    melodySelect.pagedListLiveData =
                        songDao.getPageListByDatasourceFactory(songsDataSource)
                    melodySelect.pagedListLiveData.observe(melodySelect) { pagedList: PagedList<Song>? ->
                        this@LocalSongsAdapter.submitList(pagedList)
                    }
                    Toast.makeText(
                        melodySelect,
                        song.name + if (song.isFavorite == 0) ":已加入收藏夹" else ":已移出收藏夹",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            songNameScrollText.text = song.name
            songNameScrollText.movementMethod = ScrollingMovementMethod.getInstance()
            songNameScrollText.setHorizontallyScrolling(true)
            songNameScrollText.setOnClickListener(LocalSongsStartPlayClick(melodySelect, song))
            val rightHandScore = song.rightHandHighScore
            val leftHandScore = song.leftHandHighScore
            highScoreTextView.text =
                "右手最高:" + (if (rightHandScore <= 0) "0" else rightHandScore.toString()) +
                        " 左手最高: " + if (leftHandScore <= 0) "0" else leftHandScore.toString()
            val length = song.length
            val str1 = if (length / 60 >= 10) "" + length / 60 else "0" + length / 60
            val str2 = if (length % 60 >= 10) "" + length % 60 else "0" + length % 60
            soundTimeTextView.text = "$str1:$str2"
            val rightHandDegree = song.rightHandDegree
            rightHandDegreeTextView.text =
                if (rightHandDegree.toInt() == 10) " 难度: 右手 10 " else " 难度: 右手 $rightHandDegree"
            rightHandDegreeRatingBar.numStars = 5
            rightHandDegreeRatingBar.isClickable = false
            rightHandDegreeRatingBar.rating = rightHandDegree / 2
            val leftHandDegree = song.leftHandDegree
            leftHandDegreeTextView.text =
                if (leftHandDegree.toInt() == 10) " 左手 10" else " 左手 $leftHandDegree"
            leftHandDegreeRatingBar.numStars = 5
            leftHandDegreeRatingBar.isClickable = false
            leftHandDegreeRatingBar.rating = leftHandDegree / 2
            isNewImageView.setImageResource(if (song.isNew == 1) R.drawable.s_new else R.drawable.null_pic)
        }

        init {
            listenImageView = songView.findViewById(R.id.play_image)
            waterFallImageView = songView.findViewById(R.id.play_waterfall)
            favorImageView = songView.findViewById(R.id.favor)
            isNewImageView = songView.findViewById(R.id.is_new)
            playImageView = songView.findViewById(R.id.play)
            highScoreTextView = songView.findViewById(R.id.highscore)
            soundTimeTextView = songView.findViewById(R.id.sound_time)
            rightHandDegreeTextView = songView.findViewById(R.id.nandu_1)
            leftHandDegreeTextView = songView.findViewById(R.id.nandu_2)
            rightHandDegreeRatingBar = songView.findViewById(R.id.nandu)
            leftHandDegreeRatingBar = songView.findViewById(R.id.leftnandu)
            songNameScrollText = songView.findViewById(R.id.s_n)
        }
    }
}