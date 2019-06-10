package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ShopFragment extends Fragment {
    private ViewCurrentItemsListener activityCommander;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activityCommander = (ViewCurrentItemsListener) getActivity(context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_shop, null);

        Button button = (Button) view.findViewById(R.id.view_current_items_button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCommander.viewCurrentItems();
            }
        };
        button.setOnClickListener(listener);

        return view;
    }

    // how to get activity from context
    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    // listener interface to view current items
    public interface ViewCurrentItemsListener {
        public void viewCurrentItems();
    }
}
