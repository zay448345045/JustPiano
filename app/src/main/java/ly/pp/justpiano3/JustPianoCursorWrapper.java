package ly.pp.justpiano3;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class JustPianoCursorWrapper extends CursorWrapper {
    private Cursor f5947a;
    private List<C1312mj> f5948b = new ArrayList<>();
    private int f5949c = 0;
    private Comparator f5952f = Collator.getInstance(java.util.Locale.CHINESE);

    JustPianoCursorWrapper(Cursor cursor, String str, boolean z) {
        super(cursor);
        int i = 0;
        f5947a = cursor;
        if (f5947a != null && f5947a.getCount() > 0) {
            int columnIndexOrThrow = cursor.getColumnIndexOrThrow(str);
            f5947a.moveToFirst();
            if (str.equals("name")) {
                while (!f5947a.isAfterLast()) {
                    C1312mj c1312mj = new C1312mj();
                    c1312mj.f5955a = cursor.getString(columnIndexOrThrow);
                    c1312mj.f5956b = i;
                    f5948b.add(c1312mj);
                    f5947a.moveToNext();
                    i++;
                }
                if (z) {
                    Comparator<C1312mj> f5950d = (o1, o2) -> f5952f.compare(o1.f5955a, o2.f5955a);
                    Collections.sort(f5948b, f5950d);
                } else {
                    Comparator<C1312mj> f5951e = (o1, o2) -> f5952f.compare(o2.f5955a, o1.f5955a);
                    Collections.sort(f5948b, f5951e);
                }
            } else {
                while (!f5947a.isAfterLast()) {

                    C1312mj c1312mj = new C1312mj();
                    c1312mj.num = cursor.getFloat(columnIndexOrThrow);
                    c1312mj.f5956b = i;
                    f5948b.add(c1312mj);
                    f5947a.moveToNext();
                    i++;
                }
                if (z) {
                    Comparator<C1312mj> f5950d = (o1, o2) -> Float.compare(o1.num, o2.num);
                    Collections.sort(f5948b, f5950d);
                } else {
                    Comparator<C1312mj> f5951e = (o1, o2) -> Float.compare(o2.num, o1.num);
                    Collections.sort(f5948b, f5951e);
                }
            }
        }
    }

    @Override
    public final int getPosition() {
        return f5949c;
    }

    @Override
    public final boolean move(int i) {
        return moveToPosition(f5949c + i);
    }

    @Override
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public final boolean moveToNext() {
        return moveToPosition(f5949c + 1);
    }

    @Override
    public final boolean moveToPosition(int i) {
        if (i < 0 || i >= f5948b.size()) {
            if (i < 0) {
                f5949c = -1;
            }
            if (i >= f5948b.size()) {
                f5949c = f5948b.size();
            }
            return f5947a.moveToPosition(i);
        }
        f5949c = i;
        return f5947a.moveToPosition(f5948b.get(i).f5956b);
    }

    @Override
    public final boolean moveToPrevious() {
        return moveToPosition(f5949c - 1);
    }

    static final class C1312mj {
        String f5955a;
        float num;
        int f5956b;
    }
}
