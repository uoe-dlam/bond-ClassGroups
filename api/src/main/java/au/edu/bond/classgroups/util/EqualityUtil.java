package au.edu.bond.classgroups.util;

import java.util.Date;

/**
 * Created by Shane Argo on 23/06/2014.
 */
public class EqualityUtil {

    public static <T> boolean nullSafeEquals(T left, T right) {
        return (left == null ? right == null : left.equals(right));
    }

    public static <T> int nullSafeCompare(Comparable<T> o1, T o2) {
        return o1 == null ? 1 : o2 == null ? -1 : o1.compareTo(o2);
    }

    public static int nullSafeCompare(Long o1, Long o2) {
        return o1 == null ? 1 : o2 == null ? -1 : o1.compareTo(o2);
    }

}
