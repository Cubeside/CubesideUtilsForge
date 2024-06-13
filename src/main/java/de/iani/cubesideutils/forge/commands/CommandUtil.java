package de.iani.cubesideutils.forge.commands;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.iani.cubesideutils.forge.CubesideUtilsForgeMod;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.StringUtils;

public class CommandUtil {
    private CommandUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, String command, CommandHandler handler) {
        Preconditions.checkNotNull(command, "dispatcher");
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(handler, "handler");

        CommandAdapter adapter = new CommandAdapter(handler);
        LiteralCommandNode<CommandSourceStack> commandNode = Commands.literal(command).requires(adapter).executes(adapter).build();
        ArgumentCommandNode<CommandSourceStack, String> defaultArgs = RequiredArgumentBuilder.<CommandSourceStack, String> argument("args", StringArgumentType.greedyString()).suggests(adapter).executes(adapter).build();
        Field childrenField = CommandNode.class.getDeclaredFields().clone()[0]; // Map<String, CommandNode<S>> children
        try {
            childrenField.setAccessible(true);
            Map<?, ?> map = (Map<?, ?>) childrenField.get(dispatcher.getRoot());
            map.remove(command);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // commandNode.children.remove(command);
        commandNode.addChild(defaultArgs);
        dispatcher.getRoot().addChild(commandNode);
    }

    public static class CommandAdapter implements Predicate<CommandSourceStack>, Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {
        private final CommandHandler handler;

        public CommandAdapter(CommandHandler handler) {
            this.handler = handler;
        }

        @Override
        public boolean test(CommandSourceStack source) {
            return handler.checkPermission(source);
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            String commandLine = context.getRange().get(context.getInput());
            String[] args = StringUtils.split(commandLine, ' ');
            if (args.length == 0) {
                return 0;
            }
            String label = args[0].toLowerCase(java.util.Locale.ENGLISH);
            args = Arrays.copyOfRange(args, 1, args.length);
            try {
                return handler.onCommand(context.getSource(), label, args);
            } catch (Throwable ex) {
                CubesideUtilsForgeMod.LOGGER.error("Unhandled exception in command", ex);
            }
            return 0;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            String commandLine = builder.getInput();
            String[] args = StringUtils.split(commandLine, ' ');
            if (commandLine.endsWith(" ")) {
                args = Arrays.copyOfRange(args, 0, args.length + 1);
                args[args.length - 1] = "";
            }
            List<String> results = List.of();
            try {
                if (args.length > 0) {
                    String label = args[0].toLowerCase(java.util.Locale.ENGLISH);
                    args = Arrays.copyOfRange(args, 1, args.length);
                    results = handler.onTabComplete(context.getSource(), label, args);
                }
            } catch (Throwable ex) {
                CubesideUtilsForgeMod.LOGGER.error("Unhandled exception in tab complete", ex);
            }
            builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
            if (results != null) {
                for (String s : results) {
                    builder.suggest(s);
                }
            }
            return builder.buildFuture();
        }

    }

    public static boolean executeCommand(CommandSourceStack sender, String command) {
        Preconditions.checkArgument(sender != null, "sender cannot be null");
        Preconditions.checkArgument(command != null, "commandLine cannot be null");
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            try {
                server.getCommands().getDispatcher().execute(command, sender);
                return true;
            } catch (CommandSyntaxException e) {
                CubesideUtilsForgeMod.LOGGER.error("Command could not be executed.", e);
                return false;
            }
        }
        return false;
    }
}
