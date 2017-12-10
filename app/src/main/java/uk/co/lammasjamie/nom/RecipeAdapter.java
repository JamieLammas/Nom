package uk.co.lammasjamie.nom;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.lammasjamie.nom.data.RecipeContract;
import uk.co.lammasjamie.nom.utilities.RecipeDateUtils;

/**
 * Created by Jamie on 05/12/2017.
 */

class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private RecipeAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeAdapterOnClickHandler {
        void onClick(long id);
    }


    /**
     * Constructor for the RecipeAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public RecipeAdapter(@NonNull Context mContext, RecipeAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        mClickHandler = clickHandler;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_layout, parent, false);

        return new RecipeViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getInt(MainActivity.INDEX_RECIPE_ID);
        String nameIndex = mCursor.getString(MainActivity.INDEX_RECIPE_NAME);
        /* Read date from the cursor */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_RECIPE_DATE);


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        String dateString = formatter.format(new Date(dateInMillis));


        holder.itemView.setTag(idIndex);
        holder.recipeNameView.setText(nameIndex);
         /* Display friendly date string */
        holder.recipeDateView.setText(dateString);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    // Inner class for creating ViewHolders
    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        final TextView recipeNameView;
        final TextView recipeDateView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        RecipeViewHolder(View itemView) {
            super(itemView);

            recipeNameView = (TextView) itemView.findViewById(R.id.recipeNameTextView);
            recipeDateView = (TextView) itemView.findViewById(R.id.recipeDateTextView);

            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long recipeId = mCursor.getLong(MainActivity.INDEX_RECIPE_ID);
            mClickHandler.onClick(recipeId);
        }
    }
}
