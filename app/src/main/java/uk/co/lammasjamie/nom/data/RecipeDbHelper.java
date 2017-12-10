package uk.co.lammasjamie.nom.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jamie on 05/12/2017.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "recipesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;


    // Constructor
    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the recipes database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create recipes table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "             + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeEntry._ID                  + " INTEGER PRIMARY KEY, " +
                RecipeContract.RecipeEntry.COLUMN_DATE          + " INTEGER NOT NULL, "  +
                RecipeContract.RecipeEntry.COLUMN_NAME          + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_INGREDIENTS   + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_METHOD        + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
