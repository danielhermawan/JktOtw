package favesolution.com.jktotw.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toolbar;

public class HomeFragment extends Fragment {
    Toolbar mToolbar;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_home,container,false);
        Toolbar mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        return v;
    }*/


}
