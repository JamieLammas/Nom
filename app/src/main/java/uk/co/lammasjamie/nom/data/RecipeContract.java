package uk.co.lammasjamie.nom.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jamie on 05/12/2017.
 */

public class RecipeContract {

    /* Add content provider constants to the Contract
     Clients need to know how to access the recipe data, and it's your job to provide
     these content URI's for the path to that data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the recipes directory
        4) Content URI for data in the RecipeEntry class
      */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "uk.co.lammasjamie.nom";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "recipes" directory
    public static final String PATH_RECIPES = "recipes";

    /* RecipeEntry is an inner class that defines the contents of the recipe table */
    public static final class RecipeEntry implements BaseColumns {

        // RecipeEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();


        // Recipe table and column names
        public static final String TABLE_NAME = "recipes";

        // Since RecipeEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_METHOD = "method";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param id of recipe
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildRecipeUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        recipes
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | _id  |       name         |     ingredients     |     method      |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |  1   |    Pesto pasta     |     Pasta, pesto    |  Cook pasta...  |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |  2   |  Blueberry muffin  | Flour, blueberries  | Add flour with..|
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | 43   |      Tinola        |       Chicken       |   Cook chicken  |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}
