package ca.zihao.utility;

/**
 * Created by Zihao on 6/16/2014.
 */
public class StopWatch {

	long start;
	long ms;
	boolean paused;
	long timelimit;

	public StopWatch() {
		timelimit = -1;
		reset();
	}

	public void setTimelimit(long limit) {
		timelimit = limit;
	}

	public void start() {
		start = System.currentTimeMillis();
		ms = 0;
		paused = false;
	}

	public void pause() {
		ms += time();
		paused = true;
	}

	public void resume() {
		start = System.currentTimeMillis();
		paused = false;
	}

	public int time() {
		long t = ms;
		if(!paused) {
			long now = System.currentTimeMillis();
			t += now - start;
		}
		if(timelimit > 0) {
			t = Math.min(timelimit,t);
		}
		return (int)t;
	}

	public void reset() {
		paused = true;
		ms = 0;
		start = 0;
	}
}
