//imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

//this class will manage the best times file for the game.
public class ScoreSaver
{
    private static final String FILE_NAME = "scores.csv";  //file name for the scores csv file

    //load the times of the levels
    public static HashMap<Integer, Long> loadBestTimes()
    {
        //create hashmap for the times. Acts as a dictionary
        HashMap<Integer, Long> times = new HashMap<>();
        // create a file object
        File file = new File(FILE_NAME);

        //if there is no file yet, return an empty HashMap
        if (!file.exists()) return times;

        //read times from a file if it exists
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                long time = Long.parseLong(parts[1]);
                times.put(id, time);
            }
        }
        catch (IOException e)
        {
            System.out.println("There was an issue reading best times");
            System.out.println(e.getMessage());
        }

        return times; //return the times HashMap
    }//end loadBestTimes()

    //write the HashMap to the CSV
    public static void saveBestTimes(HashMap<Integer, Long> times)
    {
        try (FileWriter writer = new FileWriter(FILE_NAME))
        {
            for (HashMap.Entry<Integer, Long> entry : times.entrySet())
            {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        }
        catch (IOException e)
        {
            System.out.println("There was an issue saving best times");
            System.out.println(e.getMessage());
        }
    }//end saveBestTimes()

    //save data container for slots
    public static class SaveData
    {
        public int levelIndex;
        public long elapsedMs;
        public int[][] userState;

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
        String slotFile = "slot" + slotNumber + ".csv";

        try (FileWriter writer = new FileWriter(slotFile))
        {
            //line 1: levelIndex,elapsedMs
            writer.write(levelIndex + "," + elapsedMs + "\n");

            //next lines: the board rows as comma-separated numbers
            for (int r = 0; r < userState.length; r++)
            {
                for (int c = 0; c < userState[0].length; c++)
                {
                    writer.write(userState[r][c] + (c == userState[0].length - 1 ? "" : ","));
                }
                writer.write("\n");
            }
        }
        catch (IOException e)
        {
            System.out.println("There was an issue saving the slot");
            System.out.println(e.getMessage());
        }
    }

    //load a slot from a file (returns null if it doesn't exist)
    public static SaveData loadSlot(int slotNumber)
    {
        String slotFile = "slot" + slotNumber + ".csv";
        File file = new File(slotFile);

        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            //read header: levelIndex,elapsedMs
            String header = reader.readLine();
            if (header == null) return null;

            String[] headParts = header.split(",");
            int levelIndex = Integer.parseInt(headParts[0]);
            long elapsedMs = Long.parseLong(headParts[1]);

            //read board
            int[][] board = new int[8][8]; //Queens is 8x8 in your project
            for (int r = 0; r < 8; r++)
            {
                String line = reader.readLine();
                if (line == null) return null;

                String[] parts = line.split(",");
                for (int c = 0; c < 8; c++)
                {
                    board[r][c] = Integer.parseInt(parts[c]);
                }
            }

            return new SaveData(levelIndex, elapsedMs, board);
        }
        catch (Exception e)
        {
            System.out.println("There was an issue loading the slot");
            System.out.println(e.getMessage());
            return null;
        }
    }
}