package android.os;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 延迟队列元素
 * 
 * @author ChenSS on 2017年9月13日
 * @param <T>
 */
public class DelayItem<T> implements Delayed {
    private long timeout;
    private long endTime;

    private T item;

    public DelayItem(T item, long timeout) {
        this.item = item;
        this.timeout = timeout;
        this.endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeout, TimeUnit.MILLISECONDS);
    }

    public DelayItem(T item, long timeout, TimeUnit unit) {
        this.item = item;
        this.timeout = timeout;
        this.endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeout, unit);
    }

    public T getItem() {
        return this.item;
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * 重置结束时间
     */
    public void reset() throws TimeoutException {
        if (System.nanoTime() > endTime)
            throw new TimeoutException("The current object has timeout, can not be reset");
        this.endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 预计结束时间-当前时间 = 剩余停滞时间（当数值小于零时此对象可用）
     * 
     * @return 纳秒（未达到超时时间对象不可用）
     */
    
    public long getDelay(TimeUnit unit) {
        return unit.convert((endTime - System.nanoTime()), TimeUnit.NANOSECONDS);
    }

    /**
     * 取出结束时间最早的
     */
    
    public int compareTo(Delayed other) {
        if (other == this)
            return 0;
        if (other instanceof DelayItem) {
            long diff = endTime - ((DelayItem<?>) other).endTime;
            return (diff == 0) ? 0 : ((diff < 0) ? -1 : 1);
        }
        long diff = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (diff == 0) ? 0 : ((diff < 0) ? -1 : 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DelayItem<?> other = (DelayItem<?>) obj;
        if (item == null) {
            if (other.item != null)
                return false;
        } else if (!item.equals(other.item))
            return false;
        return true;
    }
}