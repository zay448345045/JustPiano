package ly.pp.justpiano3.task;

import android.media.midi.MidiDeviceInfo;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ly.pp.justpiano3.adapter.MidiDeviceListAdapter;
import ly.pp.justpiano3.utils.MidiDeviceUtil;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class MidiDeviceListPreferenceTask extends AsyncTask<Boolean, Void, Void> {
    private final MidiDeviceListAdapter midiDeviceListAdapter;
    private final MidiDeviceInfo midiDeviceInfo;

    public MidiDeviceListPreferenceTask(MidiDeviceListAdapter midiDeviceListAdapter, MidiDeviceInfo midiDeviceInfo) {
        this.midiDeviceListAdapter = midiDeviceListAdapter;
        this.midiDeviceInfo = midiDeviceInfo;
    }

    @Override
    protected Void doInBackground(Boolean... open) {
        if (open[0]) {
            MidiDeviceUtil.openDevice(midiDeviceListAdapter.context, midiDeviceInfo);
            int retry = 0;
            while (retry < 3 && !MidiDeviceUtil.getAllConnectedMidiDeviceIdAndNameList().contains(
                    MidiDeviceUtil.getDeviceIdAndName(midiDeviceInfo))) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                retry++;
            }
        } else {
            MidiDeviceUtil.closeDevice(midiDeviceListAdapter.context, midiDeviceInfo);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        midiDeviceListAdapter.notifyDataSetChanged();
        midiDeviceListAdapter.midiDeviceListPreference.jpProgressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        midiDeviceListAdapter.midiDeviceListPreference.jpProgressBar.show();
    }
}
