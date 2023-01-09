package fy.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

public class MapTest {

    @Test
    void test() {
        Multimap<Integer, String> map = ArrayListMultimap.create();
        map.put(1, "a");
        map.put(1, "b");
        System.out.println(map);
        System.out.println(map.keySet().stream().findFirst().get().getClass());
        System.out.println(map.values().stream().findFirst().get().getClass());
    }
}
