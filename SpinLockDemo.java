import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现一个自旋锁 复习CAS思想
 * 
 * 通过CAS操作完成自旋锁，A线程先进来调用myLock方法自己持有锁5秒钟，B随后进来发现当前有线程持有锁，自旋等待
 */
public class SpinLockDemo {
    
    AtomicReference<Thread> auAtomicReference = new AtomicReference<>();

    public void lock(){
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName()+"\t ---- come in");
        while (!auAtomicReference.compareAndSet(null, thread)) {
            
        }
    }

    public void unLock(){
        Thread thread = Thread.currentThread();
        auAtomicReference.compareAndSet(thread, null);
        System.out.println(Thread.currentThread().getName()+"\t task over,unLock...");
    }

    public static void main(String[] args) {
        
        SpinLockDemo spinLockDemo = new SpinLockDemo();
        new Thread( () -> {
            spinLockDemo.lock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spinLockDemo.unLock();
        } ,"A").start();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread( () -> {
            spinLockDemo.lock();
            
            spinLockDemo.unLock();
        } ,"B").start();

    }
}
