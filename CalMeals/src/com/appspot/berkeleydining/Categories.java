/**
 * A class that acts as a component of all Meal Objects, listing
 * what types of foods are contained in the meal. 
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import java.util.ArrayList;
import java.util.List;

public class Categories {

    /** Categories of the menu. */
    public List<String> soupList = new ArrayList<String>();
    public List<String> pastaList = new ArrayList<String>();
    public List<String> mexicanList = new ArrayList<String>();
    public List<String> pizzaList = new ArrayList<String>();
    public List<String> asianList = new ArrayList<String>();
    public List<String> meatList = new ArrayList<String>();
    public List<String> veggieList = new ArrayList<String>();
    public List<String> dessertList = new ArrayList<String>();
    public List<String> beverageList = new ArrayList<String>();
    public List<String> breakfastList = new ArrayList<String>();
    public List<String> unsortedList = new ArrayList<String>();

    /** Merges the menu item lists into one big ArrayList */
    public ArrayList<String> mergeLists() {
        ArrayList<String> merged = new ArrayList<String>();
        if (!soupList.isEmpty()) {
            merged.add("Soups");
            merged.addAll(soupList);
        }

        if (!pastaList.isEmpty()) {
            merged.add("Pasta");
            merged.addAll(pastaList);
        }
        if (!mexicanList.isEmpty()) {
            merged.add("Mexican");
            merged.addAll(mexicanList);
        }
        if (!pizzaList.isEmpty()) {
            merged.add("Pizza");
            merged.addAll(pizzaList);
        }
        if (!asianList.isEmpty()) {
            merged.add("Asian");
            merged.addAll(asianList);
        }
        if (!meatList.isEmpty()) {
            merged.add("Meat");
            merged.addAll(meatList);
        }
        if (!veggieList.isEmpty()) {
            merged.add("Veggie");
            merged.addAll(veggieList);
        }
        if (!breakfastList.isEmpty()) {
            merged.add("Breakfast");
            merged.addAll(breakfastList);
        }
        if (!beverageList.isEmpty()) {
            merged.add("Beverages");
            merged.addAll(beverageList);
        }
        if (!dessertList.isEmpty()) {
            merged.add("Desserts");
            merged.addAll(dessertList);
        }
        if (!unsortedList.isEmpty()) {
            merged.add("Other");
            merged.addAll(unsortedList);
        }
        return merged;
    }

}
