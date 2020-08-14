package android.os;



public class Message {
    private static final Message MESSAGE = new Message();
    public Handler target;

    // 变量，用于定义此Message属于何种操作
    public int what;
    // 变量，用于定义此Message传递的信息数据，通过它传递信息
    public Object obj;
    // 变量，传递一些整型数据时使用
    public int arg1;
    // 变量，传递一些整型数据时使用
    public int arg2;

    public static Message obtain() {
        return MESSAGE;
    }
}
