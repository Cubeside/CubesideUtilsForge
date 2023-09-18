package de.iani.cubesideutils.forge;

import com.mojang.logging.LogUtils;
import de.iani.cubesideutils.forge.scheduler.Helper;
import java.sql.SQLException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CubesideUtilsForgeMod.MODID)
public class CubesideUtilsForgeMod {
    public static final String MODID = "cubesideutilsforge";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CubesideUtilsForgeMod() {
        LOGGER.info("Registering MySQL driver");
        try {
            new com.mysql.cj.jdbc.Driver();
        } catch (SQLException e) {
            LOGGER.warn("Could not register MySql driver", e);
            e.printStackTrace();
        }
        // ModLoadingContext.get().registerConfig(Type.SERVER, CubesideUtilsForgeConfig.GENERAL_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Helper.initialize(this, event);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.END) {
            Helper.processOnTick(this);
        }
    }
}
