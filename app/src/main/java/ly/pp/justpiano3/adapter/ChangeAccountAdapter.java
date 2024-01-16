package ly.pp.justpiano3.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.LoginActivity;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class ChangeAccountAdapter extends BaseAdapter {
    private final LoginActivity loginActivity;
    private final List<String> list;
    private final LayoutInflater layoutInflater;
    private final JPDialogBuilder.JPDialog dialog;
    private final JSONObject jsonObject;

    public ChangeAccountAdapter(List<String> list, LayoutInflater layoutInflater, LoginActivity loginActivity, JPDialogBuilder.JPDialog dialog, JSONObject jsonObject) {
        this.list = list;
        this.layoutInflater = layoutInflater;
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
            view = layoutInflater.inflate(R.layout.account_name, null);
        }
        String account = list.get(i);
        TextView accountNameTextView = view.findViewById(R.id.account_name_text);
        accountNameTextView.setText(account);
        accountNameTextView.setOnClickListener(v -> {
            try {
                loginActivity.accountTextView.setText(account);
                loginActivity.passwordTextView.setText(jsonObject.getString(account));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        view.findViewById(R.id.account_delete).setOnClickListener(v -> {
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
