package io.github.romvoid95.database;

import static io.github.romvoid95.database.Rethink.Rethink;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import com.rethinkdb.net.Connection;

import io.github.romvoid95.database.entity.DBUsers;

public class DatabaseManager
{

    private final Connection conn;

    public DatabaseManager(@Nonnull Connection conn)
    {
        this.conn = conn;
    }

    @Nonnull
    @CheckReturnValue
    public DBUsers usersDatabase()
    {
        DBUsers obj = Rethink.table(DBUsers.DB_TABLE).get("users").runAtom(conn, DBUsers.class);
        return obj == null ? DBUsers.create() : obj;
    }

    public void save(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "replace").runNoReply(conn);
    }

    public void saveUpdating(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).insert(object).optArg("conflict", "update").runNoReply(conn);
    }

    public void delete(@Nonnull ManagedObject object)
    {
        Rethink.table(object.getTableName()).get(object.getId()).delete().runNoReply(conn);
    }

    public Connection getConnection()
    {
        return conn;
    }
}
