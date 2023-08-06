package ly.pp.justpiano3.entity;

/**
 * 音符 缓存在队列用于传输
 */
public class OLNote {

    private long absoluteTime;

    private int pitch;

    private int volume;

    public OLNote(long absoluteTime, int pitch, int volume) {
        this.absoluteTime = absoluteTime;
        this.pitch = pitch;
        this.volume = volume;
    }

    public long getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
