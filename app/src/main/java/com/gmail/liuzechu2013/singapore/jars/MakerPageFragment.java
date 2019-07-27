package com.gmail.liuzechu2013.singapore.jars;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MakerPageFragment extends Fragment {

    int mNum;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static MakerPageFragment newInstance(int num) {
        MakerPageFragment f = new MakerPageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        if (mNum == 0) {
            v = inflater.inflate(R.layout.fragment_maker_ordinary, container, false);
        } else if (mNum == 1) {
            v = inflater.inflate(R.layout.fragment_maker_grand, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_maker_deluxe, container, false);
        }
        return v;
    }
}
