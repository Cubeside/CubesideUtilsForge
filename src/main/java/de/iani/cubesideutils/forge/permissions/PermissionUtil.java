package de.iani.cubesideutils.forge.permissions;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.util.Tristate;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class PermissionUtil {
    private static final PermissionUtilImpl impl;
    static {
        PermissionUtilImpl impl0 = null;
        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
            impl0 = new PermissionUtilImplLuckPerms();
        } catch (ClassNotFoundException e) {
        }
        if (impl0 == null) {
            impl0 = new PermissionUtilImplMinecraft();
        }
        impl = impl0;
    }

    public static boolean hasPermission(CommandSourceStack source, String permission) {
        return hasPermission(source.source, permission);
    }

    public static boolean hasPermission(CommandSource source, String permission) {
        if (!(source instanceof ServerPlayer player)) {
            return true;
        }
        return hasPermission(player, permission);
    }

    public static boolean hasPermission(ServerPlayer player, String permission) {
        return impl.hasPermission(player, permission);
    }

    public static String getPrefix(ServerPlayer player) {
        return impl.getPrefix(player);
    }

    public static String getSuffix(ServerPlayer player) {
        return impl.getSuffix(player);
    }

    public static String getMetaValue(ServerPlayer player, String key) {
        return impl.getMetaValue(player, key);
    }

    private static interface PermissionUtilImpl {
        boolean hasPermission(ServerPlayer player, String permission);

        default String getPrefix(ServerPlayer player) {
            return null;
        }

        default String getSuffix(ServerPlayer player) {
            return null;
        }

        default String getMetaValue(ServerPlayer player, String key) {
            return null;
        }
    }

    private static class PermissionUtilImplMinecraft implements PermissionUtilImpl {
        @Override
        public boolean hasPermission(ServerPlayer player, String permission) {
            return player.hasPermissions(2);
        }
    }

    private static class PermissionUtilImplLuckPerms implements PermissionUtilImpl {
        @Override
        public boolean hasPermission(ServerPlayer player, String permission) {
            CachedPermissionData permissionData = LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player).getCachedData().getPermissionData();
            Tristate result = permissionData.checkPermission(permission);
            return result == Tristate.UNDEFINED ? player.hasPermissions(2) : result.asBoolean();
        }

        @Override
        public String getPrefix(ServerPlayer player) {
            return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player).getCachedData().getMetaData().getPrefix();
        }

        @Override
        public String getSuffix(ServerPlayer player) {
            return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player).getCachedData().getMetaData().getSuffix();
        }

        @Override
        public String getMetaValue(ServerPlayer player, String key) {
            return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player).getCachedData().getMetaData().getMetaValue(key);
        }
    }
}
