package ly.pp.justpiano3.adapter;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.thread.ThreadPoolUtils;
import ly.pp.justpiano3.utils.GZIPUtil;

import java.io.File;
import java.util.List;

public final class SimpleSoundListAdapter extends BaseAdapter {
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;
    private final List<String> list;
    private final List<File> fileList;
    private final LayoutInflater li;
    private final JPDialog.JDialog dialog;

    public SimpleSoundListAdapter(List<String> list, List<File> file, LayoutInflater layoutInflater, OLPlayKeyboardRoom olPlayKeyboardRoom, JPDialog.JDialog dialog) {
        this.list = list;
        this.fileList = file;
        li = layoutInflater;
        this.olPlayKeyboardRoom = olPlayKeyboardRoom;
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int index, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = li.inflate(R.layout.account_name, null);
        }
        String name = list.get(index);
        TextView nameView = view.findViewById(R.id.account_name_text);
        nameView.setText(name);
        nameView.setOnClickListener(v -> {
            dialog.dismiss();
            olPlayKeyboardRoom.jpprogressBar.show();
            ThreadPoolUtils.execute(() -> {
                if (name.equals("原生音源")) {
                    SoundEngineUtil.reLoadOriginalSounds(olPlayKeyboardRoom.getApplicationContext());
                } else {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(olPlayKeyboardRoom).edit();
                    edit.putString("sound_list", Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + fileList.get(index - 1).getName());
                    try {
                        int i;
                        File file = new File(olPlayKeyboardRoom.getFilesDir(), "Sounds");
                        if (file.isDirectory()) {
                            File[] listFiles = file.listFiles();
                            if (listFiles != null && listFiles.length > 0) {
                                for (File delete : listFiles) {
                                    delete.delete();
                                }
                            }
                        }
                        GZIPUtil.ZIPFileTo(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + fileList.get(index - 1).getName()), file.toString());
                        edit.apply();
                        SoundEngineUtil.teardownAudioStreamNative();
                        SoundEngineUtil.unloadWavAssetsNative();
                        for (i = 108; i >= 24; i--) {
                            SoundEngineUtil.preloadSounds(olPlayKeyboardRoom.getApplicationContext(), i);
                        }
                        SoundEngineUtil.afterLoadSounds(olPlayKeyboardRoom.getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                olPlayKeyboardRoom.runOnUiThread(() -> {
                    olPlayKeyboardRoom.jpprogressBar.dismiss();
                    Toast.makeText(olPlayKeyboardRoom, "音源设置成功", Toast.LENGTH_SHORT).show();
                });
            });
        });
        Button delete = view.findViewById(R.id.account_delete);
        delete.setVisibility(View.GONE);
        return view;
    }
}
