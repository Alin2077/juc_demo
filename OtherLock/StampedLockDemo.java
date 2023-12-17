package OtherLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {
    static int number = 37;
    static StampedLock stampedLock = new StampedLock();

    public static void write(){
        long writeLock = stampedLock.writeLock();
        System.out.println(Thread.currentThread().getName()+"\t"+"write start");
        try{
            number = number + 13;
        }finally{
            stampedLock.unlockWrite(writeLock);
        }
        System.out.println(Thread.currentThread().getName()+"\t"+"write end");
    }

    public static void read(){
        long readLock = stampedLock.readLock();
        System.out.println(Thread.currentThread().getName()+"\t"+"read start");
        try{
            TimeUnit.SECONDS.sleep(3);
            System.out.println(Thread.currentThread().getName()+"\t"+"read ing");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            stampedLock.unlockRead(readLock);
        }
        System.out.println(Thread.currentThread().getName()+"\t"+"read end");
    }

    public static void Oread(){
        int result = 0;
        long readLock = stampedLock.tryOptimisticRead();
        System.out.println(Thread.currentThread().getName()+"\t"+"read start"+"\t"+number);
        try{
            TimeUnit.SECONDS.sleep(3);
            System.out.println("4s after \t"+stampedLock.validate(readLock));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!stampedLock.validate(readLock)){
            System.out.println("had modify");
            long lock = stampedLock.readLock();
            try{
                System.out.println("up to common read lock");
                result = number;
            }finally{
                stampedLock.unlockRead(lock);
            }
        }
        System.out.println(Thread.currentThread().getName()+"\t"+"read end"+"\t"+number);
    }

    public static void main(String[] args) {
        new Thread( () -> {
            // read();
            Oread();
        },"r").start();

        new Thread( () -> {
            write();
        },"w").start();
    }
}
