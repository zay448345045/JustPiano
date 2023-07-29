package ly.pp.justpiano3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

final class SkinListAdapter extends BaseAdapter {
    final SkinListPreference skinListPreference;
    Context context;
    private CharSequence[] f5938b;
    private CharSequence[] f5939c;

    SkinListAdapter(SkinListPreference skinListPreference, Context context, CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        this.skinListPreference = skinListPreference;
        this.context = context;
        f5938b = charSequenceArr;
        f5939c = charSequenceArr2;
    }

    final void mo3544a(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        f5938b = charSequenceArr;
        f5939c = charSequenceArr2;
    }

    @Override
    public int getCount() {
        return f5938b.length;
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
        String valueOf = String.valueOf(f5939c[i]);
        ((TextView) view.findViewById(R.id.skin_name)).setText(f5938b[i]);
        Button button = view.findViewById(R.id.skin_dele);
        if (valueOf.equals("original") || valueOf.equals("more")) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> skinListPreference.deleteFiles(valueOf));
        }
        button = view.findViewById(R.id.set_skin);
        if (valueOf.equals("more")) {
            button.setText("点击获取更多皮肤");
        } else {
            button.setText("设置皮肤");
        }
        button.setOnClickListener(new ChangeSkinClick(this, valueOf));
        skinListPreference.str1 = valueOf;
        return view;
    }
}
