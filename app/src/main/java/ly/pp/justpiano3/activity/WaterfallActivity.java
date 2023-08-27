package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import androidx.core.util.Pair;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.KeyboardModeView;
import ly.pp.justpiano3.view.WaterfallView;
import ly.pp.justpiano3.view.play.PmFileParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaterfallActivity extends Activity implements View.OnTouchListener {

    /**
     * 瀑布的宽度占键盘黑键宽度的百分比
     */
    public static final float BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.8f;

    /**
     * 瀑布流音符播放时的最长的持续时间
     */
    public static final int NOTE_PLAY_MAX_INTERVAL_TIME = 1200;

    /**
     * 瀑布流view
     */
    private WaterfallView waterfallView;

    /**
     * 钢琴键盘view
     */
    private KeyboardModeView keyboardModeView;

    /**
     * 用于播放动画
     */
    private ScheduledExecutorService scheduledExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterfall);
        // 从extras中的数据确定曲目，解析pm文件
        PmFileParser pmFileParser = parsePmFileFromIntentExtras();
        TextView songNameView = findViewById(R.id.waterfall_song_name);
        songNameView.setText(pmFileParser.getSongName());
        waterfallView = findViewById(R.id.waterfall_view);
        // 瀑布流设置监听某个瀑布音符到达屏幕底部或完全离开屏幕底部时的动作
        waterfallView.setNoteFallListener(new WaterfallView.NoteFallListener() {
            @Override
            public void onNoteFallDown(WaterfallNote waterfallNote) {
                // 瀑布流音符到达屏幕的底部，播放声音并触发键盘view的琴键按压效果
                SoundEngineUtil.playSound(waterfallNote.getPitch(), waterfallNote.getVolume());
                keyboardModeView.fireKeyDown(waterfallNote.getPitch(), waterfallNote.getVolume(), waterfallNote.isLeftHand() ? 14 : 1, false);
            }

            @Override
            public void onNoteLeave(WaterfallNote waterfallNote) {
                // 瀑布流音符完全离开屏幕，触发键盘view的琴键抬起效果
                keyboardModeView.fireKeyUp(waterfallNote.getPitch(), false);
            }
        });
        keyboardModeView = findViewById(R.id.waterfall_keyboard);
        // 监听键盘view布局完成，布局完成后，瀑布流即可生成并开始
        keyboardModeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 将pm文件的解析结果转换为瀑布流音符数组，传入view后开始瀑布流绘制
                WaterfallNote[] waterfallNotes = convertToWaterfallNote(pmFileParser, keyboardModeView);
                waterfallView.startPlay(waterfallNotes);
                // 开启增减白键数量、移动键盘按钮的监听
                findViewById(R.id.waterfall_sub_key).setOnTouchListener(WaterfallActivity.this);
                findViewById(R.id.waterfall_add_key).setOnTouchListener(WaterfallActivity.this);
                findViewById(R.id.waterfall_key_move_left).setOnTouchListener(WaterfallActivity.this);
                findViewById(R.id.waterfall_key_move_right).setOnTouchListener(WaterfallActivity.this);
                // 移除布局监听，避免重复调用
                keyboardModeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 重新确定瀑布流音符长条的左侧和右侧的坐标值
     */
    private void updateWaterfallNoteLeftRightLocation(WaterfallNote[] waterfallNotes, KeyboardModeView keyboardModeView) {
        for (WaterfallNote waterfallNote : waterfallNotes) {
            Pair<Float, Float> noteLeftRightLocation = convertWidthToWaterfallWidth(KeyboardModeView.isBlackKey(waterfallNote.getPitch()),
                    keyboardModeView.convertPitchToReact(waterfallNote.getPitch()));
            if (noteLeftRightLocation != null) {
                waterfallNote.setLeft(noteLeftRightLocation.first);
                waterfallNote.setRight(noteLeftRightLocation.second);
            }
        }
    }

    @NotNull
    private PmFileParser parsePmFileFromIntentExtras() {
        PmFileParser pmFileParser;
        String songPath = getIntent().getExtras().getString("songPath");
        if (StringUtil.isNullOrEmpty(songPath)) {
            byte[] songBytes = getIntent().getExtras().getByteArray("songBytes");
            pmFileParser = new PmFileParser(songBytes);
        } else {
            pmFileParser = new PmFileParser(this, songPath);
        }
        return pmFileParser;
    }

    @Override
    protected void onStop() {
        if (waterfallView.isPlaying()) {
            waterfallView.pausePlay();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        if (!waterfallView.isPlaying()) {
            waterfallView.resumePlay();
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if (!waterfallView.isPlaying()) {
            waterfallView.resumePlay();
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        waterfallView.destroy();
        waterfallView = null;
        super.onDestroy();
    }

    /**
     * pm文件解析结果转换为瀑布流音符
     */
    private WaterfallNote[] convertToWaterfallNote(PmFileParser pmFileParser, KeyboardModeView keyboardModeView) {
        // 分别处理左右手的音符list，以便寻找每条音轨的上一个音符，插入结束时间
        List<WaterfallNote> leftHandWaterfallNoteList = new ArrayList<>();
        List<WaterfallNote> rightHandWaterfallNoteList = new ArrayList<>();
        int totalTime = 0;
        // 取pm文件中的解析音符内容，进行瀑布流音符的生成
        for (int i = 0; i < pmFileParser.getNoteArray().length; i++) {
            int pitch = pmFileParser.getNoteArray()[i];
            // 计算音符播放的累计时间
            totalTime += pmFileParser.getTickArray()[i] * pmFileParser.getPm_2();
            boolean leftHand = pmFileParser.getTrackArray()[i] != 0;
            // 确定瀑布流音符长条的左侧和右侧的坐标值，根据钢琴键盘view中的琴键获取横坐标
            Pair<Float, Float> noteLeftRightLocation = convertWidthToWaterfallWidth(KeyboardModeView.isBlackKey(pitch),
                    keyboardModeView.convertPitchToReact(pitch));
            if (noteLeftRightLocation != null) {
                WaterfallNote waterfallNote = new WaterfallNote()
                        .setLeft(noteLeftRightLocation.first)
                        .setRight(noteLeftRightLocation.second)
                        .setStartTime(totalTime)
                        .setVolume(pmFileParser.getVolumeArray()[i])
                        .setPitch(pitch)
                        .setLeftHand(leftHand);
                // 根据左右手拿到对应的list
                List<WaterfallNote> waterfallNoteListByHand = leftHand ? leftHandWaterfallNoteList : rightHandWaterfallNoteList;
                // 取list中上一个元素（音符），填充它的结束时间为当前音符的（累计）开始时间
                if (!waterfallNoteListByHand.isEmpty()) {
                    // 循环向前寻找之前的音符
                    int index = waterfallNoteListByHand.size() - 1;
                    do {
                        WaterfallNote lastWaterfallNote = waterfallNoteListByHand.get(index);
                        // 设置上一个音符的结束时间
                        lastWaterfallNote.setEndTime(Math.max(lastWaterfallNote.getStartTime(), totalTime));
                        index--;
                        // 如果上一个音符的开始时间和当前音符的开始时间相同，则表示同时按下，此时循环，继续设定两个音符的结束时间相同即可
                    } while (index >= 0 && waterfallNoteListByHand.get(index).getStartTime() == waterfallNoteListByHand.get(index + 1).getStartTime());
                }
                waterfallNoteListByHand.add(waterfallNote);
            }
        }
        // 左右手音符列表合并，做后置处理
        return waterfallNoteListAfterHandle(leftHandWaterfallNoteList, rightHandWaterfallNoteList);
    }

    /**
     * 音符list后置处理，订正一些时间间隔情况，最后转为数组
     * <p>
     * 1、合并左右手两条音轨的音符list，处理最后一个音符间隔没有写进去的情况，直接写入间隔最大值
     * 2、过滤掉间隔为0的音符，按音符的起始时间进行排序
     * 3、处理音符间隔太大的情况，订正间隔为最大值
     * 4、所有音符乘以节拍比率数值，整体控制速度
     * 5、转为数组返回
     */
    private WaterfallNote[] waterfallNoteListAfterHandle(List<WaterfallNote> leftHandWaterfallNoteList, List<WaterfallNote> rightHandWaterfallNoteList) {
        List<WaterfallNote> waterfallNoteList = new ArrayList<>();
        for (WaterfallNote waterfallNote : leftHandWaterfallNoteList) {
            // 每个音轨的最后一个音符，按之前的逻辑，没有填充结束时间，这里直接填充最后一个音符的结束时间 = 起始时间 + 持续时间最大值
            if (waterfallNote.getEndTime() == 0) {
                waterfallNote.setEndTime(waterfallNote.getStartTime() + NOTE_PLAY_MAX_INTERVAL_TIME);
            }
            if (waterfallNote.interval() > 0) {
                waterfallNoteList.add(waterfallNote);
            }
        }
        for (WaterfallNote waterfallNote : rightHandWaterfallNoteList) {
            if (waterfallNote.getEndTime() == 0) {
                waterfallNote.setEndTime(waterfallNote.getStartTime() + NOTE_PLAY_MAX_INTERVAL_TIME);
            }
            if (waterfallNote.interval() > 0) {
                waterfallNoteList.add(waterfallNote);
            }
        }
        Collections.sort(waterfallNoteList, (o1, o2) -> Integer.compare(o1.getStartTime(), o2.getStartTime()));
        JPApplication jpApplication = (JPApplication) getApplication();
        for (WaterfallNote currentWaterfallNote : waterfallNoteList) {
            if (currentWaterfallNote.interval() > NOTE_PLAY_MAX_INTERVAL_TIME) {
                currentWaterfallNote.setEndTime(currentWaterfallNote.getStartTime() + NOTE_PLAY_MAX_INTERVAL_TIME);
            }
            currentWaterfallNote.setStartTime((int) (currentWaterfallNote.getStartTime() / jpApplication.getSetting().getTempSpeed()));
            currentWaterfallNote.setEndTime((int) (currentWaterfallNote.getEndTime() / jpApplication.getSetting().getTempSpeed()));
        }
        return waterfallNoteList.toArray(new WaterfallNote[0]);
    }

    /**
     * 将琴键RectF的宽度，转换成瀑布流长条的宽度（略小于琴键的宽度）
     * 返回值为瀑布流音符横坐标的左边界和右边界
     */
    private Pair<Float, Float> convertWidthToWaterfallWidth(boolean isBlack, RectF rectF) {
        if (rectF == null) {
            return null;
        }
        // 根据比例计算瀑布流的宽度
        float waterfallWidth = isBlack ? rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR
                : rectF.width() * KeyboardModeView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR;
        // 建立新的坐标，返回
        return Pair.create(rectF.centerX() - waterfallWidth / 2, rectF.centerX() + waterfallWidth / 2);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        int id = view.getId();
        if (action == MotionEvent.ACTION_DOWN) {
            view.setPressed(true);
            updateAddOrSubtract(id);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            view.setPressed(false);
            stopAddOrSubtract();
        }
        return true;
    }

    /**
     * 处理按钮长按，定时发送消息
     */
    private void updateAddOrSubtract(int viewId) {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = Message.obtain(handler);
            msg.what = viewId;
            handler.sendMessage(msg);
        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止定时发送消息
     */
    private void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    /**
     * 处理按钮长按的消息
     */
    private final Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case R.id.waterfall_sub_key:
                int keyboard1WhiteKeyNum = keyboardModeView.getWhiteKeyNum() - 1;
                keyboardModeView.setWhiteKeyNum(keyboard1WhiteKeyNum, 0);
                updateWaterfallNoteLeftRightLocation(waterfallView.getWaterfallNotes(), keyboardModeView);
                break;
            case R.id.waterfall_add_key:
                keyboard1WhiteKeyNum = keyboardModeView.getWhiteKeyNum() + 1;
                keyboardModeView.setWhiteKeyNum(keyboard1WhiteKeyNum, 0);
                updateWaterfallNoteLeftRightLocation(waterfallView.getWaterfallNotes(), keyboardModeView);
                break;
            case R.id.waterfall_key_move_left:
                int keyboard1WhiteKeyOffset = keyboardModeView.getWhiteKeyOffset() - 1;
                keyboardModeView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, 0);
                updateWaterfallNoteLeftRightLocation(waterfallView.getWaterfallNotes(), keyboardModeView);
                break;
            case R.id.waterfall_key_move_right:
                keyboard1WhiteKeyOffset = keyboardModeView.getWhiteKeyOffset() + 1;
                keyboardModeView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, 0);
                updateWaterfallNoteLeftRightLocation(waterfallView.getWaterfallNotes(), keyboardModeView);
                break;
            default:
                break;
        }
        return false;
    });
}