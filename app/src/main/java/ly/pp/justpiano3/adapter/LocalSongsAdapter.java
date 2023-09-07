package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.WaterfallActivity;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.listener.LocalSongsStartPlayClick;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.view.ScrollText;

import java.util.Objects;

public class LocalSongsAdapter extends PagedListAdapter<Song, LocalSongsAdapter.SongViewHolder> {
    private final MelodySelect melodySelect;
    private final RecyclerView songsListView;

    public LocalSongsAdapter(MelodySelect melodySelect, RecyclerView songsListView) {
        super(DIFF_CALLBACK);
        this.melodySelect = melodySelect;
        this.songsListView = songsListView;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.c_view, parent, false);
        itemView.setBackgroundResource(R.drawable.selector_list_c);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // 绑定数据到ViewHolder
        Song song = getItem(position);
        if (song != null) {
            holder.bindData(song);
        }
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Song> previousList, @Nullable PagedList<Song> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        // 当数据列表发生更改时，滚动到第一个位置
        songsListView.scrollToPosition(0);
    }

    protected class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView listenImageView;
        private final ImageView waterFallImageView;
        private final ImageView favorImageView;
        private final ImageView isNewImageView;
        private final ImageView playImageView;
        private final TextView highScoreTextView;
        private final TextView soundTimeTextView;
        private final TextView rightHandDegreeTextView;
        private final TextView leftHandDegreeTextView;
        private final RatingBar rightHandDegreeRatingBar;
        private final RatingBar leftHandDegreeRatingBar;
        private final ScrollText songNameScrollText;

        public SongViewHolder(@NonNull View songView) {
            super(songView);
            listenImageView = songView.findViewById(R.id.play_image);
            waterFallImageView = songView.findViewById(R.id.play_waterfall);
            favorImageView = songView.findViewById(R.id.favor);
            isNewImageView = songView.findViewById(R.id.is_new);
            playImageView = songView.findViewById(R.id.play);
            highScoreTextView = songView.findViewById(R.id.highscore);
            soundTimeTextView = songView.findViewById(R.id.sound_time);
            rightHandDegreeTextView = songView.findViewById(R.id.nandu_1);
            leftHandDegreeTextView = songView.findViewById(R.id.nandu_2);
            rightHandDegreeRatingBar = songView.findViewById(R.id.nandu);
            leftHandDegreeRatingBar = songView.findViewById(R.id.leftnandu);
            songNameScrollText = songView.findViewById(R.id.s_n);
            songView.setTag(getAdapterPosition());
        }

        public void bindData(Song song) {
            playImageView.setOnClickListener(new LocalSongsStartPlayClick(melodySelect, song));
            listenImageView.setOnClickListener(v -> {
                if (song.getFilePath().equals(melodySelect.songsPath)) {
                    melodySelect.songsPath = "";
                    return;
                }
                melodySelect.songsPath = song.getFilePath();
                SongPlay.INSTANCE.startPlay(melodySelect, song.getFilePath(), 0);
                Toast.makeText(melodySelect, "正在播放:《" + song.getName() + "》", Toast.LENGTH_SHORT).show();
            });
            waterFallImageView.setOnClickListener(v -> {
                SongPlay.INSTANCE.stopPlay();
                Intent intent = new Intent();
                intent.putExtra("songPath", song.getFilePath());
                intent.setClass(melodySelect, WaterfallActivity.class);
                melodySelect.startActivity(intent);
            });
            favorImageView.setOnClickListener(v -> {
                JPApplication.getSongDatabase().songDao().updateFavoriteSong(song.getFilePath(), song.isFavorite() == 0 ? 1 : 0);
                Toast.makeText(melodySelect, song.getName() + (song.isFavorite() == 0 ? ":已加入收藏夹" : ":已移出收藏夹"), Toast.LENGTH_SHORT).show();
            });
            songNameScrollText.setText(song.getName());
            songNameScrollText.setMovementMethod(ScrollingMovementMethod.getInstance());
            songNameScrollText.setHorizontallyScrolling(true);
            songNameScrollText.setOnClickListener(new LocalSongsStartPlayClick(melodySelect, song));
            int rightHandScore = song.getRightHandHighScore();
            int leftHandScore = song.getLeftHandHighScore();
            highScoreTextView.setText("右手最高:" + (rightHandScore <= 0 ? "0" : String.valueOf(rightHandScore)) + " 左手最高: " + (leftHandScore <= 0 ? "0" : String.valueOf(leftHandScore)));
            int length = song.getLength();
            String str1 = length / 60 >= 10 ? "" + length / 60 : "0" + length / 60;
            String str2 = length % 60 >= 10 ? "" + length % 60 : "0" + length % 60;
            soundTimeTextView.setText(str1 + ":" + str2);
            float rightHandDegree = song.getRightHandDegree();
            if ((int) rightHandDegree == 10) {
                rightHandDegreeTextView.setText(" 难度: 右手 10 ");
            } else {
                rightHandDegreeTextView.setText(" 难度: 右手 " + rightHandDegree);
            }
            rightHandDegreeRatingBar.setNumStars(5);
            rightHandDegreeRatingBar.setClickable(false);
            rightHandDegreeRatingBar.setRating(rightHandDegree / 2);
            float leftHandDegree = song.getLeftHandDegree();
            if ((int) leftHandDegree == 10) {
                leftHandDegreeTextView.setText(" 左手 10");
            } else {
                leftHandDegreeTextView.setText(" 左手 " + leftHandDegree);
            }
            leftHandDegreeRatingBar.setNumStars(5);
            leftHandDegreeRatingBar.setClickable(false);
            leftHandDegreeRatingBar.setRating(leftHandDegree / 2);
            if (song.isNew() == 1) {
                isNewImageView.setImageResource(R.drawable.s_new);
            } else {
                isNewImageView.setImageResource(R.drawable.null_pic);
            }
        }
    }

    public static final DiffUtil.ItemCallback<Song> DIFF_CALLBACK = new DiffUtil.ItemCallback<Song>() {
        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.equals(newItem);
        }
    };
}
