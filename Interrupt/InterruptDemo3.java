package Interrupt;
import java.util.concurrent.TimeUnit;

/**
 * 为什么会出现无限循环 需要在catch中再次设置interrupt?
 * 
 * 当异常发生时 会将中断标志位清除
 */

public class InterruptDemo3 {
 
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    System.out.println(Thread.currentThread().getName()+"\t sign is "+Thread.currentThread().isInterrupted()+ " stop");
                    break;
                }

                try {
                    Thread.sleep(200);    //会导致线程无法停止 无限循环
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  //当出现异常时 再次手动将中断标志位设置为true
                    e.printStackTrace();
                }

                System.out.println("------ helo demo3");
            }
        } ,"t1");
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);   
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread( () -> {
            t1.interrupt();
        } ,"t2").start();
    }
    
}
