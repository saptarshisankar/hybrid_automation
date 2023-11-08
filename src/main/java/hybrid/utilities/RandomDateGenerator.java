package hybrid.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RandomDateGenerator {
	 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	    public static String generateRandomBirthday(int minBirthYear, int maxBirthYear) {
	        // Get the current year
	        Calendar calendar = Calendar.getInstance();
	        int currentYear = calendar.get(Calendar.YEAR);

	        // Generate a random birth year within the specified range
	        int randomBirthYear = generateRandomNumber(minBirthYear, maxBirthYear);

	        // Generate random month (1 to 12)
	        int randomMonth = generateRandomNumber(1, 12);

	        // Generate random day (1 to 28/30/31 depending on the month)
	        int randomDay = generateRandomNumber(1, getMaxDay(randomBirthYear, randomMonth));

	        // Create a Date object with the random components
	        calendar.set(randomBirthYear, randomMonth - 1, randomDay);
	        Date randomBirthday = calendar.getTime();

	        // Format the Date object as a string
	        return dateFormat.format(randomBirthday);
	    }

	    private static int generateRandomNumber(int min, int max) {
	        Random random = new Random();
	        return random.nextInt(max - min + 1) + min;
	    }

	    private static int getMaxDay(int year, int month) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month - 1, 1);
	        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    }

	    public static void main(String[] args) {
	        int minBirthYear = 1900;
	        int maxBirthYear = 2000; // Change the range as needed
	        String randomBirthday = generateRandomBirthday(minBirthYear, maxBirthYear);
	        System.out.println("Random Birthday: " + randomBirthday);
	    }
}

