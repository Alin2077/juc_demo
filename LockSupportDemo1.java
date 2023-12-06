import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockSupportDemo1 {

    public static void main(String[] args) {
        
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread( () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\t ----come in");
                condition.await();   //和wait()和notify()类似 必须要在锁块中才能使用 否则会报错IllegalMonitorState
                System.out.println(Thread.currentThread().getName()+"\t ----signal");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                lock.unlock();
            }
        } ,"t1").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread( () -> {
            lock.lock();
            try{
                condition.signal();
                System.out.println(Thread.currentThread().getName()+"\t ----- send signal");
            }finally{
                lock.unlock();
            }
            
        } ,"t2").start();

    }


    public static void m1(){
        Object object = new Object();
        new Thread(() -> {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                try {
                    object.wait(); // wait()和notify()方法都需要在获取到锁的时候调用
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ---- notify");
            }
        }, "t1").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            synchronized (object) {
                object.notify();
                System.out.println(Thread.currentThread().getName() + "\t ---- start notify");
            }
        }, "t2").start();
    }
}