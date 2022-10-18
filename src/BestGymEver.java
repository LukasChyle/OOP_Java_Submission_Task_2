import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BestGymEver {

    public void dialog() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    null, "Enter first and last name or 10 digit PIN",
                    "Member Finder, Best Gym Ever", JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return;
            }

            input = verifyInput(input);
            if (input == null) {
                JOptionPane.showMessageDialog(null, "Invalid input");
                continue;
            }

            String[] person;
            try {
                person = searchFile("medlemmar.txt", input);
                if (person == null) {
                    JOptionPane.showMessageDialog(null, "Not found in registry , " + input);
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "File of members was not found");
                continue;
            } catch (NullPointerException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "File with members is not arranged correctly");
                continue;
            }

            try {
                if (!checkValidityDate(person[2])) {
                    JOptionPane.showMessageDialog(null, "Expired Member:  " +
                            person[0] + " , " + person[1] + " , " + person[2]);
                    continue;
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Format of date in file not valid");
                continue;
            }

            JOptionPane.showMessageDialog(null, "Current Member:  " +
                    person[0] + " , " + person[1] + " , " + person[2]);

            try {
                writeVisitLog(person);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to register visit to activity file");
            }
        }
    }

    // Verifies that name or pin is in correct format and removes whitespaces
    public String verifyInput(String input) {
        input = input.trim();
        // tests if input is a valid PIN
        if (input.length() == 10 && input.chars().allMatch(Character::isDigit)) {
            return input;
        }
        // tests if input is a valid name
        if (input.contains(" ")) {
            String firstName = input.substring(0, input.indexOf(" "));
            String lastName = input.substring(input.indexOf(" ")).trim();

            if ((!firstName.isBlank() && firstName.chars().allMatch(Character::isLetter)) &&
                    (!lastName.isBlank() && lastName.chars().allMatch(Character::isLetter))) {
                return firstName + " " + lastName;
            }
        }
        return null;
    }

    /*
    Opens file and compares input to members in the file.
    Returns member info if found, null if not found, throws exception if file wasn't found.
     */
    public String[] searchFile(String fileName, String searchInput) throws IOException, NullPointerException {
        String line;
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            while ((line = file.readLine()) != null) {
                line = line.trim();
                if (line.length() > 10 && line.substring(0, 10).chars().allMatch(Character::isDigit)) { // jumps over line if out of phase
                    String pin = line.substring(0, 10);
                    String name = line.substring(11).trim();

                    if (pin.equals(searchInput) || name.equalsIgnoreCase(searchInput)) {
                        String date = file.readLine().trim();
                        return new String[]{name, pin, date};
                    }
                    file.readLine(); //jumps over line with validity date, to look at next person.
                }
            }
        }
        return null;
    }

    // Checks if date is newer or older than one year
    public boolean checkValidityDate(String dateString) throws DateTimeParseException {
            LocalDate expireDate = LocalDate.now().minusYears(1).minusDays(1);
            LocalDate date = LocalDate.parse(dateString);
            return date.isAfter(expireDate);
    }

    // Loggs in a personal file, time and date when member entered the gym
    public void writeVisitLog(String[] person) throws IOException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.now();

        try (BufferedWriter output = new BufferedWriter(new FileWriter(person[0] + " " + person[1] + ".txt", true))) {
            output.write(person[0] + " , " + person[1] + " , " + date.format(timeFormatter) + "\n");
        }
    }
}
