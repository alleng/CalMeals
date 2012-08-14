/**
 * A class that groups the information of all the Meals at a DiningHall
 * during a given day (i.e. Breakfast, lunch, dinner at Crossroads).
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

public class Meals {

    public Meal Breakfast = new Meal();
    public Meal Lunch = new Meal();
    public Meal Dinner = new Meal();
    public Meal LateNight = new Meal();

    /** Returns a Meal based on String mealName */
    public Meal getMeal(String mealName) {
        if (mealName.equals("Breakfast")) {
            return Breakfast;
        } else if (mealName.equals("Lunch")) {
            return Lunch;
        } else if (mealName.equals("Dinner")) {
            return Dinner;
        } else if (mealName.equals("LateNight")) {
            return LateNight;
        } else {
            return null;
        }
    }

}
