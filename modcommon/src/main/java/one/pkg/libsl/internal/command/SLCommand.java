/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import one.pkg.libsl.api.loader.JavaLoader;
import one.pkg.libsl.api.ui.oreui.OreUIExampleScreen;
import one.pkg.libsl.payloads.DialogPayload;
import one.pkg.libsl.payloads.DialogsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SLCommand {
    private static final List<DialogPayload> dialogs = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger("SpringLotus");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment) {
        String baseCmd = environment == null || environment == Commands.CommandSelection.INTEGRATED ?
                "springlotusc" : "springlotus";

        var cmd = Commands.literal(baseCmd)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("dialog")
                        .then(Commands.literal("send").then(Commands.argument("player", EntityArgument.players())
                                .executes(SLCommand::sendDialog)))
                        .then(Commands.literal("reset").executes(SLCommand::cleanDialog))
                        .then(Commands.literal("add")
                                .then(Commands.argument("title", StringArgumentType.string())
                                        .then(Commands.argument("desc", StringArgumentType.string())
                                                .executes(SLCommand::addDialog))
                                )
                        )
                ).then(Commands.literal("oreui").then(Commands.literal("test").executes(SLCommand::testOreUI)));

        dispatcher.register(cmd);
    }

    private static int cleanDialog(CommandContext<CommandSourceStack> context) {
        dialogs.clear();
        context.getSource().sendSuccess(() ->
                Component.literal("[SpringLotus] ").withStyle(ChatFormatting.AQUA).append(
                        Component.literal("Dialogs cleared successfully").withStyle(ChatFormatting.GREEN)
                ), false);
        return 1;
    }

    private static int addDialog(CommandContext<CommandSourceStack> context) {
        String title = context.getArgument("title", String.class);
        String desc = context.getArgument("desc", String.class);
        dialogs.add(new DialogPayload(title, desc, true));
        context.getSource().sendSuccess(() ->
                Component.literal("[SpringLotus] ").withStyle(ChatFormatting.AQUA).append(
                        Component.literal("Dialog added successfully").withStyle(ChatFormatting.GREEN)
                ), false);
        return 1;
    }

    private static int sendDialog(CommandContext<CommandSourceStack> context) {
        if (true) return 0;
        if (dialogs.isEmpty()) {
            context.getSource().sendFailure(Component.literal("[SpringLotus] No dialogs to send")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }
        EntitySelector selector = context.getArgument("player", EntitySelector.class);
        try {
            for (var player : selector.findPlayers(context.getSource())) {
                /*if (JavaLoader.INSTANCE.net().canSend(player, dialogs.get(0).type())) {
                    new DialogsPayload(dialogs).send(player);
                } else {
                    logger.warn("Player {} cannot receive dialog", player.getDisplayName().getString());
                }*/
            }
            dialogs.clear();
            context.getSource().sendSuccess(() ->
                    Component.literal("[SpringLotus] ").withStyle(ChatFormatting.AQUA).append(
                            Component.literal("Dialog sent successfully").withStyle(ChatFormatting.GREEN)
                    ), false);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static int testOreUI(CommandContext<CommandSourceStack> context) {
        if (!JavaLoader.INSTANCE.isClient()) {
            context.getSource().sendFailure(
                    Component.literal("[SpringLotus] ")
                            .withStyle(ChatFormatting.AQUA)
                            .append(
                                    Component.literal("OreUI test command can only be executed by a player on the client side")
                                            .withStyle(ChatFormatting.RED)
                            )
            );
            return 0;
        }
        context.getSource().sendSuccess(() ->
                Component.literal("[SpringLotus] ").withStyle(ChatFormatting.AQUA).append(
                        Component.literal("OreUI test command executed").withStyle(ChatFormatting.GREEN)
                ), false);
        Screen screen = Minecraft.getInstance().screen;
        Minecraft.getInstance().setScreen(new OreUIExampleScreen(screen));
        //JavaLoader.INSTANCE.client().setScreen(OreUIExampleScreen::new);
        return 1;
    }
}
