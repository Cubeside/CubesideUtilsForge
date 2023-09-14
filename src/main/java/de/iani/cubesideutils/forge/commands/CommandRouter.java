package de.iani.cubesideutils.forge.commands;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.commands.AbstractCommandRouter;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.forge.StringUtilForge;
import de.iani.cubesideutils.forge.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.forge.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.forge.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.forge.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.forge.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.forge.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.forge.permissions.PermissionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommandRouter extends AbstractCommandRouter<SubCommand, CommandSourceStack> implements CommandHandler {

    public static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. Type \"/help\" for help.";

    private CommandExceptionHandler exceptionHandler;

    public CommandRouter() {
        this(true);
    }

    public CommandRouter(boolean caseInsensitive) {
        this(caseInsensitive, CommandExceptionHandler.DEFAULT_HANDLER);
    }

    public CommandRouter(boolean caseInsensitive, CommandExceptionHandler exceptionHandler) {
        super(caseInsensitive);

        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
    }

    // untested!
    public SubCommand getSubCommand(String path) {
        String[] args = path.split(" ");
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(null, args);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;
        return nr == args.length ? currentMap.executor : null;
    }

    @Override
    public List<String> onTabComplete(CommandSourceStack sender, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args, 1);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        String partial = args.length > 0 ? args[args.length - 1] : "";
        Collection<String> options = null;
        List<String> optionsList = null;
        // get tabcomplete options from command
        if (currentMap.executor != null) {
            options = Collections.emptyList();
            if (currentMap.executor.isExecutable(sender)) {
                options = currentMap.executor.onTabComplete(sender, alias, new ArgsParser(args, nr));
            }
        } else {
            options = Collections.emptyList();
        }
        // get tabcomplete options from subcommands
        if (nr == args.length - 1 && currentMap.subCommands != null) {
            for (Entry<String, CommandMap> e : currentMap.subCommands.entrySet()) {
                String key = e.getKey();
                if (StringUtilForge.startsWithIgnoreCase(key, partial)) {
                    CommandMap subcmd = e.getValue();
                    if (isAnySubCommandDisplayable(sender, subcmd)) {
                        if (sender.source instanceof Player || subcmd.executor == null || !subcmd.executor.requiresPlayer()) {
                            if (optionsList == null) {
                                optionsList = options == null ? new ArrayList<>() : new ArrayList<>(options);
                                options = optionsList;
                            }
                            optionsList.add(key);
                        }
                    }
                }
            }
        }
        if (options == null) {
            options = new ArrayList<>();
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                options.add(player.getName().getString());
            }
        }

        optionsList = StringUtilForge.copyPartialMatches(partial, options);
        Collections.sort(optionsList);
        return optionsList;
    }

    @Override
    public int onCommand(CommandSourceStack sender, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        // execute this?
        SubCommand toExecute = currentMap.executor;
        if (toExecute != null) {
            try {
                if (!toExecute.allowsCommandBlock() && (sender.source instanceof BaseCommandBlock)) {
                    throw new DisallowsCommandBlockException(this, sender, alias, toExecute, args);
                }
                if (toExecute.requiresPlayer() && !(sender.source instanceof Player)) {
                    throw new RequiresPlayerException(this, sender, alias, toExecute, args);
                }
                if (!toExecute.hasRequiredPermission(sender) || !toExecute.isAvailable(sender)) {
                    throw new NoPermissionException(this, sender, alias, toExecute, args, toExecute.getRequiredPermission());
                }

                if (toExecute.onCommand(sender, alias, getCommandString(alias, currentMap), new ArgsParser(args, nr))) {
                    return 1;
                } else {
                    throw new IllegalSyntaxException(this, sender, alias, toExecute, args);
                }
            } catch (DisallowsCommandBlockException e) {
                return exceptionHandler.handleDisallowsCommandBlock(e);
            } catch (RequiresPlayerException e) {
                return exceptionHandler.handleRequiresPlayer(e);
            } catch (NoPermissionException e) {
                return exceptionHandler.handleNoPermission(e);
            } catch (IllegalSyntaxException e) {
                return exceptionHandler.handleIllegalSyntax(e);
            } catch (InternalCommandException e) {
                return exceptionHandler.handleInternalException(e);
            } catch (Throwable t) {
                return exceptionHandler.handleInternalException(new InternalCommandException(this, sender, alias, toExecute, args, t));
            }
        }

        if (!isAnySubCommandExecutable(sender, currentMap)) {
            return exceptionHandler.handleNoPermissionForPath(new NoPermissionForPathException(this, sender, alias, args));
        }
        // show valid cmds
        showHelp(sender, alias, currentMap);
        return 0;
    }

    private String getCommandString(String alias, CommandMap currentMap) {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.append('/').append(alias).append(' ');
        ArrayList<CommandMap> hierarchy = new ArrayList<>();
        CommandMap map = currentMap;
        while (map != null) {
            hierarchy.add(map);
            map = map.parent;
        }
        for (int i = hierarchy.size() - 2; i >= 0; i--) {
            prefixBuilder.append(hierarchy.get(i).name).append(' ');
        }
        return prefixBuilder.toString();
    }

    public void showHelp(CommandSourceStack sender, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args);
        CommandMap currentMap = commandMapAndArg.first;
        showHelp(sender, alias, currentMap);
    }

    private void showHelp(CommandSourceStack sender, String alias, CommandMap currentMap) {
        if (currentMap.subCommands != null) {
            String prefix = getCommandString(alias, currentMap);
            for (CommandMap subcmd : currentMap.subcommandsOrdered) {
                String key = subcmd.name;
                if (subcmd.executor == null) {
                    // hat weitere subcommands
                    if (isAnySubCommandDisplayable(sender, subcmd)) {
                        sender.sendSystemMessage(Component.literal(exceptionHandler.getHelpMessagePrefix() + prefix + key + " ..."));
                    }
                } else {
                    if (subcmd.executor.hasRequiredPermission(sender) && subcmd.executor.isAvailable(sender)) {
                        if (sender.source instanceof Player || !subcmd.executor.requiresPlayer()) {
                            sender.sendSystemMessage(Component.literal(exceptionHandler.getHelpMessagePrefix() + prefix + key + " " + subcmd.executor.getUsage(sender)));
                        }
                    }
                }
            }
        }
        if (currentMap.executor != null) {
            SubCommand executor = currentMap.executor;
            if (executor.hasRequiredPermission(sender) && executor.isAvailable(sender)) {
                String prefix = getCommandString(alias, currentMap);
                if (sender.source instanceof Player || !executor.requiresPlayer()) {
                    sender.sendSystemMessage(Component.literal(exceptionHandler.getHelpMessagePrefix() + prefix + executor.getUsage(sender)));
                }
            }
        }
    }

    private boolean isAnySubCommandExecutable(CommandSourceStack sender, CommandMap cmd) {
        if (cmd.executor != null && cmd.executor.isExecutable(sender)) {
            return true;
        }
        if (cmd.subcommandsOrdered == null) {
            return false;
        }
        if (!hasAnyPermission(sender, cmd.requiredPermissions)) {
            return false;
        }
        for (CommandMap subcommand : cmd.subcommandsOrdered) {
            if (isAnySubCommandExecutable(sender, subcommand)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnySubCommandDisplayable(CommandSourceStack sender, CommandMap cmd) {
        if (cmd.executor != null && cmd.executor.isDisplayable(sender)) {
            return true;
        }
        if (cmd.subcommandsOrdered == null) {
            return false;
        }
        if (!hasAnyPermission(sender, cmd.requiredPermissions)) {
            return false;
        }
        for (CommandMap subcommand : cmd.subcommandsOrdered) {
            if (isAnySubCommandDisplayable(sender, subcommand)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyPermission(CommandSourceStack sender, Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }
        if (!(sender.source instanceof ServerPlayer player)) {
            return true;
        }
        for (String permission : permissions) {
            if (PermissionUtil.hasPermission(sender, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkPermission(CommandSourceStack sender) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, new String[0], 1);
        CommandMap currentMap = commandMapAndArg.first;
        return isAnySubCommandExecutable(sender, currentMap);
    }
}
