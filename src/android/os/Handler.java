package android.os;

public abstract class Handler {
    public Looper mLooper;
    public MessageQueue mQueue;

    public Handler() {
        final Class<? extends Handler> klass = getClass();
        // java.lang.Class.isAnonymousClass() 当且仅当底层类是匿名类，则返回true。
        // java.lang.Class.isMemberClass() 返回true当且仅当底层类是成员类。
        // java.lang.Class.isLocalClass()返回true，当且仅当基础类是局部类。
        // 原本这是日志，说明Handler不要随便去new，容易内存溢出
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass())) {
            System.out.println("The following Handler class should be static or leaks might" + " + klass.getCanonicalName()");
        }
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
    }

    public Handler(Looper looper) {
    	mLooper = looper;
    	if (mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
    }

    public final boolean sendMessage(Message msg) {
        return sendMessage(msg, 0);
    }

    public final boolean sendMessage(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessage(msg, delayMillis);
    }

    public final boolean sendMessage(Message msg, long delayMillis) {
        if (delayMillis < 0)
            delayMillis = 0;
        return sendMessageAtTime(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long delayMillis) {
        MessageQueue queue = mQueue;
        if (queue == null)
            throw new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
        return enqueueMessage(queue, msg, delayMillis);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long delayMillis) {
        msg.target = this;
        return queue.enqueueMessage(msg, delayMillis);
    }

    public final void dispatchMessage(Message msg) {
        handleMessage(msg);
    }

    abstract public void handleMessage(Message msg);
}
