//imports
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.IOException;

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

        //read times from a file if it exists, if not, create it
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split(","); //split each line in the CSV into key value pairs
                int id = Integer.parseInt(parts[0]);
                long time = Long.parseLong(parts[1]);
                times.put(id, time); //add the time to the HashMap
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
    }
}
