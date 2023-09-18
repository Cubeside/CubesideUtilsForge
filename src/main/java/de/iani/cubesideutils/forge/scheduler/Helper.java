package de.iani.cubesideutils.forge.scheduler;

import de.iani.cubesideutils.forge.CubesideUtilsForgeMod;
import net.minecraftforge.event.server.ServerStartingEvent;

public class Helper {
    public static void initialize(CubesideUtilsForgeMod mod, ServerStartingEvent event) {
        Scheduler.INSTANCE.initialize(event.getServer().getRunningThread());
    }

    public static void processOnTick(CubesideUtilsForgeMod mod) {
        Scheduler.INSTANCE.processOnTick();
    }
}
