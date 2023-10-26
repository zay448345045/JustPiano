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

import java.io.File;
import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class SimpleSoundListAdapter extends BaseAdapter {
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;
    private final List<String> list;
    private final List<File> fileList;
    private final LayoutInflater layoutInflater;
    private final JPDialogBuilder.JPDialog dialog;

    public SimpleSoundListAdapter(List<String> list, List<File> file, LayoutInflater layoutInflater, OLPlayKeyboardRoom olPlayKeyboardRoom, JPDialogBuilder.JPDialog dialog) {
        this.list = list;
        this.fileList = file;
        this.layoutInflater = layoutInflater;
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
            view = layoutInflater.inflate(R.layout.account_name, null);
        }
        String name = list.get(index);
        TextView nameView = view.findViewById(R.id.account_name_text);
        nameView.setText(name);
        nameView.setOnClickListener(v -> {
            dialog.dismiss();
            olPlayKeyboardRoom.jpprogressBar.show();
            ThreadPoolUtil.execute(() -> {
                if (name.equals("默认音源")) {
                    SoundEngineUtil.unloadSf2Sound();
                    SoundEngineUtil.reLoadOriginalSounds(olPlayKeyboardRoom.getApplicationContext());
                } else {
                    try {
                        File file = new File(olPlayKeyboardRoom.getFilesDir(), "Sounds");
                        if (file.isDirectory()) {
                            File[] listFiles = file.listFiles();
                            if (listFiles != null) {
                                for (File delete : listFiles) {
                                    delete.delete();
                                }
                            }
                        }
                        if (name.endsWith(".ss")) {
                            GZIPUtil.ZIPFileTo(new File(Environment.getExternalStorageDirectory()
                                    + "/JustPiano/Sounds/" + fileList.get(index - 1).getName()), file.toString());
                        }

                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(olPlayKeyboardRoom).edit();
                        edit.putString("sound_list", Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + fileList.get(index - 1).getName());
                        edit.apply();

                        if (name.endsWith(".ss")) {
                            SoundEngineUtil.teardownAudioStreamNative();
                            SoundEngineUtil.unloadWavAssetsNative();
                            SoundEngineUtil.unloadSf2Sound();
                            for (int i = 108; i >= 24; i--) {
                                SoundEngineUtil.preloadSounds(olPlayKeyboardRoom, i);
                            }
                            SoundEngineUtil.afterLoadSounds(olPlayKeyboardRoom);
                        } else if (name.endsWith(".sf2")) {
                            SoundEngineUtil.unloadSf2Sound();
                            SoundEngineUtil.loadSf2Sound(olPlayKeyboardRoom, new File(
                                    Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + name));
                        }
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
