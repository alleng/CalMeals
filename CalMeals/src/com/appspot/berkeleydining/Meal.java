	/**
	 * A class that groups the information of one meal (i.e. Breakfast at
	 * Foothill) together.  
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */

package com.appspot.berkeleydining;

public class Meal {
	public float rating = 0;
	public float userRating = 0; 
	public int number_ratings = 0;
	public Categories categories = new Categories();

	
	/**Returns the food Categories for this Meal */
	public Categories getCategories() {
		return categories;
	}
	
}