package de.iani.cubesideutils.forge.commands;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;

public interface CommandHandler {
    public boolean checkPermission(CommandSourceStack source);

    public int onCommand(CommandSourceStack source, String label, String[] args);

    public List<String> onTabComplete(CommandSourceStack source, String label, String[] args);
}
