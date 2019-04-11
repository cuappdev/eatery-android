package com.cornellappdev.android.eatery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.ArrayList;

public class MessengerDialog extends DialogFragment {
  String[] allItems;

  public MessengerDialog() {
  }

  public static interface OnCompleteListener {
    public abstract void onComplete(ArrayList<String> selected);
  }

  private OnCompleteListener mListener;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    allItems = getArguments().getStringArray("allItems");
    ArrayList selectedItems = new ArrayList();
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle("Pick favorite food items!")
        .setMultiChoiceItems(allItems, null, new DialogInterface.OnMultiChoiceClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
              selectedItems.add(allItems[which]);
            } else if (selectedItems.contains(allItems[which])) {
              selectedItems.remove(allItems[which]);
            }
          }
        })
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mListener.onComplete(selectedItems);
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.e("FUCK", "cancel!");
          }
        });
    return builder.create();
  }

  // make sure the Activity implemented it
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      this.mListener = (OnCompleteListener)activity;
    }
    catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }
}