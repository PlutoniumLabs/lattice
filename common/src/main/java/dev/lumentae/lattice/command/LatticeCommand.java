package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.Mod;
import dev.lumentae.lattice.features.nickname.NicknameManager;
import dev.lumentae.lattice.features.status.StatusManager;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.net.URI;
import java.time.LocalDateTime;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LatticeCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal(Constants.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("allowed");
                            builder.suggest("config");
                            builder.suggest("help");
                            builder.suggest("illegal");
                            builder.suggest("motd");
                            builder.suggest("nick");
                            builder.suggest("open");
                            builder.suggest("pvp");
                            builder.suggest("rules");
                            builder.suggest("status");
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String action = StringArgumentType.getString(commandContext, "action");
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            if (!action.equals("help")) return Command.SINGLE_SUCCESS;

                            TextUtils.sendMessage(player, Component.translatable("message.lattice.help")
                                    .withStyle(
                                            Style.EMPTY.withClickEvent(
                                                    new ClickEvent.OpenUrl(URI.create("https://github.com/lumentae/lattice"))
                                            )
                                    )
                                    .withStyle(ChatFormatting.UNDERLINE)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(subCommand(dispatcher))
                )
        );
    }

    public RequiredArgumentBuilder<CommandSourceStack, String> subCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        return argument("subAction", StringArgumentType.word())
                .suggests((context, builder) -> {
                    String action = StringArgumentType.getString(context, "action");
                    switch (action) {
                        case "config":
                            builder.suggest("reload");
                            builder.suggest("save");
                            break;
                        case "status", "nick", "rules", "pvp":
                            for (ServerPlayer player : Mod.getServer().getPlayerList().getPlayers()) {
                                builder.suggest(player.getName().getString());
                            }
                            break;
                        case "motd", "illegal", "allowed":
                            builder.suggest("add");
                            builder.suggest("list");
                            builder.suggest("remove");
                            break;
                        case "open":
                            builder.suggest("end");
                            builder.suggest("nether");
                            builder.suggest("server");
                            break;
                    }
                    return builder.buildFuture();
                })
                .then(argument("subActionArg", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            String action = StringArgumentType.getString(context, "action");
                            String subAction = StringArgumentType.getString(context, "subAction");
                            switch (action) {
                                case "status", "nick":
                                    builder.suggest("remove");
                                    break;
                                case "rules", "pvp":
                                    builder.suggest("disable");
                                    builder.suggest("enable");
                                    break;
                                case "open":
                                    builder.suggest(LocalDateTime.now().toString());
                                    switch (subAction) {
                                        case "nether":
                                            builder.suggest(Config.INSTANCE.netherOpenDate.toString());
                                            break;
                                        case "end":
                                            builder.suggest(Config.INSTANCE.endOpenDate.toString());
                                            break;
                                        case "server":
                                            builder.suggest(Config.INSTANCE.serverOpenDate.toString());
                                            break;
                                    }
                                    break;
                                case "motd":
                                    if (subAction.equals("remove")) {
                                        for (String motd : Config.INSTANCE.motds) {
                                            builder.suggest(motd);
                                        }
                                    }
                                    break;
                            }
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String action = StringArgumentType.getString(commandContext, "action");
                            String subAction = StringArgumentType.getString(commandContext, "subAction");
                            String subActionArg = StringArgumentType.getString(commandContext, "subActionArg");
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            ServerPlayer searchedPlayer = Mod.getServer().getPlayerList().getPlayerByName(subAction);

                            switch (action) {
                                case "status":
                                    switch (subActionArg) {
                                        case "remove":
                                            StatusManager.removeStatus(searchedPlayer);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.status.removed"));
                                            break;
                                        default:
                                            StatusManager.setStatus(searchedPlayer, subActionArg);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.status.set")
                                                    .append(StatusManager.getStatus(searchedPlayer))
                                            );
                                            break;
                                    }
                                    break;
                                case "nick":
                                    switch (subActionArg) {
                                        case "remove":
                                            NicknameManager.removeNickname(searchedPlayer);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.removed"));
                                            break;
                                        default:
                                            NicknameManager.setNickname(searchedPlayer, subActionArg);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.set")
                                                    .append(NicknameManager.getNickname(searchedPlayer))
                                            );
                                            break;
                                    }
                                    break;
                                case "rules":
                                    switch (subActionArg) {
                                        case "enable":
                                            Config.getPlayerPlayOptions(searchedPlayer.getUUID()).acceptedRules = true;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.rules.enabled")
                                                    .append(searchedPlayer.getName())
                                                    .append(Component.translatable("message.lattice.now"))
                                            );
                                            break;
                                        case "disable":
                                            Config.getPlayerPlayOptions(searchedPlayer.getUUID()).acceptedRules = false;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.rules.disabled")
                                                    .append(searchedPlayer.getName())
                                                    .append(Component.translatable("message.lattice.now"))
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subActionArg)
                                                    .withStyle(ChatFormatting.RED)
                                            );
                                    }
                                    break;
                                case "pvp":
                                    switch (subActionArg) {
                                        case "enable":
                                            Config.getPlayerPlayOptions(searchedPlayer.getUUID()).enablePvP = true;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.pvp.enabled")
                                                    .append(searchedPlayer.getName())
                                                    .append(Component.translatable("message.lattice.now"))
                                            );
                                            break;
                                        case "disable":
                                            Config.getPlayerPlayOptions(searchedPlayer.getUUID()).enablePvP = false;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.pvp.disabled")
                                                    .append(searchedPlayer.getName())
                                                    .append(Component.translatable("message.lattice.now"))
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subActionArg)
                                                    .withStyle(ChatFormatting.RED)
                                            );
                                    }
                                    break;
                                case "illegal":
                                    switch (subAction) {
                                        case "add":
                                            Config.INSTANCE.illegalMods.add(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.add")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        case "remove":
                                            Config.INSTANCE.illegalMods.remove(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.remove")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subAction)
                                                    .withStyle(ChatFormatting.RED));
                                            break;
                                    }
                                    break;
                                case "allowed":
                                    switch (subAction) {
                                        case "add":
                                            Config.INSTANCE.allowedMods.add(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.add")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        case "remove":
                                            Config.INSTANCE.allowedMods.remove(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.remove")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subAction)
                                                    .withStyle(ChatFormatting.RED));
                                            break;
                                    }
                                    break;
                                case "open":
                                    LocalDateTime parsed = TextUtils.parseDateString(subActionArg);
                                    switch (subAction) {
                                        case "nether":
                                            Config.INSTANCE.netherOpenDate = parsed;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.open.set")
                                                    .append(parsed.toString())
                                            );
                                            break;
                                        case "end":
                                            Config.INSTANCE.endOpenDate = parsed;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.open.set")
                                                    .append(parsed.toString())
                                            );
                                            break;
                                        case "server":
                                            Config.INSTANCE.serverOpenDate = parsed;
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.open.set")
                                                    .append(parsed.toString())
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subAction)
                                                    .withStyle(ChatFormatting.RED));
                                            break;
                                    }
                                    break;
                                case "motd":
                                    switch (subAction) {
                                        case "add":
                                            Config.INSTANCE.motds.add(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.add")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        case "remove":
                                            Config.INSTANCE.motds.remove(subActionArg);
                                            Config.saveConfig();
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.remove")
                                                    .append(subActionArg)
                                            );
                                            break;
                                        default:
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                                    .append(subAction)
                                                    .withStyle(ChatFormatting.RED));
                                            break;
                                    }
                                    break;
                                default:
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                            .append(subAction)
                                            .withStyle(ChatFormatting.RED));
                                    break;
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).executes(commandContext -> {
                    ServerPlayer player = commandContext.getSource().getPlayer();
                    String action = StringArgumentType.getString(commandContext, "action");
                    String subAction = StringArgumentType.getString(commandContext, "subAction");

                    switch (action) {
                        case "status":
                            String status = Config.getPlayerPlayOptions(player.getUUID()).status;
                            if (status == null || status.isEmpty()) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.status.none"));
                                return Command.SINGLE_SUCCESS;
                            }
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.status.show")
                                    .append(StatusManager.getStatus(player)));
                            break;
                        case "nick":
                            String nickname = Config.getPlayerPlayOptions(player.getUUID()).nickname;
                            if (nickname == null || nickname.isEmpty()) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.none"));
                                return Command.SINGLE_SUCCESS;
                            }
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.show")
                                    .append(NicknameManager.getNickname(player)));
                            break;
                        case "config":
                            switch (subAction) {
                                case "save":
                                    Config.saveConfig();
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.config.save"));
                                    break;
                                case "reload":
                                    Config.loadConfig();
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.config.reload"));
                                    break;
                                default:
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                            .append(subAction)
                                            .withStyle(ChatFormatting.RED)
                                    );
                                    break;
                            }
                            break;
                        case "rules":
                            boolean acceptedRules = Config.getPlayerPlayOptions(player.getUUID()).acceptedRules;
                            if (acceptedRules) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.rules.enabled")
                                        .append(player.getName())
                                );
                                break;
                            }
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.rules.disabled")
                                    .append(player.getName())
                            );
                            break;
                        case "pvp":
                            boolean enablePvP = Config.getPlayerPlayOptions(player.getUUID()).enablePvP;
                            if (enablePvP) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.pvp.enabled")
                                        .append(player.getName())
                                );
                                break;
                            }
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.pvp.disabled")
                                    .append(player.getName())
                            );
                            break;
                        case "motd":
                            switch (subAction) {
                                case "list":
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.list")
                                            .append(String.join("\n- ", Config.INSTANCE.motds))
                                    );
                                    break;
                                default:
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                            .append(subAction)
                                            .withStyle(ChatFormatting.RED)
                                    );
                                    break;
                            }
                            break;
                        case "allowed":
                            switch (subAction) {
                                case "list":
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.list")
                                            .append(String.join("\n- ", Config.INSTANCE.allowedMods))
                                    );
                                    break;
                                default:
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                            .append(subAction)
                                            .withStyle(ChatFormatting.RED)
                                    );
                                    break;
                            }
                            break;
                        case "illegal":
                            switch (subAction) {
                                case "list":
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.list")
                                            .append(String.join("\n- ", Config.INSTANCE.illegalMods))
                                    );
                                    break;
                                default:
                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                            .append(subAction)
                                            .withStyle(ChatFormatting.RED)
                                    );
                                    break;
                            }
                            break;
                        default:
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.lattice.unknown")
                                    .append(subAction)
                                    .withStyle(ChatFormatting.RED)
                            );
                            break;
                    }


                    return Command.SINGLE_SUCCESS;
                })
                ;
    }
}
