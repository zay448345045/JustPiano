package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.midi.MidiDeviceInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.task.MidiDeviceListPreferenceTask;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.view.MidiDeviceListPreference;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class MidiDeviceListAdapter extends BaseAdapter {
    public final MidiDeviceListPreference midiDeviceListPreference;
    public Context context;
    private final MidiDeviceInfo[] midiDeviceInfoList;
    private final String[] midiDeviceNameList;

    public MidiDeviceListAdapter(MidiDeviceListPreference midiDeviceListPreference, Context context,
                                 String[] midiDeviceNameList, MidiDeviceInfo[] midiDeviceInfoList) {
        this.midiDeviceListPreference = midiDeviceListPreference;
        this.context = context;
        this.midiDeviceNameList = midiDeviceNameList;
        this.midiDeviceInfoList = midiDeviceInfoList;
    }

    @Override
    public int getCount() {
        return midiDeviceNameList.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ol_skin_view, null);
        }
        ((TextView) view.findViewById(R.id.skin_name_view)).setText(midiDeviceNameList[i]);
        Button operateButton = view.findViewById(R.id.skin_dele);
        operateButton.setTag(midiDeviceInfoList[i]);
        Button enableText = view.findViewById(R.id.set_skin);
        enableText.setBackgroundResource(R.drawable._none);
        if (MidiDeviceUtil.getAllConnectedMidiDeviceIdAndNameList().contains(
                MidiDeviceUtil.getDeviceIdAndName(midiDeviceInfoList[i]))) {
            enableText.setText("开启中");
            enableText.setTextColor(Color.RED);
            operateButton.setText("断开");
            operateButton.setOnClickListener(view1 -> new MidiDeviceListPreferenceTask(
                    this, midiDeviceInfoList[i]).execute(false));
        } else {
            enableText.setText("已禁用");
            enableText.setTextColor(Color.BLACK);
            operateButton.setText("连接");
            operateButton.setOnClickListener(view1 -> new MidiDeviceListPreferenceTask(
                    this, midiDeviceInfoList[i]).execute(true));
        }
        return view;
    }
}
