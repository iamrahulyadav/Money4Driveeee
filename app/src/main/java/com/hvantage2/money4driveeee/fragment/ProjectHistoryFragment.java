package com.hvantage2.money4driveeee.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.util.FragmentIntraction;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hvantage2 on 2018-02-20.
 */

public class ProjectHistoryFragment extends Fragment {
     private View rootView;
     Context context;
    PagerAdapter adapter;
    FragmentIntraction intraction;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     rootView = inflater.inflate(R.layout.project_history_fragment,container,false);
     setHasOptionsMenu(true);
        context = getActivity();
        if (intraction != null) {
            intraction.actionbarsetTitle("Project History");
        }
        setTabLayout(rootView);

        return rootView;
    }

    private void setTabLayout(View rootView) {
        adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(new HistoryCompletedFragment(), "Completed");
        adapter.addFragment(new HistoryPendingFragment(), "Pending");

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> tabTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

        public void addFragment(Fragment fragment, String tabTitle) {
            fragments.add(fragment);
            tabTitles.add(tabTitle);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentIntraction) {
            intraction = (FragmentIntraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        intraction = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_home){
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_frame, new HomeFragment());
            ft.commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }
}
