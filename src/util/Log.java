package util;

public class Log {
	
	public static void i(String tag,String msg){
		System.out.println("Log.i --> "+tag+"  "+msg);
	}
	
	public static void w(String tag,String msg){
		System.out.println("Log.w --> "+tag+"  "+msg);
	}
	
	public static void e(String tag,String msg){
		System.out.println("Log.e --> "+tag+"  "+msg);
	}
	
	public static void d(String tag,String msg){
		System.out.println("Log.d --> "+tag+"  "+msg);
	}
	
	public static void v(String tag,String msg){
		System.out.println("Log.v --> "+tag+"  "+msg);
	}
}
