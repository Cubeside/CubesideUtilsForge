package de.iani.cubesideutils.forge.permissions;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
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

    private static interface PermissionUtilImpl {
        boolean hasPermission(ServerPlayer player, String permission);
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
            return permissionData.checkPermission(permission).asBoolean();
        }
    }
}
