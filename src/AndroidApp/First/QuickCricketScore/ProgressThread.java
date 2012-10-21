package AndroidApp.First.QuickCricketScore;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** Nested class that performs progress calculations (counting) */
class ProgressThread extends Thread {
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    int mState;
    int total;
   
    ProgressThread(Handler h) {
        mHandler = h;
    }
   
    public void run() {
        mState = STATE_RUNNING;   
        total = 0;
        while (mState == STATE_RUNNING) {
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread Interrupted");
            }
            Message msg = mHandler.obtainMessage();
            msg.arg1 = total;
            mHandler.sendMessage(msg);
            total++;
        }
    }
    
    /* sets the current state for the thread,
     * used to stop the thread */
    public void setState(int state) {
        mState = state;
    }
}
