package com.appspot.berkeleydining;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.viewpagerindicator.TitlePageIndicator;

public class MenusFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryList = new ArrayList<String>();
        fragmentManager = getFragmentManager();
        fillCategoryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.foodactivity_layout, container, false);

        final MyPagerAdapter adapter = new MyPagerAdapter();
        final ViewPager myPager = (ViewPager) v.findViewById(R.id.myPagerElement);
        myPager.setAdapter(adapter);
        TitlePageIndicator tabIndicator = (TitlePageIndicator) v.findViewById(R.id.tabs);
        tabIndicator.setViewPager(myPager);
        tabIndicator.setTextColor(Color.GRAY);
        tabIndicator.setSelectedColor(Color.BLACK);
        tabIndicator.setCurrentItem(1);
        tabIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((MenuActivity) getActivity()).setCurrentHall(CurrentMenu.halls.Foothill);
                } else if (position == 1) {
                    ((MenuActivity) getActivity()).setCurrentHall(CurrentMenu.halls.Crossroads);
                } else if (position == 2) {
                    ((MenuActivity) getActivity()).setCurrentHall(CurrentMenu.halls.Cafe3);
                } else if (position == 3) {
                    ((MenuActivity) getActivity()).setCurrentHall(CurrentMenu.halls.ClarkKerr);
                }

                if (!((MenuActivity) getActivity()).getCurrentMeal().equals("LateNight")) {
                    Handler refresh = new Handler();
                    refresh.post(new Runnable() {
                        @Override
                        public void run() {
                            Fragment f = fragmentManager.findFragmentById(R.id.BottomFrag);
                            Fragment f2 = new RatingBarFragment();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            ft.remove(f);
                            ft.add(R.id.BottomFrag, f2);
                            ft.commit();
                        }
                    });

                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel) {

            }

        });
        return v;
    }

    private void fillCategoryList() {
        categoryList.add("Soups");
        categoryList.add("Pasta");
        categoryList.add("Mexican");
        categoryList.add("Pizza");
        categoryList.add("Asian");
        categoryList.add("Meat");
        categoryList.add("Veggie");
        categoryList.add("Breakfast");
        categoryList.add("Desserts");
        categoryList.add("Beverages");
        categoryList.add("Other");
        categoryList.add("Closed");
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            Meal displayMeal = null;
            Meals currentHall = null;
            int resId = 0;
            View view = null;
            ListView lv = null;
            ArrayList<String> menu = null;
            MyCustomAdapter custAd = null;
            String currentMeal = ((MenuActivity) getActivity()).getCurrentMeal();
            switch (position) {
            case 0:
                resId = R.layout.tab_frag1_layout;
                view = inflater.inflate(resId, null);
                currentHall = CurrentMenu.halls.Foothill;
                break;
            case 1:
                resId = R.layout.tab_frag1_layout;
                view = inflater.inflate(resId, null);
                currentHall = CurrentMenu.halls.Crossroads;
                break;
            case 2:
                resId = R.layout.tab_frag1_layout;
                view = inflater.inflate(resId, null);
                currentHall = CurrentMenu.halls.Cafe3;
                break;
            case 3:
                resId = R.layout.tab_frag1_layout;
                view = inflater.inflate(resId, null);
                currentHall = CurrentMenu.halls.ClarkKerr;
                break;
            }

            if (currentMeal.equals("Breakfast")) {
                displayMeal = currentHall.Breakfast;
            } else if (currentMeal.equals("Lunch")) {
                displayMeal = currentHall.Lunch;
            } else if (currentMeal.equals("Dinner")) {
                displayMeal = currentHall.Dinner;
            } else if (currentMeal.equals("LateNight")) {
                displayMeal = currentHall.LateNight;
            }

            lv = (ListView) view.findViewById(R.id.listspace);
            menu = displayMeal.categories.mergeLists();
            if (menu.isEmpty()) {
                menu.add("Closed");
            }
            custAd = new MyCustomAdapter(getActivity().getApplicationContext(),
                    R.layout.category_item, menu);

            lv.setAdapter(custAd);

            ((ViewPager) collection).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public String getPageTitle(int position) {
            String[] titles = { "Foothill", "Crossroads", "Cafe 3", "Clark Kerr" };
            return titles[position];
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mData = new ArrayList<String>();

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            mData = objects;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder itemHolder = null;
            itemHolder = new ViewHolder();

            LayoutInflater inflater = getActivity().getLayoutInflater();
            if (categoryList.contains(mData.get(position))) {
                row = inflater.inflate(R.layout.food_item, parent, false);
            } else {
                row = inflater.inflate(R.layout.category_item, parent, false);
            }
            row.setTag(itemHolder);

            itemHolder.textView = (TextView) row.findViewById(R.id.rowTextView);

            itemHolder.textView.setText(mData.get(position));

            return row;

        }
    }

    public static class ViewHolder {
        public TextView textView;
    }

    ArrayList<String> categoryList;
    FragmentManager fragmentManager;

}
