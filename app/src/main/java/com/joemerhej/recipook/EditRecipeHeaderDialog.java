package com.joemerhej.recipook;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

/**
 * Created by Joe Merhej on 2/10/17.
 */

public class EditRecipeHeaderDialog extends DialogFragment
{
    // the activity that creates an instance of this dialog fragment must implement this.
    // each method passes the dialog fragment in case the host needs to query it.
    public interface DetailEditRecipeHeaderDialogListener
    {
        void onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog dialog);
        void onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog dialog);
        void onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog dialog);
    }

    // use this instance of the interface to deliver action events
    DetailEditRecipeHeaderDialogListener mListener;

    public Button mChooseImageButton;
    public TextInputEditText mNewRecipeTitleEditText;


    public EditRecipeHeaderDialog()
    {

    }

    public static EditRecipeHeaderDialog Instance(String recipeTitle)
    {
        EditRecipeHeaderDialog dialog = new EditRecipeHeaderDialog();
        Bundle args = new Bundle();
        args.putString("recipeTitle", recipeTitle);
        dialog.setArguments(args);
        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DetailEditRecipeHeaderDialogListener) context;
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_recipe_header, null);

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog.this);
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog.this);
                    }
                });


        // hook up the views
        mNewRecipeTitleEditText = (TextInputEditText) view.findViewById(R.id.dialog_title_edit_text);
        mNewRecipeTitleEditText.setText(getArguments().getString("recipeTitle"));
        mNewRecipeTitleEditText.setSelection(getArguments().getString("recipeTitle").length());

        // hook up the listener for the choose image button
        mChooseImageButton = (Button) view.findViewById(R.id.choose_image_button);
        mChooseImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog.this);
            }
        });

        return builder.create();
    }
}






















