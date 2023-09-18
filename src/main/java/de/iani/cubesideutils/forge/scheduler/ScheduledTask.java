package de.iani.cubesideutils.forge.scheduler;

public class ScheduledTask implements Comparable<ScheduledTask> {
    private Runnable task;
    private volatile boolean cancelled;
    private long nextExecutionTick;
    private int delay;
    private int intervall;

    public ScheduledTask(Runnable task, int delay, int intervall) {
        this.task = task;
        this.delay = delay;
        this.intervall = intervall;
    }

    public void cancel() {
        cancelled = true;
        task = null;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    void execute() {
        Runnable task = this.task;
        if (!isCancelled() && task != null) {
            try {
                task.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public int getDelay() {
        return delay;
    }

    void setScheduledOnTick(long scheduledOnTick, boolean forFirstExecution) {
        if (forFirstExecution) {
            this.nextExecutionTick = scheduledOnTick + delay;
        } else {
            this.nextExecutionTick = scheduledOnTick + intervall;
        }
    }

    long getScheduledOnTick() {
        return nextExecutionTick;
    }

    public int getIntervall() {
        return intervall;
    }

    long getExecutionTick() {
        return nextExecutionTick;
    }

    @Override
    public int compareTo(ScheduledTask o) {
        return Long.signum(nextExecutionTick - o.nextExecutionTick);
    }
}
