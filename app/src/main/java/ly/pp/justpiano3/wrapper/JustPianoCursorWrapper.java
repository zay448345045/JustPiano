package ly.pp.justpiano3.wrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class JustPianoCursorWrapper extends CursorWrapper {
    private final Cursor cursor;
    private final List<Data> dataList = new ArrayList<>();
    private int position = 0;
    private final Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINESE);

    public JustPianoCursorWrapper(Cursor cursor, String str, boolean z) {
        super(cursor);
        int i = 0;
        this.cursor = cursor;
        if (this.cursor != null && this.cursor.getCount() > 0) {
            int columnIndex = cursor.getColumnIndexOrThrow(str);
            this.cursor.moveToFirst();
            if (str.equals("name")) {
                while (!this.cursor.isAfterLast()) {
                    Data data = new Data();
                    data.name = cursor.getString(columnIndex);
                    data.index = i;
                    dataList.add(data);
                    this.cursor.moveToNext();
                    i++;
                }
                if (z) {
                    Comparator<Data> comparator = (o1, o2) -> this.comparator.compare(o1.name, o2.name);
                    Collections.sort(dataList, comparator);
                } else {
                    Comparator<Data> comparator = (o1, o2) -> this.comparator.compare(o2.name, o1.name);
                    Collections.sort(dataList, comparator);
                }
            } else {
                while (!this.cursor.isAfterLast()) {
                    Data data = new Data();
                    data.num = cursor.getFloat(columnIndex);
                    data.index = i;
                    dataList.add(data);
                    this.cursor.moveToNext();
                    i++;
                }
                if (z) {
                    Comparator<Data> comparator = (o1, o2) -> Double.compare(o1.num, o2.num);
                    Collections.sort(dataList, comparator);
                } else {
                    Comparator<Data> comparator = (o1, o2) -> Double.compare(o2.num, o1.num);
                    Collections.sort(dataList, comparator);
                }
            }
        }
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean move(int i) {
        return moveToPosition(position + i);
    }

    @Override
    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean moveToNext() {
        return moveToPosition(position + 1);
    }

    @Override
    public boolean moveToPosition(int i) {
        if (i < 0 || i >= dataList.size()) {
            if (i < 0) {
                position = -1;
            }
            if (i >= dataList.size()) {
                position = dataList.size();
            }
            return cursor.moveToPosition(i);
        }
        position = i;
        return cursor.moveToPosition(dataList.get(i).index);
    }

    @Override
    public boolean moveToPrevious() {
        return moveToPosition(position - 1);
    }

    static final class Data {
        String name;
        float num;
        int index;
    }
}
