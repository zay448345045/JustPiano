package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.MelodySelect;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MelodySelectAdapter extends CursorAdapter {
    private final MelodySelect melodySelect;

    public MelodySelectAdapter(Context context, Cursor cursor, MelodySelect melodySelect) {
        super(context, cursor, true);
        this.melodySelect = melodySelect;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.f_view, parent, false);
        inflate.setBackgroundResource(R.drawable.selector_ol_button);
        return inflate;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tx = view.findViewById(R.id.ol_s_p);
        String txstr = cursor.getString(cursor.getColumnIndex("name"));
        tx.setText(txstr);
        tx.setTextSize(14);
        tx.setBackgroundResource(R.drawable.selector_ol_button);
        view.setOnClickListener(v -> {
            melodySelect.mo2784a(melodySelect.search(tx.getText().toString()));
            melodySelect.autoctv.dismissDropDown();
            melodySelect.autoctv.clearFocus();
            try {
                ((InputMethodManager) melodySelect.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(melodySelect.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}