package de.iani.cubesideutils.forge.location;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class Location {
    private Reference<ServerLevel> level;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public Location(@Nullable final ServerLevel level, final double x, final double y, final double z) {
        this(level, x, y, z, 0, 0);
    }

    public Location(@Nullable final ServerLevel level, final double x, final double y, final double z, final float yaw, final float pitch) {
        if (level != null) {
            this.level = new WeakReference<>(level);
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public ServerLevel getWorld() {
        if (this.level == null) {
            return null;
        }

        ServerLevel world = this.level.get();
        Preconditions.checkArgument(world != null, "level unloaded");
        return world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public int getBlockX() {
        return locToBlock(x);
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public int getBlockY() {
        return locToBlock(y);
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public int getBlockZ() {
        return locToBlock(z);
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public static float normalizeYaw(float yaw) {
        yaw %= 360.0f;
        if (yaw >= 180.0f) {
            yaw -= 360.0f;
        } else if (yaw < -180.0f) {
            yaw += 360.0f;
        }
        return yaw;
    }

    public static float normalizePitch(float pitch) {
        if (pitch > 90.0f) {
            pitch = 90.0f;
        } else if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        return pitch;
    }

    public static int locToBlock(double loc) {
        final int floor = (int) loc;
        return floor == loc ? floor : floor - (int) (Double.doubleToRawLongBits(loc) >>> 63);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }
}
