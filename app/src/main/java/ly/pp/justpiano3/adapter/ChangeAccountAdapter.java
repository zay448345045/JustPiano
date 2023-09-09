package ly.pp.justpiano3.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.view.JPDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class ChangeAccountAdapter extends BaseAdapter {
    private final LoginActivity loginActivity;
    private final List<String> list;
    private final LayoutInflater li;
    private final JPDialog.JDialog dialog;
    private final JSONObject jsonObject;

    public ChangeAccountAdapter(List<String> list, LayoutInflater layoutInflater, LoginActivity loginActivity, JPDialog.JDialog dialog, JSONObject jsonObject) {
        this.list = list;
        li = layoutInflater;
        this.loginActivity = loginActivity;
        this.dialog = dialog;
        this.jsonObject = jsonObject;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = li.inflate(R.layout.account_name, null);
        }
        String account = list.get(i);
        TextView accountName = view.findViewById(R.id.account_name_text);
        accountName.setText(account);
        accountName.setOnClickListener(v -> {
            try {
                loginActivity.accountTextView.setText(account);
                loginActivity.passwordTextView.setText(jsonObject.getString(account));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        Button accountDelete = view.findViewById(R.id.account_delete);
        accountDelete.setOnClickListener(v -> {
            jsonObject.remove(account);
            SharedPreferences.Editor edit = loginActivity.sharedPreferences.edit();
            edit.putString("accountList", jsonObject.toString());
            edit.apply();
            list.remove(account);
            notifyDataSetChanged();
        });
        return view;
    }
}
