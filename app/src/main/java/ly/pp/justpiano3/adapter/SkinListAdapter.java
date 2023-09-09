package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.ChangeSkinClick;
import ly.pp.justpiano3.view.SkinListPreference;

public final class SkinListAdapter extends BaseAdapter {
    public final SkinListPreference skinListPreference;
    public Context context;
    private CharSequence[] skinNameList;
    private CharSequence[] skinKeyList;

    public SkinListAdapter(SkinListPreference skinListPreference, Context context, CharSequence[] skinNameList, CharSequence[] skinKeyList) {
        this.skinListPreference = skinListPreference;
        this.context = context;
        this.skinNameList = skinNameList;
        this.skinKeyList = skinKeyList;
    }

    public void mo3544a(CharSequence[] skinNameList, CharSequence[] skinKeyList) {
        this.skinNameList = skinNameList;
        this.skinKeyList = skinKeyList;
    }

    @Override
    public int getCount() {
        return skinNameList.length;
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
        String skinKey = String.valueOf(skinKeyList[i]);
        ((TextView) view.findViewById(R.id.skin_name_view)).setText(skinNameList[i]);
        Button button = view.findViewById(R.id.skin_dele);
        if (skinKey.equals("original") || skinKey.equals("more")) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> skinListPreference.deleteFiles(skinKey));
        }
        button = view.findViewById(R.id.set_skin);
        if (skinKey.equals("more")) {
            button.setText("点击获取更多皮肤");
        } else {
            button.setText("设置皮肤");
        }
        button.setOnClickListener(new ChangeSkinClick(this, skinKey));
        skinListPreference.skinKey = skinKey;
        return view;
    }
}
