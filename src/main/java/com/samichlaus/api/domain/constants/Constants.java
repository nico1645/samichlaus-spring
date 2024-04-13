package com.samichlaus.api.domain.constants;

import java.util.HashMap;
import java.util.List;

public class Constants {
    static private final HashMap<Integer, Integer> CHILDREN_MAP = new HashMap<>();
    static private final HashMap<Integer, Integer> SENIOR_MAP = new HashMap<>();

    static public final List<Double> DEPOT_LAT_LNG= List.of(46.990265, 8.310657);
    static private final int DEFAULT_MAX_WAIT_TIME = 50;

    static {
        CHILDREN_MAP.put(0, 0);
        CHILDREN_MAP.put(1, 15);
        CHILDREN_MAP.put(2, 20);
        CHILDREN_MAP.put(3, 30);
        CHILDREN_MAP.put(4, 40);

        SENIOR_MAP.put(0, 0);
        SENIOR_MAP.put(1, 10);
        SENIOR_MAP.put(2, 20);
        SENIOR_MAP.put(3, 30);
        SENIOR_MAP.put(4, 40);
    }

    public static int getChildrenTime(int children) {
       return CHILDREN_MAP.getOrDefault(children, DEFAULT_MAX_WAIT_TIME);
    }

    public static int getSeniorTime(int seniors) {
        return SENIOR_MAP.getOrDefault(seniors, DEFAULT_MAX_WAIT_TIME);
    }

    public static int getChildrenSeniorCapacity(int children, int seniors) {
        int time = getChildrenTime(children) + getSeniorTime(seniors);
        return Math.min(time, 50);
    }
}
