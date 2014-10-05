package tools;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DataBuffer<T> extends ArrayList<T>{
	private static final long serialVersionUID = -8798274277079081411L;
	private Semaphore s;
	
	public DataBuffer(){
		this.s = new Semaphore(0);
	}
	
	public T pop() throws InterruptedException{
		this.acquire();
		return this.remove(0);
	}
	
	public void push(T data){
		this.add(data);
		this.release();
	}
	
	private void acquire() throws InterruptedException{
		s.acquire();
	}
	
	private void release(){
		this.s.release();
	}
}
