package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.KeyboardModeView;
import ly.pp.justpiano3.view.WaterfallView;
import ly.pp.justpiano3.view.play.PmFileParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaterfallActivity extends Activity {

    /**
     * 瀑布流view
     */
    private WaterfallView waterfallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterfall);
        Bundle bundle = getIntent().getExtras();
        String songPath = bundle.getString("songPath");
        PmFileParser pmFileParser = new PmFileParser(this, songPath);
        TextView songNameView = findViewById(R.id.waterfall_song_name);
        songNameView.setText(pmFileParser.getSongName());
        KeyboardModeView keyboardModeView = findViewById(R.id.waterfall_keyboard);
        waterfallView = findViewById(R.id.waterfall_view);
        waterfallView.setNoteFallListener(new WaterfallView.NoteFallListener() {
            @Override
            public void onNoteFallDown(WaterfallNote waterfallNote) {
                SoundEngineUtil.playSound(waterfallNote.getPitch(), waterfallNote.getVolume());
                keyboardModeView.fireKeyDown(waterfallNote.getPitch(), waterfallNote.getVolume(), waterfallNote.isLeftHand() ? 14 : 1, false);
            }

            @Override
            public void onNoteLeave(WaterfallNote waterfallNote) {
                keyboardModeView.fireKeyUp(waterfallNote.getPitch(), false);
            }
        });
        // 监听键盘view布局完成，布局完成后，瀑布流即可生成并开始
        keyboardModeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                WaterfallNote[] waterfallNotes = convertToWaterfallNote(pmFileParser, keyboardModeView);
                waterfallView.startPlay(waterfallNotes);

                // 移除监听，避免重复调用
                keyboardModeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        waterfallView.stopPlay();
        waterfallView = null;
    }

    private WaterfallNote[] convertToWaterfallNote(PmFileParser pmFileParser, KeyboardModeView keyboardModeView) {
        // 分别处理左右手的音符list，以便寻找每条音轨的上一个音符，插入结束时间
        List<WaterfallNote> leftHandWaterfallNoteList = new ArrayList<>();
        List<WaterfallNote> rightHandWaterfallNoteList = new ArrayList<>();
        int totalTime = 0;
        for (int i = 0; i < pmFileParser.getNoteArray().length; i++) {
            int pitch = pmFileParser.getNoteArray()[i];
            totalTime += pmFileParser.getTickArray()[i] * pmFileParser.getPm_2();
            boolean leftHand = pmFileParser.getTrackArray()[i] != 0;
            RectF rectF = WaterfallView.convertWidthToWaterfallWidth(KeyboardModeView.isBlackKey(pitch),
                    keyboardModeView.convertPitchToReact(pitch));
            if (rectF != null) {
                WaterfallNote waterfallNote = new WaterfallNote()
                        .setLeft(rectF.left)
                        .setRight(rectF.right)
                        .setStartTime(totalTime)
                        .setVolume(pmFileParser.getVolumeArray()[i])
                        .setPitch(pitch)
                        .setLeftHand(leftHand);
                // 根据左右手拿到对应的list
                List<WaterfallNote> waterfallNoteListByHand = leftHand ? leftHandWaterfallNoteList : rightHandWaterfallNoteList;
                // 取list中上一个元素（音符），填充它的结束时间为当前音符的（累计）开始时间
                if (!waterfallNoteListByHand.isEmpty()) {
                    WaterfallNote lastNote = waterfallNoteListByHand.get(waterfallNoteListByHand.size() - 1);
                    lastNote.setEndTime(totalTime);
                }
                waterfallNoteListByHand.add(waterfallNote);
            }
        }
        // 合并左右手两条音轨的音符list，并按音符的开始时间排序
        List<WaterfallNote> waterfallNoteList = new ArrayList<>();
        waterfallNoteList.addAll(leftHandWaterfallNoteList);
        waterfallNoteList.addAll(rightHandWaterfallNoteList);
        Collections.sort(waterfallNoteList, (o1, o2) -> Integer.compare(o1.getStartTime(), o2.getStartTime()));
        // 检验排序后的所有音符，如果音符的持续时间过短或过长，进行范围调整
        for (WaterfallNote waterfallNote : waterfallNoteList) {
            // 结束时间为0的说明是当前音轨的最后一个音符，还没来得及写入结束时间，直接给一个最大值
            if (waterfallNote.getEndTime() == 0) {
                waterfallNote.setEndTime(waterfallNote.getStartTime() + WaterfallView.MAX_INTERVAL_TIME);
            } else if (waterfallNote.interval() < WaterfallView.MIN_INTERVAL_TIME) {
                waterfallNote.setEndTime(waterfallNote.getStartTime() + WaterfallView.MIN_INTERVAL_TIME);
            } else if (waterfallNote.interval() > WaterfallView.MAX_INTERVAL_TIME) {
                waterfallNote.setEndTime(waterfallNote.getStartTime() + WaterfallView.MAX_INTERVAL_TIME);
            }
        }
        return waterfallNoteList.toArray(new WaterfallNote[0]);
    }
}