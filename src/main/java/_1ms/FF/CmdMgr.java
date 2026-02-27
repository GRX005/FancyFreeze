/*
    This file is part of the FancyFreeze project, licensed under the
    GNU General Public License v3.0

    Copyright (C) 2024-2026 _1ms (GRX005)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package _1ms.FF;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static _1ms.FF.CfgMgr.RELOAD;
import static _1ms.FF.CfgMgr.load;
import static _1ms.FF.EventMgr.freezed;

public class CmdMgr {

    public static LiteralCommandNode<CommandSourceStack> freezeCmd() {
        return Commands.literal("freeze")
                .requires(stack->stack.getSender().hasPermission(Perm.Freeze.s))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .suggests((ctx, bld)-> {//Cant be async :(
                            var snd = ctx.getSource().getSender();
                            Bukkit.getOnlinePlayers().stream().filter(p->p!=snd&&!p.hasPermission(Perm.Bypass.s)&&!freezed.contains(p.getUniqueId())).map(Player::getName).forEach(bld::suggest);
                            return bld.buildFuture();
                        })
                        .executes(ctx-> {
                            var src = ctx.getSource();
                            final var target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(src).getFirst();
                            Freezer.freeze(src.getSender(), target);
                            return Command.SINGLE_SUCCESS;
                        })).build();

    }

    public static LiteralCommandNode<CommandSourceStack> freezeAllCmd() {
        return Commands.literal("freezeall")
                .requires(stack->stack.getSender().hasPermission(Perm.FreezeAll.s))
                .executes(ctx -> {
                    Freezer.freeze(ctx.getSource().getSender(), null);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    public static LiteralCommandNode<CommandSourceStack> unFreezeCmd() {
        return Commands.literal("unfreeze")
                .requires(stack->stack.getSender().hasPermission(Perm.Freeze.s))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .suggests((ctx, bld)-> {
                            if (!freezed.isEmpty())
                                Bukkit.getOnlinePlayers().stream().filter(p->freezed.contains(p.getUniqueId())).map(Player::getName).forEach(bld::suggest);
                            return bld.buildFuture();
                        })
                        .executes(ctx-> {
                            var src = ctx.getSource();
                            final var target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(src).getFirst();
                            Freezer.unFreeze(src.getSender(), target);
                            return Command.SINGLE_SUCCESS;
                        })).build();
    }

    public static LiteralCommandNode<CommandSourceStack> unFreezeAllCmd() {
        return Commands.literal("unfreezeall")
                .requires(stack->stack.getSender().hasPermission(Perm.FreezeAll.s))
                .executes(ctx -> {
                    Freezer.unFreeze(ctx.getSource().getSender(), null);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    public static LiteralCommandNode<CommandSourceStack> reloadCmd() {
        return Commands.literal("freeze-rl")
                .requires(stack->stack.getSender().hasPermission(Perm.Reload.s))
                .executes(ctx -> {
                    Main.pl.reloadConfig();
                    load();
                    ctx.getSource().getSender().sendMessage(RELOAD);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

}
