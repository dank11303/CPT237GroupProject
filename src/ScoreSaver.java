//imports (these let us read/write files and store data in a HashMap)
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

//this class will manage the best times file for the game.
public class ScoreSaver {
    //the file that stores best times for each level
    private static final String FILE_NAME = "scores.csv";

    //load the times of the levels
    public static HashMap<Integer, Long> loadBestTimes() {
        //HashMap acts like a dictionary:
        //key = level index, value = best time in milliseconds
        HashMap<Integer, Long> times = new HashMap<>();

        //create a file object for scores.csv
        File file = new File(FILE_NAME);

        //if the file does not exist yet, return an empty HashMap
        if (!file.exists()) return times;

        //read times from a file if it exists
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            //read each line of the CSV file
            while ((line = reader.readLine()) != null) {
                //skip blank lines
                if (line.trim().isEmpty()) continue;

                //split "levelIndex,time" into two parts
                String[] parts = line.split(",");

                //convert the text into numbers
                int id = Integer.parseInt(parts[0]);
                long time = Long.parseLong(parts[1]);

                //store in the HashMap
                times.put(id, time);
            }
        } catch (IOException e) {
            //print an error message if something goes wrong
            System.out.println("There was an issue reading best times");
            System.out.println(e.getMessage());
        }

        //return the HashMap of best times
        return times;
    }//end loadBestTimes()

    //write the HashMap to the CSV
    public static void saveBestTimes(HashMap<Integer, Long> times) {
        //write/overwrite the scores.csv file with the current HashMap data
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            //loop through each entry in the HashMap
            for (HashMap.Entry<Integer, Long> entry : times.entrySet()) {
                //write "levelIndex,time" to the file
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            //print an error message if something goes wrong
            System.out.println("There was an issue saving best times");
            System.out.println(e.getMessage());
        }
    }//end saveBestTimes()

    //save data container for slots
    public static class SaveData {
        //which level the player was on when they saved
        public int levelIndex;

        //how much time had passed when they saved (milliseconds)
        public long elapsedMs;

        //the saved board (0 empty, 1 x, 2 queen)
        public int[][] userState;

        //constructor sets the values for this save data
        public SaveData(int levelIndex, long elapsedMs, int[][] userState) {
            this.levelIndex = levelIndex;
            this.elapsedMs = elapsedMs;
            this.userState = userState;
        }
    }

    //save a slot to a file (slot1.csv, slot2.csv, etc.)
    public static void saveSlot(int slotNumber, int levelIndex, int[][] userState, long elapsedMs) {
        //build the slot filename based on the slot number
        String slotFile = "slot" + slotNumber + ".csv";

        //write/overwrite the slot file
        try (FileWriter writer = new FileWriter(slotFile)) {
            //line 1: levelIndex,elapsedMs,n
            writer.write(levelIndex + "," + elapsedMs + "," + userState.length + "\n");

            //next lines: write the n by n board as comma-separated numbers
            for (int[] row : userState) {
                for (int c = 0; c < userState[0].length; c++) {
                    //write each number, and add commas between columns
                    writer.write(row[c] + (c == userState[0].length - 1 ? "" : ","));
                }

                //move to the next line after each row
                writer.write("\n");
            }
        } catch (IOException e) {
            //print an error message if something goes wrong
            System.out.println("There was an issue saving the slot");
            System.out.println(e.getMessage());
        }
    }

    //load a slot from a file (returns null if it doesn't exist)
    public static SaveData loadSlot(int slotNumber) {
        //build the slot filename
        String slotFile = "slot" + slotNumber + ".csv";

        //create a file object for that slot
        File file = new File(slotFile);

        //if it does not exist, return null
        if (!file.exists()) return null;

        //read the slot file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //read header line: levelIndex,elapsedMs
            String header = reader.readLine();

            //if file is empty, return null
            if (header == null) return null;

            //split the header into three parts
            String[] headParts = header.split(",");

            //convert header parts into numbers
            int levelIndex = Integer.parseInt(headParts[0]);
            long elapsedMs = Long.parseLong(headParts[1]);
            int n = Integer.parseInt(headParts[2]);

            //read board (n by n)
            int[][] board = new int[n][n];

            //read n lines, one line per row
            for (int r = 0; r < n; r++) {
                String line = reader.readLine();

                //if the file ends early, return null
                if (line == null) return null;

                //split the row by commas
                String[] parts = line.split(",");

                //convert each number and store in the board
                for (int c = 0; c < n; c++) {
                    board[r][c] = Integer.parseInt(parts[c]);
                }
            }

            //return the loaded SaveData object
            return new SaveData(levelIndex, elapsedMs, board);
        } catch (Exception e) {
            //print an error message if something goes wrong
            System.out.println("There was an issue loading the slot");
            System.out.println(e.getMessage());
            return null;
        }
    }

    //these methods will be used to save and load custom levels
    public static void saveCustomLevel(CustomLevel level) {
        //try to write data to a file
        try (FileWriter writer = new FileWriter("customlevels.csv", true)) {
            //header line for the custom levels csv
            writer.write(level.creatorName + "," + level.n + "\n");

            //write the region map
            for (int[] row : level.regionMap) {
                for (int c = 0; c < level.n; c++) {
                    writer.write(row[c] + (c == level.n - 1 ? "" : ","));
                }
                writer.write("\n");
            }

            //write a delimiter to mark the end of each level
            writer.write("---\n");
        } catch (Exception ex) {
            System.out.println("There was an issue saving a custom level");
            System.out.println(ex.getMessage());
        }//end try catch
    }//end saveCustomLevel(CustomLevel)

    public static ArrayList<CustomLevel> loadCustomLevels() {
        ArrayList<CustomLevel> levels = new ArrayList<CustomLevel>(); //create an array to store the levels

        //check if the file even exists first.
        File file = new File("customlevels.csv");
        if (!file.exists()) return levels;

        //create a reader and read each line and save each level to the array
        try (BufferedReader reader = new BufferedReader(new FileReader("customlevels.csv"))) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                //read the header, separate it, and get the name and row count
                String[] parts = line.split(",");
                String creatorName = parts[0];
                int n = Integer.parseInt(parts[1]);

                //read the following region map using the row count to keep track
                int[][] regionMap = new int[n][n];
                for (int r = 0; r < n; r++) {
                    //read the line
                    String[] row = reader.readLine().split(",");
                    for (int c = 0; c < n; c++) {
                        //write each and every cell
                        regionMap[r][c] = Integer.parseInt(row[c]);
                    }
                }

                //read the level separator delimiter (---)
                reader.readLine();

                //create the level and assign an index
                CustomLevel level = new CustomLevel(creatorName, n, regionMap);
                level.index = index;
                levels.add(level);
                index++;
            }//end while
        } catch (Exception ex) {
            System.out.println("There was an issue loading the custom levels");
            System.out.println(ex.getMessage());
        }//end catch

        return levels;
    }//end loadCustomLevels()


    /*****Stuff for saving Custom Times *****/
    //this works exactly like the regular times up above
    //create a constant variable for the custom times file
    private static final String CUSTOM_TIMES_FILE = "customtimes.csv";

    //method to load custom times intoa HashMap for accessing it just like a dictionary
    public static HashMap<Integer, Long> loadCustomBestTimes() {
        //create the HashMap
        HashMap<Integer, Long> times = new HashMap<>();
        //create a File object using the constant
        File file = new File(CUSTOM_TIMES_FILE);

        //check if the file exists and if it doesnt, return an empty map
        if (!file.exists()) return times;

        //try catch to read the times usinga Buffered reader (with auto closing)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                times.put(Integer.parseInt(parts[0]), Long.parseLong(parts[1]));
            }
        } catch (Exception ex) {
            System.out.println("There was an issue reading the custom times");
            System.out.println(ex.getMessage());
        }
        return times;
    }//end loadCustomBestTimes

    //saves the best times for custom levels just like they do for built-in levels
    public static void saveCustomBestTimes(HashMap<Integer, Long> times)
    {
        //try to write the data to the file
        try (FileWriter writer = new FileWriter(CUSTOM_TIMES_FILE))
        {
            for (HashMap.Entry<Integer, Long> entry : times.entrySet())
            {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        }
        catch (Exception ex)
        {
            System.out.println("There was an issue saving custom best times");
            System.out.println(ex.getMessage());
        }//end try catch
    }//end SaveCustomBestTimes
}