package android.os;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {
    private final transient ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<DelayItem<Message>> q = new PriorityQueue<DelayItem<Message>>();
    private final Condition available = lock.newCondition();
    private Thread leader = null;

    public Message next() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                DelayItem<Message> first = q.peek();
                if (first == null)
                    available.await();
                else {
                    long delay = first.getDelay(NANOSECONDS);
                    if (delay <= 0) {
                        first = q.poll();
                        return first.getItem();
                    }
                    first = null;
                    // don't retain ref while waiting
                    if (leader != null)
                        available.await();
                    else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && q.peek() != null)
                available.signal();
            lock.unlock();
        }
    }

    public boolean enqueueMessage(Message msg, long uptimeMillis) {
    final ReentrantLock lock = this.lock;
    DelayItem<Message> item = new DelayItem<Message>(msg, uptimeMillis);
    lock.lock();
    try {
        q.offer(item);
        if (q.peek() == item) {
            leader = null;
            available.signal();
        }
        return true;
    } finally {
        lock.unlock();
    }
    }
}