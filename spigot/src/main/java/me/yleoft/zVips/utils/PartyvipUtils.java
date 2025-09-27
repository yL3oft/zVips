package me.yleoft.zVips.utils;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.zVipsBukkit;

public abstract class PartyvipUtils {

    public static void checkPartyvip(int points) {
        int goal = zVipsBukkit.cfgu.pvipObjective();
        if(goalAchieved(points, goal)) {
            int leftover = points-goal;
            PlayerUtils.performCommand(null, zVipsBukkit.cfgu.pvipCommands());
            if(goalAchieved(leftover, goal)) {
                checkPartyvip(leftover);
                return;
            }
            zVipsBukkit.dbe.setPVPoints(leftover);
        }
    }
    public static void checkPartyvip() {
        checkPartyvip(zVipsBukkit.dbe.getPVPoints());
    }

    public static boolean goalAchieved(int points, int goal) {
        return points >= goal;
    }
    public static boolean goalAchieved() {
        return goalAchieved(zVipsBukkit.dbe.getPVPoints(), zVipsBukkit.cfgu.pvipObjective());
    }

}
