package com.joemerhej.recipook;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Joe Merhej on 2/10/17.
 */

public class EditRecipeHeaderDialog extends DialogFragment
{
    // the activity that creates an instance of this dialog fragment must implement this.
    // each method passes the dialog fragment in case the host needs to query it or use its variables.
    public interface DetailEditRecipeHeaderDialogListener
    {
        void onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog dialog);

        void onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog dialog);

        void onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog dialog);

        void onEditHeaderDialogRemoveImageClick(EditRecipeHeaderDialog dialog);
    }

    // use this instance of the interface to deliver action events
    DetailEditRecipeHeaderDialogListener mListener;

    // views of the dialog
    public ImageView mRecipeImageView;
    public Button mChooseImageButton;
    public ImageButton mRemoveImageButton;
    public TextInputEditText mRecipeNameEditText;


    public EditRecipeHeaderDialog()
    {

    }

    // instance puts the parameters in an argument bundle in the dialog
    public static EditRecipeHeaderDialog Instance(String recipeTitle, String recipeImageUri)
    {
        EditRecipeHeaderDialog dialog = new EditRecipeHeaderDialog();
        Bundle args = new Bundle();
        args.putString("recipeTitle", recipeTitle);
        args.putString("mRecipeImage", recipeImageUri);
        dialog.setArguments(args);
        return dialog;
    }

    // override the Fragment.onAttach() method to instantiate the DetailEditRecipeHeaderDialogListener
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        // verify that the host activity implements the callback interface
        try
        {
            // instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DetailEditRecipeHeaderDialogListener) context;
        }
        catch (ClassCastException e)
        {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement DetailEditRecipeHeaderDialogListener");
        }
    }

    // same onAttach() as above but with different function declaration to support older SDKs
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            // verify that the host activity implements the callback interface
            try
            {
                // instantiate the NoticeDialogListener so we can send events to the host
                mListener = (DetailEditRecipeHeaderDialogListener) activity;
            }
            catch (ClassCastException e)
            {
                // the activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString() + " must implement DetailEditRecipeHeaderDialogListener");
            }
        }
    }

    // when the dialog is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_recipe_header, null);

        // hook up the  main view and the positive/negative buttons click listeners
        builder.setView(view)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog.this);
                    }
                });


        // hook up the child views
        mRecipeNameEditText = (TextInputEditText) view.findViewById(R.id.dialog_edit_header_recipe_name_edit_text);
        mChooseImageButton = (Button) view.findViewById(R.id.dialog_edit_header_edit_image_button);
        mRemoveImageButton = (ImageButton) view.findViewById(R.id.dialog_edit_header_remove_image_button);
        mRecipeImageView = (ImageView) view.findViewById(R.id.dialog_edit_header_recipe_image_view);

        // set the text to the recipe name that is in the arguments of the dialog instance called
        mRecipeNameEditText.setText(getArguments().getString("recipeTitle"));
        mRecipeNameEditText.setSelection(getArguments().getString("recipeTitle").length());

        // set the image view to the recipe image view (also from arguments of instance)
        Glide.with(builder.getContext())
                .load(Uri.parse(getArguments().getString("mRecipeImage")))
                .into(mRecipeImageView);

        // hook up the listener for the choose image button
        mChooseImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog.this);
            }
        });

        // hook up the listener for the remove image button
        mRemoveImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogRemoveImageClick(EditRecipeHeaderDialog.this);
            }
        });

        return builder.create();
    }
}






















