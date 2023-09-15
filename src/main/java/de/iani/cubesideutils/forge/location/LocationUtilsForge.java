package de.iani.cubesideutils.forge.location;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class LocationUtilsForge {

    public static String getWorldName(ServerLevel level) {
        String serverLevelName = level.toString().substring(12, level.toString().length() - 1);
        ResourceKey<Level> typeKey = level.dimension();
        if (typeKey == null || typeKey == Level.OVERWORLD) {
            return serverLevelName;
        } else {
            String worldName = serverLevelName + "/";
            String suffix;
            if (typeKey == Level.END) {
                suffix = "DIM1";
            } else if (typeKey == Level.NETHER) {
                suffix = "DIM-1";
            } else {
                suffix = typeKey.location().getNamespace() + "/" + typeKey.location().getPath();
            }
            return worldName + suffix;
        }
    }

    public static String getWorldName(Location location) {
        return getWorldName(location.getWorld());
    }

    public static ServerLevel getServerLevel(String worldName) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerLevel serverLevel : server.getAllLevels()) {
            String serverLevelName = getWorldName(serverLevel);
            if (serverLevelName.equals(worldName)) {
                return serverLevel;
            }
        }
        return null;
    }

    public static void teleportPlayerToLocation(ServerPlayer player, Location location) {
        player.teleportTo(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static Location getPlayerLocation(ServerPlayer player) {
        return new Location(player.getLevel(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
    }
}
