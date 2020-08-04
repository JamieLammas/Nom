package uk.co.lammasjamie.nom;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
//import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import uk.co.lammasjamie.nom.data.RecipeContract;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        RecipeAdapter.RecipeAdapterOnClickHandler {

    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * recipe data.
     */
    public static final String[] MAIN_RECIPE_PROJECTION = {
            RecipeContract.RecipeEntry._ID,
            RecipeContract.RecipeEntry.COLUMN_DATE,
            RecipeContract.RecipeEntry.COLUMN_NAME,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_DATE = 1;
    public static final int INDEX_RECIPE_NAME = 2;

    private static final int ID_RECIPE_LOADER = 44;

    // Member variables for the adapter and RecyclerView
    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().setElevation(0f);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewRecipes);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // Initialize the adapter and attach it to the RecyclerView
        mRecipeAdapter = new RecipeAdapter(this, this);

        mRecyclerView.setAdapter(mRecipeAdapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the recipe to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all recipes after a deletion
                getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null, MainActivity.this);

                setRecyclerView();

            }

        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddRecipeActivity.
         */
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddRecipeActivity
                Intent addRecipeIntent = new Intent(MainActivity.this, AddRecipeActivity.class);
                startActivity(addRecipeIntent);
            }
        });

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);

        setRecyclerView();
    }

    public void setRecyclerView() {
        ViewSwitcher switcherMain = (ViewSwitcher) findViewById(R.id.homeSwitcher);

        if (mRecipeAdapter.getItemCount() != 0) {
            switcherMain.showNext();
        }
    }


    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddRecipeActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all recipes
        getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null, this);
    }


    /**
     * Instantiates and returns a new AsyncRecipeLoader with the given ID.
     * This loader will return recipe data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_RECIPE_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = RecipeContract.RecipeEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = RecipeContract.RecipeEntry.COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                //String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_RECIPE_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mRecipeAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) mRecyclerView.setVisibility(View.VISIBLE);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipeAdapter.swapCursor(null);
    }


    /**
     * This method is for responding to clicks from our list.
     *
     * @param id Normalized UTC time that represents the local date of the weather in GMT time.
     * @see RecipeContract.RecipeEntry#COLUMN_DATE
     */
    @Override
    public void onClick(long id) {
        Intent recipeDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForRecipeClicked = RecipeContract.RecipeEntry.buildRecipeUriWithId(id);
        //Toast.makeText(this, "Uri: " + uriForRecipeClicked, Toast.LENGTH_LONG).show();
        recipeDetailIntent.setData(uriForRecipeClicked);
        startActivity(recipeDetailIntent);
    }
}
