package ly.pp.justpiano3.utils;

import ly.pp.justpiano3.entity.OriginalNote;
import ly.pp.justpiano3.entity.SongData;
import ly.pp.justpiano3.midi.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 曲谱pm文件操作相关工具类
 *
 * @author as2134u
 * @since 2021-07-08
 */
public class SongUtil {

    /**
     * 决定是否为隐藏键音符的临界间隔时间，目前固定为100，单位毫秒
     */
    public static final int HIDE_NOTE_TIME_CRITICAL = 100;

    /**
     * pm文件全局速度，目前固定值为5
     */
    public static final int PM_GLOBAL_SPEED = 5;

    /**
     * pm时间间隔过长时，填充的空音符的间隔时间，目前固定为100
     */
    public static final int PM_DEFAULT_FILLED_INTERVAL = 100;

    /**
     * pm时间间隔过长时，填充的空音符数组
     */
    public static final byte[] PM_DEFAULT_FILLED_DATA = new byte[]{PM_DEFAULT_FILLED_INTERVAL, 1, 110, 3};

    /**
     * 在正式曲谱文件夹里根据分类和曲谱id获取pm曲谱文件名
     *
     * @param items 曲谱分类
     * @param id    曲谱id
     * @return 文件名
     */
    public static String getPmSongFileName(String items, Integer id) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumIntegerDigits(6);
        nf.setMinimumIntegerDigits(6);
        return items + nf.format(id) + ".pm";
    }

    /**
     * midi文件转pm文件
     *
     * @param midiFile midi文件
     * @param pmFile pm文件转换结果
     * @return 曲谱信息
     */
    public static SongData midiFileToPmFile(File midiFile, File pmFile) throws Exception {
        List<OriginalNote> originalNoteList = parseMidiOriginalNote(midiFile);
        Collections.sort(originalNoteList, (note1, note2) -> Long.compare(note1.getPlayTime(), note2.getPlayTime()));
        // 获取左手和右手的音符列表
        List<OriginalNote> leftHandNoteList = new ArrayList<>();
        List<OriginalNote> rightHandNoteList = new ArrayList<>();
        for (OriginalNote originalNote : originalNoteList) {
            if (originalNote.getLeftHand()) {
                leftHandNoteList.add(originalNote);
            } else {
                rightHandNoteList.add(originalNote);
            }
        }
        SongData songData = new SongData();
        songData.setLength(calculateSongLength(originalNoteList));
        songData.setDegree(calculateDegree(rightHandNoteList));
        songData.setLeftDegree(calculateDegree(leftHandNoteList));
        songData.setFullScore(calculateFullScore(rightHandNoteList));
        songData.setLeftFullScore(calculateFullScore(leftHandNoteList));
        songData.setFullAllScore(calculateFullAllScore(rightHandNoteList));
        songData.setLeftFullAllScore(calculateFullAllScore(leftHandNoteList));
        songData.setMidiFile(midiFile);
        String songName = midiFile.getName().substring(0, midiFile.getName().indexOf('.'));
        songData.setPmFile(createPmFile(pmFile, songName, songData, originalNoteList));
        return songData;
    }

    /**
     * 转换pm，生成临时pm文件
     */
    private static File createPmFile(File pmFile, String songName, SongData songData, List<OriginalNote> originalNoteList) throws Exception {
        if (pmFile.exists()) {
            pmFile.delete();
        }
        pmFile.createNewFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(pmFile)) {
            fileOutputStream.write(songName.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write('\n');
            fileOutputStream.write(songData.getLeftDegree());
            // 记录上一个音符的起始播放时间，用于计算时间间隔
            long lastNotePlayTime = 0L;
            for (OriginalNote originalNote : originalNoteList) {
                // 实际的音符时间间隔 = 当前音符的起始播放时间 - 上一个音符的起始播放时间
                int intervalTime = (int) (originalNote.getPlayTime() - lastNotePlayTime);
                // 若时间间隔过高，防止byte字节溢出，则需进行拆解（先循环填充空音符的时间间隔，之后填充剩余的时间间隔）
                while (intervalTime > PM_DEFAULT_FILLED_INTERVAL * PM_GLOBAL_SPEED) {
                    fileOutputStream.write(PM_DEFAULT_FILLED_DATA);
                    intervalTime -= PM_DEFAULT_FILLED_INTERVAL * PM_GLOBAL_SPEED;
                }
                // 写入pm音符数据，元素依次为：时间间隔/全局速度、左右手、音高、力度
                fileOutputStream.write(intervalTime / PM_GLOBAL_SPEED);
                fileOutputStream.write(originalNote.getLeftHand() ? 1 : 0);
                fileOutputStream.write(originalNote.getPitch());
                fileOutputStream.write((byte) (Math.ceil(originalNote.getVolume() * 100d / 128)));
                // 更新上一个音符的起始播放时间
                lastNotePlayTime = originalNote.getPlayTime();
            }
            fileOutputStream.write(PM_GLOBAL_SPEED);
            fileOutputStream.write(songData.getDegree());
            fileOutputStream.flush();
        }
        return pmFile;
    }

    /**
     * 解析midi，输出原始音符列表
     */
    private static List<OriginalNote> parseMidiOriginalNote(File midiFile) throws Exception {
        List<OriginalNote> originalNoteList = new ArrayList<>();
        // midi文件转化为midi序列，相当于解析了
        Sequence sequence = new StandardMidiFileReader().getSequence(midiFile);
        // 这个缓存就在计算midi中所有的速度变化事件，计算好了之后缓存下来，传入，就不用每次都计算了，提性能用的
        MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache(sequence);
        // 提取midi的所有音轨，过滤掉无音符的音轨
        List<Track> filteredTracks = new ArrayList<>();
        for (int i = 0; i < sequence.getTracks().length; i++) {
            Track track = sequence.getTracks()[i];
            if (SongUtil.hasNote(track)) {
                filteredTracks.add(track);
            }
        }
        Track[] tracks = filteredTracks.toArray(new Track[filteredTracks.size()]);
        for (int i = 0; i < tracks.length; i++) {
            for (int j = 0; j < tracks[i].size(); j++) {
                MidiEvent event = tracks[i].get(j);
                // 如果事件为ShortMessage且为音符按下的事件
                if (event.getMessage() instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) event.getMessage();
                    if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                        // 取出音符的音高和力度，力度大于0一定是音符按下了
                        // 力度等于0时，比如museScore导出的midi，力度等于0其实是音符抬起的意思，虽然它不是ShortMessage.NOTE_OFF类型
                        // 其他一些软件导出的mid，许多都是按照midi标准来的，ShortMessage.NOTE_OFF类型表示音符抬起
                        int note = shortMessage.getData1();
                        int velocity = shortMessage.getData2();
                        if (velocity > 0) {
                            // tick换算为实际的时间，看JDK源码得知，考虑到了变速，按tick划分变速，计算没有问题
                            long time = MidiUtils.tick2microsecond(sequence, event.getTick(), tempoCache);
                            originalNoteList.add(new OriginalNote(time / 1000, i > 0, (byte) note, (byte) velocity));
                        }
                    }
                }
            }
        }
        return originalNoteList;
    }

    /**
     * 判断音轨中是否有音符
     */
    private static boolean hasNote(Track track) {
        if (track == null || track.size() == 0) {
            return false;
        }
        int trackSize = track.size();
        for (int i = 0; i < trackSize; i++) {
            MidiMessage message = track.get(i).getMessage();
            if (message instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) message;
                if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算曲谱时长，单位秒
     */
    private static int calculateSongLength(List<OriginalNote> originalNoteList) {
        long maxPlayTime = 0L;
        for (OriginalNote note : originalNoteList) {
            long playTime = note.getPlayTime();
            if (playTime > maxPlayTime) {
                maxPlayTime = playTime;
            }
        }
        return Math.round(maxPlayTime / 1000f);
    }

    /**
     * 给定音符列表，计算难度（客户端显示难度 * 10，返回参数范围在0～100之间）
     */
    private static int calculateDegree(List<OriginalNote> originalNoteList) {
        if (originalNoteList == null || originalNoteList.isEmpty()) {
            return 0;
        }
        // 非隐藏键音块数量
        int showingNoteCount = 1;
        // 每个非隐藏键音符的难度累加，音符的难度 = 1 / 相邻两个音符的时间间隔
        float allNoteDegree = 0f;
        // 临时变量，用于记录每一个非隐藏音块的开始时间
        Long lastShowingNoteStartTime = null;
        for (int i = 0; i < originalNoteList.size() - 1; i++) {
            if (lastShowingNoteStartTime == null) {
                lastShowingNoteStartTime = originalNoteList.get(i).getPlayTime();
            }
            long noteTimeInterval = originalNoteList.get(i + 1).getPlayTime() - originalNoteList.get(i).getPlayTime();
            // 时间差大于隐藏键的临界值，执行计算非隐藏键音符的难度
            if (noteTimeInterval >= HIDE_NOTE_TIME_CRITICAL) {
                // 每个非隐藏键音符的难度 = 两个非隐藏键音符的持续时间间隔的倒数
                allNoteDegree += 1d / (originalNoteList.get(i + 1).getPlayTime() - lastShowingNoteStartTime);
                lastShowingNoteStartTime = null;
                showingNoteCount++;
            }
        }
        // 曲谱难度 = 1000 * 所有非隐藏键音符的难度之和 / 非隐藏键音符数量
        return Math.round(10 * 1000f * allNoteDegree / showingNoteCount);
    }

    /**
     * 计算非隐藏键音符的满分
     */
    private static int calculateFullScore(List<OriginalNote> originalNoteList) {
        if (originalNoteList == null || originalNoteList.isEmpty()) {
            return 0;
        }
        // 非隐藏键音块数量
        int showingNoteCount = 1;
        for (int i = 0; i < originalNoteList.size() - 1; i++) {
            long noteTimeInterval = originalNoteList.get(i + 1).getPlayTime() - originalNoteList.get(i).getPlayTime();
            if (noteTimeInterval >= HIDE_NOTE_TIME_CRITICAL) {
                showingNoteCount++;
            }
        }
        return calculateScoreByNoteCount(showingNoteCount);
    }

    /**
     * 计算所有（含隐藏键）音符的满分
     */
    private static int calculateFullAllScore(List<OriginalNote> originalNoteList) {
        if (originalNoteList == null || originalNoteList.isEmpty()) {
            return 0;
        }
        // 可弹奏（含隐藏）音块数量
        int playableNoteCount = 1;
        // 临时变量，用于记录每一个非隐藏音块的音高
        Byte lastShowingNotePitch = null;
        // 临时变量，为lastShowingNotePitch变量所对应的音块的前一个非隐藏音块的音高，仅仅做八度特殊比较用
        byte preLastShowingNotePitch = 127;
        for (int i = 0; i < originalNoteList.size() - 1; i++) {
            if (lastShowingNotePitch == null) {
                lastShowingNotePitch = originalNoteList.get(i).getPitch();
            }
            long noteTimeInterval = originalNoteList.get(i + 1).getPlayTime() - originalNoteList.get(i).getPlayTime();
            if (noteTimeInterval >= HIDE_NOTE_TIME_CRITICAL) {
                // 有点像滑动窗口，把非隐藏音块的音高存储前置一下
                preLastShowingNotePitch = lastShowingNotePitch;
                lastShowingNotePitch = null;
                playableNoteCount++;
            } else if (noteTimeInterval > 0) {
                byte currentNotePitch = originalNoteList.get(i + 1).getPitch();
                // 处理音符的间隔时间小于隐藏键的临界值，且大于0的情况，只要当前音块和它前一个非隐藏音块落在同一个屏幕音高区域，当前(隐藏键)音块就可以按到并得分
                // 在客户端弹奏部分逻辑重写之前，为保证详细逻辑不复杂，就先只算个大概，具体来说，有以下情况：
                // 1、前一个非隐藏音块和当前音块在同一个八度上，当前的(隐藏)音块即可按到并得分
                // 2、前一个非隐藏音块没有落在屏幕最右侧的"C"键，并且当前音块落在了屏幕最右侧的"C"键(就是高了一个八度的"C"键)
                // 2.1、子问题：前一个非隐藏音块是"C"键的时候，如何确定是屏幕最左侧的"C"键还是屏幕最右侧的"C"键？
                //      这需要看前前一个非隐藏音块的音高，如果比前一个非隐藏音块的音高大，则就是屏幕最左侧的"C"键，实际更复杂，这样大概算算好了
                // 3、前一个非隐藏音块落在了屏幕最右侧的"C"键，并且当前音块落在了低一个八度的所有区域
                if (lastShowingNotePitch / 12 == currentNotePitch / 12
                        || (lastShowingNotePitch % 12 != 0 && lastShowingNotePitch / 12 * 12 + 12 == currentNotePitch)
                        || (lastShowingNotePitch % 12 == 0 && preLastShowingNotePitch > lastShowingNotePitch && lastShowingNotePitch + 12 == currentNotePitch)
                        || (lastShowingNotePitch % 12 == 0 && preLastShowingNotePitch < lastShowingNotePitch && lastShowingNotePitch / 12 == currentNotePitch / 12 + 1)) {
                    playableNoteCount++;
                }
            }
        }
        return calculateScoreByNoteCount(playableNoteCount);
    }

    /**
     * 根据音符个数计算满分
     */
    private static int calculateScoreByNoteCount(int noteCount) {
        // 音符个数小于10时：等差数列求和，例：共7个音符，满分为 10 + 11 + 12 + 13 + 14 + 15 + 16 = 91
        // 音符个数大于10时，每个音符均为20分，例：共n个音符，满分为 10 + 11 + ... + 20 + 20 * (n - 10)
        return noteCount < 10 ? noteCount * (19 + noteCount) / 2 : noteCount * 20 - 55;
    }
}
