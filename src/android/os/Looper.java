package android.os;

public class Looper {
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
    public MessageQueue mQueue = new MessageQueue();
    public Thread mThread;
    //保存主线程looper
    public static Looper mainLooper;
    

    private Looper() {
    }

    private Looper(boolean quitAllowed) {
        mThread = Thread.currentThread(); //当前线程
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(mainLooper = new Looper(quitAllowed)); //将当前线程添加到looper
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        for (;;) {
            try {
                Message msg = queue.next();
                if (msg == null)
                    return;
                msg.target.dispatchMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	public static Looper getMainLooper() {
		// TODO Auto-generated method stub
		return mainLooper;
	}
}

