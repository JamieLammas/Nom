package uk.co.lammasjamie.nom;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.ViewSwitcher;


import uk.co.lammasjamie.nom.data.RecipeContract;
import uk.co.lammasjamie.nom.databinding.ActivityDetailBinding;
import uk.co.lammasjamie.nom.utilities.RecipeDateUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String recipeName;
    private String recipeIngredients;
    private String recipeMethod;
    private Menu mMenu;

    /*
     * The columns of data that we are interested in displaying within our DetailActivity's
     * weather display.
     */
    public static final String[] RECIPE_DETAIL_PROJECTION = {
            //RecipeContract.RecipeEntry.COLUMN_DATE,
            RecipeContract.RecipeEntry.COLUMN_NAME,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENTS,
            RecipeContract.RecipeEntry.COLUMN_METHOD
    };

    //public static final int INDEX_RECIPE_DATE = 0;
    public static final int INDEX_RECIPE_NAME = 0;
    public static final int INDEX_RECIPE_INGREDIENTS = 1;
    public static final int INDEX_RECIPE_METHOD = 2;

    /*
     * This ID will be used to identify the Loader responsible for loading the weather details
     * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
     * our case, there is only one. We will still use this ID to initialize the loader and create
     * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
     * whatever number you like, so long as it is unique and consistent.
     */
    private static final int ID_DETAIL_LOADER = 353;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mRecipeSummary;

    /* The URI that is used to access the chosen day's weather details */
    private Uri mUri;

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityDetailBinding mDetailBinding;
    private FloatingActionButton fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddRecipeActivity.
         */
        fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddRecipeActivity
                editRecipe();
            }
        });


        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    /**
     * Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param loaderArgs Any arguments supplied by the caller
     *
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        RECIPE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the details of the weather on the date they
     * selected from the forecast.
     *
     * @param loader The cursor loader that finished.
     * @param data   The cursor that is being returned.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }


        /***********************
         * Recipe Name *
         ***********************/

        recipeName = data.getString(INDEX_RECIPE_NAME);
        recipeIngredients = data.getString(INDEX_RECIPE_INGREDIENTS);
        recipeMethod = data.getString(INDEX_RECIPE_METHOD);

        mDetailBinding.textViewRecipeName.setText(recipeName);
        mDetailBinding.textViewRecipeIngredients.setText(recipeIngredients);
        mDetailBinding.textViewRecipeMethod.setText(recipeMethod);
//
//        /* Set the content description on the weather image (for accessibility purposes) */
//        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);


    }

    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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

        mMenu = menu;
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        if (fabButton.getVisibility() == View.INVISIBLE){
            inflater.inflate(R.menu.add_recipe, menu);
        }
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
            onClickUpdateRecipe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editRecipe() {

        fabButton.setVisibility(View.INVISIBLE);
        onCreateOptionsMenu(mMenu);

        ViewSwitcher switcherName = (ViewSwitcher) findViewById(R.id.viewSwitcherRecipeName);
        ViewSwitcher switcherIngredients = (ViewSwitcher) findViewById(R.id.viewSwitcherRecipeIngredients);
        ViewSwitcher switcherMethod = (ViewSwitcher) findViewById(R.id.viewSwitcherRecipeMethod);

        switcherName.showNext(); //or switcher.showPrevious();
        switcherIngredients.showNext();
        switcherMethod.showNext();

        TextView recipeNameTV = (TextView) switcherName.findViewById(R.id.textViewRecipeName);
        TextView recipeIngredientsTV = (TextView) switcherName.findViewById(R.id.textViewRecipeIngredients);
        TextView recipeMethodTV = (TextView) switcherName.findViewById(R.id.textViewRecipeMethod);

        EditText switchRecipeName = (EditText) switcherName.findViewById(R.id.editTextRecipeName);
        EditText switchRecipeIngredients = (EditText) switcherIngredients.findViewById(R.id.editTextRecipeIngredients);
        EditText switchRecipeMethod = (EditText) switcherMethod.findViewById(R.id.editTextRecipeMethod);


        switchRecipeName.setText(recipeName);
        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            switchRecipeIngredients.setText(recipeIngredients);
        } else {
            switchRecipeIngredients.setHint("Ingredients...");
        }

        if (recipeMethod != null && !recipeMethod.isEmpty()) {
            switchRecipeMethod.setText(recipeMethod);
        } else {
            switchRecipeMethod.setHint("Method...");
        }



    }


    /**
     * onClickAddRecipe is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new recipe data into the underlying database.
     */
    public void onClickUpdateRecipe() {
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


        String id = mUri.getPathSegments().get(1);
        int updateBoolean = getContentResolver().update(mUri,
                contentValues,
                "_id=?",
                new String[]{id});


        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
//        if(updateBoolean != -1) {
//            Toast.makeText(getBaseContext(), mUri.toString(), Toast.LENGTH_LONG).show();
//        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }

}
