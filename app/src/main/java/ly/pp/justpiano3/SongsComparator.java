package ly.pp.justpiano3;

import ly.pp.justpiano3.activity.OLMelodySelect;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public final class SongsComparator implements Comparator<HashMap> {
    private final OLMelodySelect olMelodySelect;
    private final int f5334c;
    private final Comparator comparator = Collator.getInstance(Locale.CHINA);

    public SongsComparator(OLMelodySelect oLMelodySelect, int i) {
        olMelodySelect = oLMelodySelect;
        f5334c = i;
    }

    @Override
    public int compare(HashMap obj, HashMap obj2) {
        switch (f5334c) {
            case 0:
                return olMelodySelect.f4331t ? 0 - ((String) obj.get("update")).compareTo((String) obj2.get("update")) : ((String) obj.get("update")).compareTo((String) obj2.get("update"));
            case 1:
                return olMelodySelect.f4332u ? ((Double) obj.get("degree")).compareTo((Double) obj2.get("degree")) : 0 - ((Double) obj.get("degree")).compareTo((Double) obj2.get("degree"));
            case 2:
                return olMelodySelect.f4333v ? comparator.compare(obj.get("songName"), obj2.get("songName")) : 0 - comparator.compare(obj.get("songName"), obj2.get("songName"));
            case 3:
                return olMelodySelect.f4334w ? comparator.compare(obj.get("items"), obj2.get("items")) : 0 - comparator.compare(obj.get("items"), obj2.get("items"));
            case 4:
                return olMelodySelect.f4335x ? 0 - comparator.compare(obj.get("playCount"), obj2.get("playCount")) : comparator.compare(obj.get("playCount"), obj2.get("playCount"));
            default:
                return 0;
        }
    }
}
