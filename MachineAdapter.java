package com.example.keenan.scanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {
    private HashMap mDataset;
    private PassDataToActivity mCallback;
    Set<String> setOfKeys;
    String[] arrayOfKeys;

    //this interface is implemented to the activity in which you wish to pass data to.
    public interface PassDataToActivity {
        void PassData (HashMap dataSet);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    //@param: the data set for machine objects in MAP
    //@param: listener takes 'this' from activity, and is used to call PassData
    public MachineAdapter(HashMap myDataset, PassDataToActivity listener) {
        mCallback = listener;
        mDataset = myDataset;

        //grabs a list of keys from teh hashmap
        setOfKeys = (Set) mDataset.keySet();
        arrayOfKeys = setOfKeys.toArray(new String[setOfKeys.size()]);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView mTextView;
        public final EditText mEditText;
        public EditTextListener editTextListener;

        public ViewHolder(View view, EditTextListener editTextListener) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.tv_item);
            this.mEditText = (EditText) view.findViewById(R.id.et_item);
            this.editTextListener = editTextListener;
            this.mEditText.addTextChangedListener(editTextListener);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create a new view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_2_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ViewHolder(view, new EditTextListener());
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String machineElement = arrayOfKeys[position];
        holder.mTextView.setText(machineElement);
        holder.editTextListener.updatePosition(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (null == arrayOfKeys) return 0;

        return arrayOfKeys.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class EditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                mDataset.put(arrayOfKeys[position], charSequence.toString());
                System.out.println("ontextchanged: " + mDataset);
                //this line passes data to activity2
                mCallback.PassData(mDataset);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }


}
