package uk.co.lammasjamie.nom;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

//import android.widget.Toast;

import uk.co.lammasjamie.nom.data.RecipeContract;
import uk.co.lammasjamie.nom.utilities.RecipeDateUtils;

/**
 * Created by Jamie on 05/12/2017.
 */

public class AddRecipeActivity extends AppCompatActivity {

    private Uri mUri;
    private static final int ID_DETAIL_LOADER = 253;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mUri = getIntent().getData();
        //if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        //getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }


    /**
     * onClickAddRecipe is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new recipe data into the underlying database.
     */
    public void onClickAddRecipe() {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String recipeNameInput = ((EditText) findViewById(R.id.editTextRecipeName)).getText().toString();
        String recipeIngredientsInput = ((EditText) findViewById(R.id.editTextRecipeIngredients)).getText().toString();
        String recipeMethodInput = ((EditText) findViewById(R.id.editTextRecipeMethod)).getText().toString();
        if (recipeNameInput.length() == 0) {
            return;
        }

        // Insert new recipe data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        //Get today's normalized date
        long today = RecipeDateUtils.normalizeDate(System.currentTimeMillis());

        // Put the recipe name, ingredients and method into the ContentValues
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_DATE, today);
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipeNameInput);
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS, recipeIngredientsInput);
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_METHOD, recipeMethodInput);


        if (mUri != null) {
            String id = mUri.getPathSegments().get(1);
            int updateBoolean = getContentResolver().update(RecipeContract.RecipeEntry.CONTENT_URI, contentValues, "_id=?",
                    new String[]{id});
        } else {
            // Insert the content values via a ContentResolver
            getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
        }

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
//        if(uri != null) {
//            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
//        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }


    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see android.app.Activity#onPrepareOptionsMenu(Menu)
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.add_recipe, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_add) {
            //change text view to edit text
            onClickAddRecipe();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
