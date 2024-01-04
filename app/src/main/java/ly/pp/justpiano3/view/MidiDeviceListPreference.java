package ly.pp.justpiano3.view;

import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.MidiDeviceListAdapter;
import ly.pp.justpiano3.utils.MidiDeviceUtil;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class MidiDeviceListPreference extends DialogPreference {
    public JPProgressBar jpProgressBar;
    private MidiDeviceInfo[] midiDeviceInfoList;
    private String[] midiDeviceNameList;
    private MidiDeviceListAdapter midiDeviceListAdapter;

    public MidiDeviceListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MidiDeviceListPreference(Context context) {
        super(context, null);
    }

    private void loadMidiDeviceList() {
        List<MidiDeviceInfo> allConnectedMidiDeviceList = MidiDeviceUtil.getAllMidiDeviceList();
        int size = allConnectedMidiDeviceList.size();
        midiDeviceNameList = new String[size == 0 ? 1 : size];
        midiDeviceInfoList = new MidiDeviceInfo[size == 0 ? 1 : size];
        if (size == 0) {
            midiDeviceNameList[0] = "您当前没有连接任何MIDI设备";
        }
        for (int i = 0; i < size; i++) {
            MidiDeviceInfo deviceInfo = allConnectedMidiDeviceList.get(i);
            String deviceIdAndName = MidiDeviceUtil.getDeviceIdAndName(deviceInfo);
            midiDeviceNameList[i] = deviceIdAndName.substring(deviceIdAndName.indexOf('-') + 1);
            midiDeviceInfoList[i] = deviceInfo;
        }
    }

    public void midiDeviceListRefresh() {
        loadMidiDeviceList();
        if (midiDeviceListAdapter != null) {
            midiDeviceListAdapter.notifyDataSetChanged();
        }
    }

    public DialogFragmentCompat newDialog() {
        return new DialogFragmentCompat(this);
    }

    public static class DialogFragmentCompat extends PreferenceDialogFragmentCompat {

        private final MidiDeviceListPreference midiDeviceListPreference;

        public DialogFragmentCompat(MidiDeviceListPreference midiDeviceListPreference) {
            this.midiDeviceListPreference = midiDeviceListPreference;
            Bundle bundle = new Bundle(1);
            bundle.putString(ARG_KEY, midiDeviceListPreference.getKey());
            setArguments(bundle);
        }

        @Override
        protected void onPrepareDialogBuilder(@NonNull AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            midiDeviceListPreference.loadMidiDeviceList();
            midiDeviceListPreference.jpProgressBar = new JPProgressBar(
                    new ContextThemeWrapper(midiDeviceListPreference.getContext(), R.style.JustPianoTheme));
            LinearLayout linearLayout = new LinearLayout(midiDeviceListPreference.getContext());
            linearLayout.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setMinimumWidth(400);
            linearLayout.setPadding(20, 20, 20, 20);
            linearLayout.setBackgroundColor(-1);
            ListView listView = new ListView(midiDeviceListPreference.getContext());
            listView.setDivider(null);
            midiDeviceListPreference.midiDeviceListAdapter = new MidiDeviceListAdapter(midiDeviceListPreference,
                    midiDeviceListPreference.midiDeviceNameList, midiDeviceListPreference.midiDeviceInfoList);
            listView.setAdapter(midiDeviceListPreference.midiDeviceListAdapter);
            linearLayout.addView(listView);
            builder.setView(linearLayout);
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            // nothing
        }
    }
}
