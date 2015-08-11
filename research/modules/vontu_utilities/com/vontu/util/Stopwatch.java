// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import com.vontu.util.format.ElapsedTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import sun.misc.Perf;
import java.util.Map;

public class Stopwatch
{
    private final Map<Thread, Long> _startTimes;
    private final String _id;
    private final Perf _perf;
    private Statistics _statistics;
    
    public Stopwatch(final String id) {
        this._startTimes = Collections.synchronizedMap(new HashMap<Thread, Long>());
        this._perf = Perf.getPerf();
        this._id = id;
        this._statistics = new Statistics(id);
    }
    
    public String getId() {
        return this._id;
    }
    
    public void start() {
        this._startTimes.put(Thread.currentThread(), this.currentTime());
    }
    
    public boolean isStarted() {
        return this._startTimes.get(Thread.currentThread()) != null;
    }
    
    public void stopIfRunning() {
        final Long startTime = this._startTimes.remove(Thread.currentThread());
        if (startTime != null) {
            this._statistics.update(this.currentTime() - startTime);
        }
    }
    
    public Statistics stop() {
        final Long startTime = this._startTimes.remove(Thread.currentThread());
        if (startTime == null) {
            throw new IllegalStateException("stop watch has not been started");
        }
        return this._statistics.update(this.currentTime() - startTime);
    }
    
    public void reset() {
        synchronized (this._startTimes) {
            if (this._startTimes.size() != 0) {
                throw new IllegalStateException("Stop watch is currently running");
            }
            this._startTimes.clear();
            this._statistics = new Statistics(this._id);
        }
    }
    
    public long getCurrentElapsedTime() {
        final Long startTime = this._startTimes.get(Thread.currentThread());
        if (startTime != null) {
            return this.currentTime() - startTime;
        }
        return 0L;
    }
    
    public Statistics getStatistics() {
        return (Statistics)this._statistics.clone();
    }
    
    @Override
    public String toString() {
        return this._statistics.toString();
    }
    
    private final long currentTime() {
        return this._perf.highResCounter() * 1000L / this._perf.highResFrequency();
    }
    
    public static class Statistics implements Cloneable
    {
        private String _stopwatchId;
        private int _count;
        private long _minTime;
        private long _maxTime;
        private long _totalTime;
        private long _lastTime;
        
        private Statistics(final String stopwatchId) {
            this._minTime = Long.MAX_VALUE;
            this._stopwatchId = stopwatchId;
        }
        
        public Object clone() {
            Statistics clone = null;
            try {
                clone = (Statistics)super.clone();
            }
            catch (CloneNotSupportedException ex) {}
            synchronized (this) {
                clone._stopwatchId = this._stopwatchId;
                clone._count = this._count;
                clone._minTime = this._minTime;
                clone._maxTime = this._maxTime;
                clone._totalTime = this._totalTime;
                clone._lastTime = this._lastTime;
            }
            return clone;
        }
        
        private synchronized Statistics update(final long time) {
            ++this._count;
            this._lastTime = time;
            this._totalTime += time;
            this._minTime = Math.min(this._minTime, time);
            this._maxTime = Math.max(this._maxTime, time);
            return (Statistics)this.clone();
        }
        
        public int getCount() {
            return this._count;
        }
        
        public String getMinTimeFormatted() {
            return ElapsedTimeFormatter.format(this.getMinTime());
        }
        
        public long getMinTime() {
            if (this._minTime == Long.MAX_VALUE) {
                return 0L;
            }
            return this._minTime;
        }
        
        public String getMaxTimeFormatted() {
            return ElapsedTimeFormatter.format(this.getMaxTime());
        }
        
        public long getMaxTime() {
            return this._maxTime;
        }
        
        public String getTotalTimeFormatted() {
            return ElapsedTimeFormatter.format(this.getTotalTime());
        }
        
        public long getTotalTime() {
            return this._totalTime;
        }
        
        public String getAverageTimeFormatted() {
            return ElapsedTimeFormatter.format(this.getAverageTime());
        }
        
        public long getAverageTime() {
            if (this._count > 0) {
                return this._totalTime / this._count;
            }
            return 0L;
        }
        
        public long getLastTime() {
            return this._lastTime;
        }
        
        public String getLastTimeFormatted() {
            return ElapsedTimeFormatter.format(this.getLastTime());
        }
        
        @Override
        public String toString() {
            final StringBuffer string = new StringBuffer();
            if (this._stopwatchId != null) {
                string.append("Stopwatch: ");
                string.append(this._stopwatchId);
                string.append(", ");
            }
            string.append("Total Time: ");
            string.append(this._totalTime);
            string.append(", Count: ");
            string.append(this._count);
            string.append(", Last Time: ");
            string.append(this._lastTime);
            string.append(", Average Time: ");
            string.append(this.getAverageTime());
            string.append(", Min Time: ");
            string.append(this._minTime);
            string.append(", Max Time: ");
            string.append(this._maxTime);
            return string.toString();
        }
    }
}
