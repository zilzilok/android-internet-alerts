package ru.zilzilok.ict.utils.database;

/**
 * Singleton class for database.
 */
public class Databases {
    private static final Databases instance = new Databases();

    public StatisticsDBHelper statisticsDBHelper;

    private Databases() {
        statisticsDBHelper = new StatisticsDBHelper();
    }

    public static Databases getInstance() {
        return instance;
    }
}
