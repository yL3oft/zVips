package me.yleoft.zVips.utils;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.zVips;

public abstract class PartyvipUtils {

    public static void checkPartyvip(int points) {
        int goal = zVips.cfgu.pvipObjective();
        if(goalAchieved(points, goal)) {
            int leftover = points-goal;
            PlayerUtils.performCommand(null, zVips.cfgu.pvipCommands());
            if(goalAchieved(leftover, goal)) {
                checkPartyvip(leftover);
                return;
            }
            zVips.dbe.setPVPoints(leftover);
        }
    }
    public static void checkPartyvip() {
        checkPartyvip(zVips.dbe.getPVPoints());
    }

    public static boolean goalAchieved(int points, int goal) {
        return points >= goal;
    }
    public static boolean goalAchieved() {
        return goalAchieved(zVips.dbe.getPVPoints(), zVips.cfgu.pvipObjective());
    }

}
