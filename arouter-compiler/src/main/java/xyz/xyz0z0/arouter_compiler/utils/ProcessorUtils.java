package xyz.xyz0z0.arouter_compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Author: Cheng
 * Date: 2021/6/24 9:56
 * Description: xyz.xyz0z0.arouter_compiler.utils
 *
 * @author Cheng
 */
public final class ProcessorUtils {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
