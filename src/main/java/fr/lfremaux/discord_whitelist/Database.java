package fr.lfremaux.discord_whitelist;

import fr.lfremaux.dataManager.exceptions.InvalidAccessException;
import fr.lfremaux.dataManager.exceptions.ModuleNotInitializedException;
import fr.lfremaux.dataManager.mysql.MysqlAccess;
import fr.lfremaux.dataManager.mysql.MysqlManager;

import java.sql.Connection;
import java.sql.SQLException;

public enum Database {
    WHITELIST("whitelist");

    private MysqlManager mysql;
    private MysqlAccess access;

    Database(String accessName) {
        try {
            this.access = mysql.getAccess(accessName);
        } catch (InvalidAccessException e) {
            e.printStackTrace();
        }
    }

    {
        try {
            this.mysql = (MysqlManager) Whitelister.getInstance().getDataManager().getModule(MysqlManager.class);
        } catch (ModuleNotInitializedException e) {
            e.printStackTrace();
        }
    }

    public MysqlAccess getAccess() {
        return access;
    }

    public Connection getConnection() throws SQLException {
        return access.getConnection();
    }
}
