package de.iani.cubesideutils.forge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
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
        // ModLoadingContext.get().registerConfig(Type.SERVER, CubesideUtilsForgeConfig.GENERAL_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
    }
}
