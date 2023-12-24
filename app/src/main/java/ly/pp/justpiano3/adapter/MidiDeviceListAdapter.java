package ly.pp.justpiano3.adapter;

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
    private final MidiDeviceInfo[] midiDeviceInfoList;
    private final String[] midiDeviceNameList;

    public MidiDeviceListAdapter(MidiDeviceListPreference midiDeviceListPreference,
                                 String[] midiDeviceNameList, MidiDeviceInfo[] midiDeviceInfoList) {
        this.midiDeviceListPreference = midiDeviceListPreference;
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
            view = LayoutInflater.from(midiDeviceListPreference.context).inflate(R.layout.ol_skin_view, null);
        }
        ((TextView) view.findViewById(R.id.skin_name_view)).setText(midiDeviceNameList[i]);
        Button enableText = view.findViewById(R.id.set);
        enableText.setBackgroundResource(R.drawable._none);
        Button operateButton = view.findViewById(R.id.skin_dele);
        if (midiDeviceInfoList[i] == null) {
            operateButton.setVisibility(View.INVISIBLE);
            enableText.setText("");
        } else {
            operateButton.setTag(midiDeviceInfoList[i]);
            if (MidiDeviceUtil.getAllConnectedMidiDeviceIdAndNameList().contains(
                    MidiDeviceUtil.getDeviceIdAndName(midiDeviceInfoList[i]))) {
                enableText.setText("已启用");
                enableText.setTextColor(Color.RED);
                operateButton.setText("断开");
                operateButton.setBackgroundResource(R.drawable.selector_ol_button_login);
                operateButton.setOnClickListener(view1 -> new MidiDeviceListPreferenceTask(
                        this, midiDeviceInfoList[i]).execute(false));
            } else {
                enableText.setText("已禁用");
                enableText.setTextColor(Color.BLACK);
                operateButton.setText("连接");
                operateButton.setBackgroundResource(R.drawable.selector_ol_button);
                operateButton.setOnClickListener(view1 -> new MidiDeviceListPreferenceTask(
                        this, midiDeviceInfoList[i]).execute(true));
            }
        }
        return view;
    }
}
