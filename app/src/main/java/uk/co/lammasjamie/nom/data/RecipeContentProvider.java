package uk.co.lammasjamie.nom.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;

/**
 * Created by Jamie on 05/12/2017.
 */

public class RecipeContentProvider extends ContentProvider {

    // Define final integer constants for the directory of recipes and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int RECIPES = 100;
    public static final int RECIPES_WITH_ID = 101;

    // CDeclare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // Member variable for a RecipeDbHelper that's initialized in the onCreate() method
    private RecipeDbHelper mRecipeDbHelper;

    // Define a static buildUriMatcher method that associates URI's with their int match
    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeContract.AUTHORITY;

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the recipe directory and a single item by ID.
         */
        uriMatcher.addURI(authority, RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(authority, RecipeContract.PATH_RECIPES + "/#", RECIPES_WITH_ID);

        return uriMatcher;
    }



    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        // Complete onCreate() and initialize a RecipeDbHelper on startup
        // [Hint] Declare the DbHelper as a global variable

        Context context = getContext();
        mRecipeDbHelper = new RecipeDbHelper(context);
        return true;
    }


    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the recipe database (to write new data to)
        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the recipes directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case RECIPES:
                // Insert new values into the database
                // Inserting values into recipes table
                long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        // Query for the recipes directory and write a default case
        switch (sUriMatcher.match(uri)) {
            // Query for the recipes directory
            case RECIPES_WITH_ID:

                /*
                 * In order to determine the date associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 1472214172 and
                 * represents the number of seconds since the epoch, or UTC time.
                 */
                String idString = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionArguments = new String[]{idString};

                cursor =  mRecipeDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        RecipeContract.RecipeEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            case RECIPES:
                cursor = mRecipeDbHelper.getReadableDatabase().query(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return cursor;
    }


    // Implement delete to delete a single row of data
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Keep track of the number of deleted recipes
        int recipesDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (sUriMatcher.match(uri)) {
            // Handle the single item case, recognized by the ID included in the URI path
            case RECIPES_WITH_ID:
                // Get the recipe ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                recipesDeleted = mRecipeDbHelper.getWritableDatabase().delete(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (recipesDeleted != 0) {
            // A recipe was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of recipes deleted
        return recipesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        // Keep track of the number of deleted recipes
        int recipesUpdated; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (sUriMatcher.match(uri)) {
            // Handle the single item case, recognized by the ID included in the URI path
            case RECIPES_WITH_ID:
                // Get the recipe ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                recipesUpdated = mRecipeDbHelper.getWritableDatabase().update(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (recipesUpdated != 0) {
            // A recipe was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of recipes deleted
        return recipesUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
