package io.github.axtuki1.onenightjinro.scoreboard;

import io.github.axtuki1.onenightjinro.JinroConfig;
import io.github.axtuki1.onenightjinro.MConJinro;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

public class JinroScoreboard {

    private static Scoreboard board;
    private static Objective Info;
    private static Objective ChatCounter;

    public static Scoreboard getScoreboard() {
        board = Bukkit.getScoreboardManager().getMainScoreboard();
        return board;
    }

    public static Objective getInfoObj() {
        Info = getScoreboard().getObjective(getInfoName());
        return Info;
    }

    public static String getInfoName() {
        String InfoName = MConJinro.getMain().getConfig().getString(JinroConfig.InfoObjectiveName.getPath());
        return InfoName;
    }
}
