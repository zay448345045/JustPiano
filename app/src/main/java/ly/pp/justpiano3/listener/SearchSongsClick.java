package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import ly.pp.justpiano3.activity.MelodySelect;

final class SearchSongsClick implements OnClickListener {
    private final MelodySelect melodySelect;
    private final String keyWords;

    SearchSongsClick(MelodySelect melodySelect, String str) {
        this.melodySelect = melodySelect;
        keyWords = str;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (keyWords.isEmpty()) {
            Toast.makeText(melodySelect, "请输入曲谱的关键字!", Toast.LENGTH_SHORT).show();
        } else {
            dialogInterface.dismiss();
            melodySelect.cursor = melodySelect.sqlitedatabase.query("jp_data", null, "name like '%" + keyWords.replace("'", "''") + "%' AND " + melodySelect.onlineCondition, null, null, null, melodySelect.sortStr);
            int count = melodySelect.cursor.getCount();
            if (melodySelect.cursor.getCount() == 0) {
                Toast.makeText(melodySelect, "未搜索到与 " + keyWords + " 有关的曲目!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(melodySelect, "搜索到" + count + "首与 " + keyWords + " 有关的曲目!", Toast.LENGTH_SHORT).show();
            melodySelect.mo2784a(melodySelect.cursor);
        }
    }
}
