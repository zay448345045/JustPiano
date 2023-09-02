package ly.pp.justpiano3.adapter;

import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.thread.PlaySongs;
import protobuf.dto.OnlinePlaySongDTO;

import java.util.Objects;

public class OLRoomSongsAdapter extends PagedListAdapter<Song, OLRoomSongsAdapter.SongViewHolder> {
    private final OLPlayRoom olPlayRoom;

    public OLRoomSongsAdapter(OLPlayRoom olPlayRoom) {
        super(DIFF_CALLBACK);
        this.olPlayRoom = olPlayRoom;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.song_view, null);
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

    protected class SongViewHolder extends RecyclerView.ViewHolder {
        private final TextView songNameTextView;
        private final TextView timeTextView;
        private final TextView rightHandDegreeTextView;
        private final TextView leftHandDegreeTextView;
        private final ImageView songFavorImageView;
        private final RatingBar rightHandDegreeRatingBar;
        private final RatingBar leftHandDegreeRatingBar;

        public SongViewHolder(@NonNull View songView) {
            super(songView);
            songNameTextView = songView.findViewById(R.id.song_name);
            songFavorImageView = songView.findViewById(R.id.song_favor);
            rightHandDegreeRatingBar = songView.findViewById(R.id.song_degree);
            leftHandDegreeRatingBar = songView.findViewById(R.id.song_degree2);
            timeTextView = songView.findViewById(R.id.ol_sound_time);
            rightHandDegreeTextView = songView.findViewById(R.id.nandu_1);
            leftHandDegreeTextView = songView.findViewById(R.id.nandu_2);

            songView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Song song = getItem(position);
                    if (song != null) {
                        PlaySongs.setSongFilePath(song.getFilePath());
                        OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
                        builder.setTune(olPlayRoom.getdiao());
                        builder.setSongPath(song.getFilePath().substring(6, song.getFilePath().length() - 3));
                        olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
                        Message obtainMessage = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
                    }
                }
            });
        }

        public void bindData(Song song) {
            songNameTextView.setText(song.getName());
            songNameTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            songNameTextView.setHorizontallyScrolling(true);

            if (song.isFavorite() == 0) {
                songFavorImageView.setImageResource(R.drawable.favor_1);
            } else {
                songFavorImageView.setImageResource(R.drawable.favor);
            }
            songFavorImageView.setOnClickListener(v -> {
                if (song.isFavorite() == 0) {
                    JPApplication.getSongDatabase().songDao().updateFavoriteSong(song.getFilePath(), 1);
                    songFavorImageView.setImageResource(R.drawable.favor);
                } else {
                    JPApplication.getSongDatabase().songDao().updateFavoriteSong(song.getFilePath(), 0);
                    songFavorImageView.setImageResource(R.drawable.favor_1);
                }
                DataSource.Factory<Integer, Song> favoriteSong = JPApplication.getSongDatabase().songDao().getFavoriteSongWithDataSource();
                PagedList<Song> songPageList = JPApplication.getSongDatabase().songDao().getPageListByDatasourceFactory(favoriteSong);
                olPlayRoom.getMutablePagedListLiveData().setValue(songPageList);
            });
            rightHandDegreeTextView.setText(" 右手:" + song.getRightHandDegree());
            leftHandDegreeTextView.setText(" 左手:" + song.getLeftHandDegree());
            timeTextView.setText((song.getLength() / 60 < 10 ? "0" : "") +
                    (song.getLength() / 60) + ":" + (song.getLength() % 60 < 10 ? "0" : "") + (song.getLength() % 60));
            rightHandDegreeRatingBar.setNumStars(5);
            rightHandDegreeRatingBar.setClickable(false);
            rightHandDegreeRatingBar.setRating(song.getRightHandDegree() / 2);
            leftHandDegreeRatingBar.setNumStars(5);
            leftHandDegreeRatingBar.setClickable(false);
            leftHandDegreeRatingBar.setRating(song.getLeftHandDegree() / 2);
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
