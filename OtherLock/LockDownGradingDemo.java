package OtherLock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 锁降级
 * 锁的严苛程度下降
 */
public class LockDownGradingDemo {
    
    public static void main(String[] args) {
        
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

        ReadLock readLock = rwLock.readLock();
        WriteLock writeLock = rwLock.writeLock();

        //正常 A B两个线程
        //A
        // writeLock.lock();
        // System.out.println("---write");
        // writeLock.unlock();

        // //B
        // readLock.lock();
        // System.out.println("---read");
        // readLock.unlock();

        //本例，only one 同一个线程
        // writeLock.lock();
        // System.out.println("----write");
        // readLock.lock();
        // System.out.println("----read");
        // writeLock.unlock();
        // readLock.unlock();


        readLock.lock();
        System.out.println("----read");
        writeLock.lock();
        System.out.println("----write");
        readLock.unlock();
        writeLock.unlock();
    }
}
