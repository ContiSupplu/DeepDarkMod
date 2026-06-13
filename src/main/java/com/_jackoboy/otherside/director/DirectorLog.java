package com._jackoboy.otherside.director;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DirectorLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(ServerLevel level, String eventId, BlockPos pos, String detail) {
        if (!OthersideConfig.SERVER.directorLogEnabled.get()) return;

        long gameDay = level.getDayTime() / 24000;
        long gameTime = level.getDayTime();
        String dimension = level.dimension().location().toString();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        String line = String.format("%s,%d,%d,%s,%s,%d,%d,%d,%s",
                timestamp, gameDay, gameTime, eventId, dimension,
                pos.getX(), pos.getY(), pos.getZ(), detail);

        // Write to file
        try {
            Path logPath = level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                    .resolve("otherside_director_log.csv");
            File file = logPath.toFile();
            boolean isNew = !file.exists();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                if (isNew) {
                    writer.println("real_timestamp,game_day,game_time,event_id,dimension,x,y,z,detail");
                }
                writer.println(line);
            }
        } catch (IOException e) {
            OthersideMod.LOGGER.error("Failed to write director log", e);
        }

        // Chat feed to ops
        if (OthersideConfig.SERVER.directorChatFeed.get()) {
            Component message = Component.literal("[DIR] ")
                    .withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(eventId + " ")
                            .withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("@ " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
                            .withStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GRAY)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))))
                    .append(Component.literal(" (" + detail + ")")
                            .withStyle(ChatFormatting.DARK_GRAY));

            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                if (player.hasPermissions(2)) {
                    player.sendSystemMessage(message);
                }
            }
        }
    }
}
