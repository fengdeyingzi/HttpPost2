package android.os;

public class HandlerUtil {
	
	//初始化handler 在main中调用
	Handler sMainThreadHandler;
	
	public void init(){
		// 启动looper
        Looper.prepare(true);
        // 创建一个子线程（工作线程，比如加载Activity啊，初始化一堆的对象等等）
        startWorkThread();
        // 创建一个子线程与主线程通讯的处理器，有需要主线程处理的事情，子线程就通过这个Handler发消息
        sMainThreadHandler = initMainHandler();
        // 启动主线程的无限轮询
        Looper.loop();
	}
	
	 public  Handler initMainHandler() {
	        return new Handler() {

	            @Override
	            public void handleMessage(Message msg) {
	                switch (msg.what) {
	                //TODO: 处理不同的消息（安卓中是一堆类型强转跟消息传递的代码）
	                default:
	                    // EG：创建一个新的Handler
	                    createActivity();
	                    break;
	                }
	            }
	        };
	    }

	    public  void startWorkThread(){
	        new Thread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					try {
		                //延迟，确保Lopper进入循环
		                Thread.sleep(1000);
		                System.out.println(Thread.currentThread().getName()+ " :send msg");
		                //假设说子线程的作用就是发送消息，要主线程new一个Handler
		                sMainThreadHandler.sendMessageAtTime(new Message(), 3000);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
				}}
			);
	    }

	    public  void createActivity() {
	        Handler handler = new Handler() {

	            @Override
	            public void handleMessage(Message msg) {
	                System.out.println(Thread.currentThread().getName()+ " :get msg");
	            }
	        };

	        System.out.println(Thread.currentThread().getName()+ " :send msg");
	        handler.sendMessage(new Message());
	    }

}
