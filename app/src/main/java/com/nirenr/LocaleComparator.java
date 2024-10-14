package com.nirenr;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by nirenr on 2019/6/25.
 */

public class LocaleComparator implements Comparator <String>{

    public int compare(String o1, String o2) {
        Collator myCollator = Collator.getInstance(java.util.Locale.getDefault());

        if (myCollator.compare(o1, o2) < 0)

            return -1;

        else if (myCollator.compare(o1, o2) > 0)

            return 1;

        else

            return 0;

    }

}