package ly.pp.justpiano3.task;

import android.media.midi.MidiDeviceInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import ly.pp.justpiano3.adapter.MidiDeviceListAdapter;
import ly.pp.justpiano3.utils.MidiDeviceUtil;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class MidiDeviceListPreferenceTask extends AsyncTask<Boolean, Void, String> {
    private final MidiDeviceListAdapter midiDeviceListAdapter;
    private final MidiDeviceInfo midiDeviceInfo;

    public MidiDeviceListPreferenceTask(MidiDeviceListAdapter midiDeviceListAdapter, MidiDeviceInfo midiDeviceInfo) {
        this.midiDeviceListAdapter = midiDeviceListAdapter;
        this.midiDeviceInfo = midiDeviceInfo;
    }

    @Override
    protected String doInBackground(Boolean... open) {
        if (open[0]) {
            MidiDeviceUtil.openDevice(midiDeviceListAdapter.midiDeviceListPreference.context, midiDeviceInfo);
            int retry = 0;
            while (retry < 5 && !MidiDeviceUtil.getAllConnectedMidiDeviceIdAndNameList().contains(
                    MidiDeviceUtil.getDeviceIdAndName(midiDeviceInfo))) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                retry++;
            }
            if (retry >= 5) {
                return "error";
            }
        } else {
            MidiDeviceUtil.closeDevice(midiDeviceListAdapter.midiDeviceListPreference.context, midiDeviceInfo);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        if (Objects.equals(str, "error")) {
            Toast.makeText(midiDeviceListAdapter.midiDeviceListPreference.context,
                    "MIDI设备手动连接失败，请确认设备物理连接是否正常", Toast.LENGTH_SHORT).show();
        }
        midiDeviceListAdapter.notifyDataSetChanged();
        midiDeviceListAdapter.midiDeviceListPreference.jpProgressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        midiDeviceListAdapter.midiDeviceListPreference.jpProgressBar.show();
    }
}
