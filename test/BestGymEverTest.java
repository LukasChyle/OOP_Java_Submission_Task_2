import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class BestGymEverTest {

    BestGymEver gym = new BestGymEver();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    void verifyInputString() {
        assertEquals("1234561234", gym.verifyInput("1234561234"));
        assertEquals("1234561234", gym.verifyInput("   1234561234   "));
        assertEquals("förnamn efternamn", gym.verifyInput("förnamn efternamn"));
        assertEquals("förnamn efternamn", gym.verifyInput("  förnamn   efternamn  "));

        assertNull(gym.verifyInput("onlyOneWord"));
        assertNull(gym.verifyInput("one word toMuch"));
        assertNull(gym.verifyInput("2564 numbers"));
        assertNull(gym.verifyInput("12345678912"));
        assertNull(gym.verifyInput("123456-1234"));
        assertNull(gym.verifyInput("  123456 1234  "));
        assertNull(gym.verifyInput("letters123"));
    }

    @Test
    void searchFile() {
        // Creates a file that can be tested on.
        String testData =
                "   sdgdsg \n" +
                "            " +
                "   7502031234,   Test Andersson\n" +
                "2022-05-03\n" +
                "2022-05-03\n" +
                "8505132345, Test Persson\n" +
                "   2019-12-29";

        try (BufferedWriter output = new BufferedWriter(new FileWriter("testFile.txt"))) {
            output.write(testData);
            output.close();

            // searches a person in the test file through the method and checks that the info retrieved is correct.
            String[] test = gym.searchFile("testFile.txt", "TEST PERSSON");
            assertEquals(test[0], "Test Persson");
            assertEquals(test[1], "8505132345");
            assertEquals(test[2], "2019-12-29");

            test = gym.searchFile("testFile.txt", "7502031234");
            assertEquals(test[0], "Test Andersson");
            assertEquals(test[1], "7502031234");
            assertEquals(test[2], "2022-05-03");
            assertNotEquals(test[1], "8505132345");

            assertNull(gym.searchFile("testFile.txt", "0000000000"));
            assertNull(gym.searchFile("testFile.txt", "Anna Persson"));

            assertThrows(IOException.class,
                    () -> gym.searchFile("wrongFile.txt", "8505132345"));


            // deletes the test file
            File testFile = new File("testFile.txt");
            if (testFile.delete()) {
                System.out.println("Deleted the test file \"testFile.txt\"");
            } else {
                System.out.println("Failed to delete the test file \"testFile.txt\"");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkValidityDate() {
        LocalDate date1 = LocalDate.now().minusYears(1);
        LocalDate date2 = LocalDate.now().minusYears(1).minusDays(1);

        try {
            // checks if method returns true if date is exactly one year, and false if one year and one day old.
            assertTrue(gym.checkValidityDate(date1.toString()));
            assertFalse(gym.checkValidityDate(date2.toString()));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        assertThrows(DateTimeParseException.class,
                () -> gym.checkValidityDate("notADate"));
    }


    @Test
    void writeVisitLog() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().minusDays(1);

        String[] person = {"Test Person", "1234567891", "Validity date not relevant"};
        try {
            gym.writeVisitLog(person); // writes a test file with the method
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;

        // opens the test file that was created with the method to check that it exists.
        try (BufferedReader test = new BufferedReader(new FileReader("Test Person 1234567891.txt"))) {
            line = test.readLine();
            test.close();

            // checks that the last entry of the test file has the right text.
            assertEquals(line, "Test Person , 1234567891 , " + time1.format(timeFormatter));

            assertNotEquals(line, "Person Test , 1234567891 , " + time1.format(timeFormatter));
            assertNotEquals(line, "Test Person , 1234567891 , " + time2.format(timeFormatter));

            // deletes the test file
            File testFile = new File("Test Person 1234567891.txt");
            if (testFile.delete()) {
                System.out.println("Deleted the test file \"Test Person 1234567891.txt\"");
            } else {
                System.out.println("Failed to delete the test file \"Test Person 1234567891.txt\"");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}