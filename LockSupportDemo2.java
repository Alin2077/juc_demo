import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
/**
 * LockSupport解决了两个之前使用wait或await时的痛点
 * 1. 无需获取到锁即可执行park或unpark
 * 2. park或者unpark的顺序没有硬性规定  ----- 
 * 
 * 注意：
 *  因为unpark的许可证不会累计 始终只有一个 所以park和unpark不要用成多个
 */
public class LockSupportDemo2 {
    
    public static void main(String[] args) {
    
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + "\t -----come in");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t -----unpark");
        }, "t1");
        t1.start();
        
        // try {
        //     TimeUnit.SECONDS.sleep(1);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        new Thread( () -> {
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName()+"\t ---send ");
        },"t2").start();
    }
}
