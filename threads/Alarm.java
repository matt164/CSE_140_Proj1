package nachos.threads;
import nachos.machine.*;
import java.util.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 * 
 */
class Alarm_Comparator implements Comparator<KThread>{
// implement compare funtion.

//changed code
public int compare(KThread one, KThread two){

    if(one.getWaitTime() > two.getWaitTime()){
        return 1;
    }
    else{
        return -1;
    }

}


}
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    
    private TreeSet<KThread> sleepQueue;

    public Alarm() {
        //initialize sleepQueue.

        sleepQueue = new TreeSet<KThread>(new Alarm_Comparator());
        Machine.timer().setInterruptHandler(new Runnable() {
        public void run() { timerInterrupt(); }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    //changed code
    boolean intStatus = Machine.interrupt().disable();
    while(!sleepQueue.isEmpty() && sleepQueue.first().getWaitTime() < Machine.timer().getTime()){
        sleepQueue.pollFirst().ready();

    }
    Machine.interrupt().restore(intStatus);
    KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     * 
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param    x    the minimum number of clock ticks to wait.
     *
     * @see    nachos.machine.Timer#getTime()
     */

    

    public void waitUntil(long x) {
    // for now, cheat just to get something working (busy waiting is bad)
    
    //changed code
    KThread.currentThread().setWaitTime((Machine.timer().getTime() + x));
    boolean intStatus = Machine.interrupt().disable();
    //add current thread to sleepQueue
    //sleep current thread.
    if(KThread.currentThread().getWaitTime() >= Machine.timer().getTime()) {
       
        sleepQueue.add(KThread.currentThread());
        KThread.currentThread().sleep();
        
    }
    Machine.interrupt().restore(intStatus);
    
    }

}
