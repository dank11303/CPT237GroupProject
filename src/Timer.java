

//this class will handle the timing of each level when it starts and when it stops
public class Timer
{
    //stores timer start time, total time so far, and if the timer is running
    private long startTime;
    private long elapsedTime;
    private boolean running;

    //start the timer
    public void start()
    {
        if (!running)
        {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }
    //stop the timer
    public void stop()
    {
        if (running)
        {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }
    //pause the timer
    public void pause()
    {
        stop();
    }
    //output the difference in time once the level is completed and a time is called for.
    public long getElapsedMilliseconds()
    {
        if (running)
        {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        }
        return elapsedTime;
    }

    //convert the elapsed time into a simple minutes:seconds string
    public String getElapsedTimeString()
    {
        long ms = getElapsedMilliseconds();
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    //overload the getElapsedTimeString function to allow for the time to be entered instead of grabbed from the object.
    public String getElapsedTimeString(long ms)
    {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }//end getElapsedTimeString(long)

    //reset the timer back to zero
    public void reset()
    {
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }
}
