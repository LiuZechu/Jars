package com.gmail.liuzechu2013.singapore.jars;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FilesFragment extends Fragment {
    private OnFileOpenListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnFileOpenListener) getActivity(context);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_files, null);
        Button button = view.findViewById(R.id.open_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openFile();
            }
        });

        // button to watch tutorial from an external link
        Button watchTutorialButton = view.findViewById(R.id.watch_tutorial_button);
        watchTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchTutorial();
            }
        });

        return view;
    }

    public interface OnFileOpenListener{
        void openFile();
    }

    // directs the user to an external link to watch our tutorial
    private void watchTutorial() {
        // TODO: change the URL to an actual video
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
        startActivity(browseIntent);
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
}
