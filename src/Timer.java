//this class will handle the timing of each level when it starts and when it stops
public class Timer
{
    //startTime = when the timer last started (ms since 1970)
    //elapsedTime = total time counted so far (ms)
    //running = true when the timer is currently counting
    private long startTime;
    private long elapsedTime;
    private boolean running;

    //start the timer
    public void start()
    {
        //only start if we are not already running
        if (!running)
        {
            //save the current system time as our start point
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    //stop the timer
    public void stop()
    {
        //only stop if we are currently running
        if (running)
        {
            //add the time since start() to the total elapsed time
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    //pause the timer
    public void pause()
    {
        //pause is the same thing as stop for this project
        stop();
    }

    //output the difference in time once the level is completed and a time is called for.
    public long getElapsedMilliseconds()
    {
        //if the timer is running, add the "live" time to what we already saved
        if (running)
        {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        }

        //if paused/stopped, just return what we saved
        return elapsedTime;
    }

    //convert the elapsed time into a simple minutes:seconds string
    public String getElapsedTimeString()
    {
        //get total milliseconds from the timer
        long ms = getElapsedMilliseconds();

        //convert ms to seconds, then split into minutes and seconds
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        //format seconds with 2 digits (ex: 1:05 instead of 1:5)
        return String.format("%d:%02d", minutes, seconds);
    }

    //overload the getElapsedTimeString function to allow for the time to be entered instead of grabbed from the object.
    public String getElapsedTimeString(long ms)
    {
        //convert the passed-in ms to seconds, then split into minutes and seconds
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        //format seconds with 2 digits
        return String.format("%d:%02d", minutes, seconds);
    }//end getElapsedTimeString(long)

    //set the timer to a specific elapsed time (used for loading save slots)
    public void setElapsedMilliseconds(long ms)
    {
        //reset the timer first so old values do not mix with the loaded time
        reset();

        //set the elapsed time to the loaded value
        elapsedTime = ms;
    }

    //reset the timer back to zero
    public void reset()
    {
        //clear all timer values and mark it as not running
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }
}