package io.github.axtuki1.onenightjinro.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class RandomConfig {

    static LinkedHashMap<Job, Integer> rnjobSet;
    static HashMap<RandomSelect.Config, Boolean> rnconfig;

    public static void init(){
        rnjobSet = new LinkedHashMap<Job, Integer>();
        rnconfig = new HashMap<RandomSelect.Config, Boolean>();
    }

    public static LinkedHashMap<Job, Integer> getRandomJobSet() {
        return rnjobSet;
    }

    public static void setRandomJobSet(LinkedHashMap<Job, Integer> jobSet) {
        RandomConfig.rnjobSet = jobSet;
    }

    public static HashMap<RandomSelect.Config, Boolean> getRandomConfig() {
        return rnconfig;
    }

    public static void setRandomConfig(HashMap<RandomSelect.Config, Boolean> rnconfig) {
        RandomConfig.rnconfig = rnconfig;
    }
}
