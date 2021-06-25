package fr.lfremaux.discord_whitelist;

import fr.lfremaux.queryBuilder.exceptions.DatabaseConnectionException;
import fr.lfremaux.queryBuilder.objects.Query;
import fr.lfremaux.queryBuilder.objects.result.QueryResultSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhitelistRunnable implements Runnable {

    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public WhitelistRunnable(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 20L * 60);
    }

    @Override
    public void run() {
        try {
            final QueryResultSet rs = new Query(Database.WHITELIST.getConnection(), true)
                    .from("pending_whitelist")
                    .select("uuid", "discord_id")
                    .execute();

            while (rs.next()) {
                final UUID uuid = UUID.fromString(rs.getString("uuid"));
                final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                Bukkit.getWhitelistedPlayers().add(player);

                Bukkit.getLogger().info("Player " + uuid + " added to the server whitelist");

                final String discordId = rs.getString("discord_id");

                pool.execute(() -> {
                    try {
                        new Query(Database.WHITELIST.getConnection(), true)
                                .from("registered_users")
                                .insert("discord_id", discordId)
                                .insert("uuid", uuid.toString())
                                .execute();

                        new Query(Database.WHITELIST.getConnection(), true)
                                .from("pending_whitelist")
                                .where("uuid", uuid.toString())
                                .delete();
                    } catch (DatabaseConnectionException | SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (DatabaseConnectionException | SQLException e) {
            e.printStackTrace();
        }
    }
}
