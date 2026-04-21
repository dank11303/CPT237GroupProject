//imports (these let us read/write files and store data in a HashMap)
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

//this class will manage the best times file for the game.
public class ScoreSaver
{
    //the file that stores best times for each level
    private static final String FILE_NAME = "scores.csv";

    //load the times of the levels
    public static HashMap<Integer, Long> loadBestTimes()
    {
        //HashMap acts like a dictionary:
        //key = level index, value = best time in milliseconds
        HashMap<Integer, Long> times = new HashMap<>();

        //create a file object for scores.csv
        File file = new File(FILE_NAME);

        //if the file does not exist yet, return an empty HashMap
        if (!file.exists()) return times;

        //read times from a file if it exists
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;

            //read each line of the CSV file
            while ((line = reader.readLine()) != null)
            {
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
        }
        catch (IOException e)
        {
            //print an error message if something goes wrong
            System.out.println("There was an issue reading best times");
            System.out.println(e.getMessage());
        }

        //return the HashMap of best times
        return times;
    }//end loadBestTimes()

    //write the HashMap to the CSV
    public static void saveBestTimes(HashMap<Integer, Long> times)
    {
        //write/overwrite the scores.csv file with the current HashMap data
        try (FileWriter writer = new FileWriter(FILE_NAME))
        {
            //loop through each entry in the HashMap
            for (HashMap.Entry<Integer, Long> entry : times.entrySet())
            {
                //write "levelIndex,time" to the file
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        }
        catch (IOException e)
        {
            //print an error message if something goes wrong
            System.out.println("There was an issue saving best times");
            System.out.println(e.getMessage());
        }
    }//end saveBestTimes()

    //save data container for slots
    public static class SaveData
    {
        //which level the player was on when they saved
        public int levelIndex;

        //how much time had passed when they saved (milliseconds)
        public long elapsedMs;

        //the saved board (0 empty, 1 x, 2 queen)
        public int[][] userState;

        //constructor sets the values for this save data
        public SaveData(int levelIndex, long elapsedMs, int[][] userState)
        {
            this.levelIndex = levelIndex;
            this.elapsedMs = elapsedMs;
            this.userState = userState;
        }
    }

    //save a slot to a file (slot1.csv, slot2.csv, etc.)
    public static void saveSlot(int slotNumber, int levelIndex, int[][] userState, long elapsedMs)
    {
        //build the slot filename based on the slot number
        String slotFile = "slot" + slotNumber + ".csv";

        //write/overwrite the slot file
        try (FileWriter writer = new FileWriter(slotFile))
        {
            //line 1: levelIndex,elapsedMs
            writer.write(levelIndex + "," + elapsedMs + "\n");

            //next lines: write the 8x8 board as comma-separated numbers
            for (int[] row : userState)
            {
                for (int c = 0; c < userState[0].length; c++)
                {
                    //write each number, and add commas between columns
                    writer.write(row[c] + (c == userState[0].length - 1 ? "" : ","));
                }

                //move to the next line after each row
                writer.write("\n");
            }
        }
        catch (IOException e)
        {
            //print an error message if something goes wrong
            System.out.println("There was an issue saving the slot");
            System.out.println(e.getMessage());
        }
    }

    //load a slot from a file (returns null if it doesn't exist)
    public static SaveData loadSlot(int slotNumber)
    {
        //build the slot filename
        String slotFile = "slot" + slotNumber + ".csv";

        //create a file object for that slot
        File file = new File(slotFile);

        //if it does not exist, return null
        if (!file.exists()) return null;

        //read the slot file
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            //read header line: levelIndex,elapsedMs
            String header = reader.readLine();

            //if file is empty, return null
            if (header == null) return null;

            //split the header into two parts
            String[] headParts = header.split(",");

            //convert header parts into numbers
            int levelIndex = Integer.parseInt(headParts[0]);
            long elapsedMs = Long.parseLong(headParts[1]);

            //read board (8x8)
            int[][] board = new int[8][8];

            //read 8 lines, one line per row
            for (int r = 0; r < 8; r++)
            {
                String line = reader.readLine();

                //if the file ends early, return null
                if (line == null) return null;

                //split the row by commas
                String[] parts = line.split(",");

                //convert each number and store in the board
                for (int c = 0; c < 8; c++)
                {
                    board[r][c] = Integer.parseInt(parts[c]);
                }
            }

            //return the loaded SaveData object
            return new SaveData(levelIndex, elapsedMs, board);
        }
        catch (Exception e)
        {
            //print an error message if something goes wrong
            System.out.println("There was an issue loading the slot");
            System.out.println(e.getMessage());
            return null;
        }
    }
}