package com.joemerhej.recipook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.attr.dialogTitle;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_0_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_1_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_2_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_3_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_4_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_5_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_6_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_7_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_8_layout;
import static com.joemerhej.recipook.R.id.dialog_edit_duration_number_9_layout;

/**
 * Created by Joe Merhej on 3/1/17.
 */


public class EditRecipeDurationsDialog extends DialogFragment
{
    // enum to set the dialog type
    public enum RecipeDurationDialogType
    {
        PREPARATION_TIME_DIALOG(0), // need to be 0 and 1 because we're using id as index when building the dialog in onCreate
        COOKING_TIME_DIALOG(1);

        private final int value;

        RecipeDurationDialogType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    // the activity that creates an instance of this dialog fragment must implement this.
    // each method passes the dialog fragment in case the host needs to query it or use its variables.
    public interface DetailEditRecipeDurationsDialogListener
    {
        void onEditDurationsDialogPositiveClick(EditRecipeDurationsDialog dialog, RecipeDurationDialogType dialogType);

        void onEditDurationsDialogNegativeClick(EditRecipeDurationsDialog dialog, RecipeDurationDialogType dialogType);

        void onEditDurationsDialogBackspaceClick(EditRecipeDurationsDialog dialog);

        void onEditHeaderDialogNumberClick(EditRecipeDurationsDialog dialog, int digit);
    }

    // use this instance of the interface to deliver action events
    DetailEditRecipeDurationsDialogListener mListener;

    // views of the dialog
    public TextView mHoursText;
    public TextView mMinutesText;
    public LinearLayout mBackSpaceLayout;
    public LinearLayout mNumber1Layout;
    public LinearLayout mNumber2Layout;
    public LinearLayout mNumber3Layout;
    public LinearLayout mNumber4Layout;
    public LinearLayout mNumber5Layout;
    public LinearLayout mNumber6Layout;
    public LinearLayout mNumber7Layout;
    public LinearLayout mNumber8Layout;
    public LinearLayout mNumber9Layout;
    public LinearLayout mNumber0Layout;

    public EditRecipeDurationsDialog()
    {

    }

    // instance puts parameters in an argument bundle in the dialog
    public static EditRecipeDurationsDialog Instance(RecipeDurationDialogType dialogType, String dialogTitle, int hoursToShow, int minutesToShow)
    {
        EditRecipeDurationsDialog dialog = new EditRecipeDurationsDialog();
        Bundle args = new Bundle();
        args.putInt("dialogType", dialogType.getValue());
        args.putString("dialogTitle", dialogTitle);
        args.putInt("hoursToShow", hoursToShow);
        args.putInt("minutesToShow", minutesToShow);
        dialog.setArguments(args);
        return dialog;
    }

    // override the Fragment.onAttach() method to instantiate the DetailEditRecipeDurationsDialogListener
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        // verify that the host activity implements the callback interface
        try
        {
            // instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DetailEditRecipeDurationsDialogListener) context;
        }
        catch (ClassCastException e)
        {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement DetailEditRecipeDurationsDialogListener");
        }
    }

    // when the dialog is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_recipe_durations, null);

        // hook up the  main view and the positive/negative buttons click listeners
        builder.setView(view)
                .setTitle(getArguments().getString("dialogTitle"))
                .setPositiveButton("Apply", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditDurationsDialogPositiveClick(EditRecipeDurationsDialog.this, RecipeDurationDialogType.values()[getArguments().getInt("dialogType")]);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mListener.onEditDurationsDialogNegativeClick(EditRecipeDurationsDialog.this, RecipeDurationDialogType.values()[getArguments().getInt("dialogType")]);
                    }
                });

        // hook up the child views
        mHoursText = (TextView) view.findViewById(R.id.dialog_edit_duration_hours);
        mMinutesText = (TextView) view.findViewById(R.id.dialog_edit_duration_minutes);
        mBackSpaceLayout = (LinearLayout) view.findViewById(R.id.dialog_edit_duration_backspace_layout);
        mNumber1Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_1_layout);
        mNumber2Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_2_layout);
        mNumber3Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_3_layout);
        mNumber4Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_4_layout);
        mNumber5Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_5_layout);
        mNumber6Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_6_layout);
        mNumber7Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_7_layout);
        mNumber8Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_8_layout);
        mNumber9Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_9_layout);
        mNumber0Layout = (LinearLayout) view.findViewById(dialog_edit_duration_number_0_layout);


        // set the time already set, also found in args
        setTimeInView(getArguments().getInt("hoursToShow"), getArguments().getInt("minutesToShow"));

        // hook up the listener for the backspace key
        mBackSpaceLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditDurationsDialogBackspaceClick(EditRecipeDurationsDialog.this);
            }
        });

        // hook up the numbers listeners
        mNumber1Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 1);
            }
        });
        mNumber2Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 2);
            }
        });
        mNumber3Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 3);
            }
        });
        mNumber4Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 4);
            }
        });
        mNumber5Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 5);
            }
        });
        mNumber6Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 6);
            }
        });
        mNumber7Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 7);
            }
        });
        mNumber8Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 8);
            }
        });
        mNumber9Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 9);
            }
        });
        mNumber0Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onEditHeaderDialogNumberClick(EditRecipeDurationsDialog.this, 0);
            }
        });


        return builder.create();
    }

    // method that will set the text of hours and minutes following the right format
    public void setTimeInView(int hours, int minutes)
    {
        mHoursText.setText(String.format("%02d", hours));
        mMinutesText.setText(String.format("%02d", minutes));
    }
}








































