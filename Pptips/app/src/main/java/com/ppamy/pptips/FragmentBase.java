package com.ppamy.pptips;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class FragmentBase extends Fragment {
    protected SelectedFragmentInterface mSelectedFragmentInterface;

    public abstract String getTagText();

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof SelectedFragmentInterface)) {
            throw new ClassCastException("Hosting activity must implement fragmentBase");
        } else {
            mSelectedFragmentInterface = (SelectedFragmentInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mSelectedFragmentInterface.setSelectedFragment(this);
    }
    
    @Override
    public void onDetach() {
        mSelectedFragmentInterface.setSelectedFragment(null);
        super.onDetach();
    }

    public interface SelectedFragmentInterface {
        public void setSelectedFragment(FragmentBase fragmentBase);
    }
}   