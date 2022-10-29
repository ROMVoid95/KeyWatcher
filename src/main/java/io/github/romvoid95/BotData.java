package io.github.romvoid95;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import io.github.romvoid95.database.DatabaseManager;
import io.github.romvoid95.database.Rethink;
import io.github.romvoid95.util.Factory;

public class BotData
{

    private static final ScheduledExecutorService exec = Factory.newScheduledThreadPool(1, "Galactic-Thread-%d", false);
    private static DatabaseManager                db;
 
    public static DatabaseManager database()
    {
        if (db == null)
        {
            db = new DatabaseManager(Rethink.connect());
        }

        return db;
    }

    public static ScheduledExecutorService executor()
    {
        return exec;
    }

    public static void queue(Callable<?> action)
    {
    	executor().submit(action);
    }

    public static void queue(Runnable runnable)
    {
    	executor().submit(runnable);
    }
}
