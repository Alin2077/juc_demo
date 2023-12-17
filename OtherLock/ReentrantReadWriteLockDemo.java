package OtherLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyResource{
    Map<String,String> map = new HashMap<>();
    //等价于Synchronized
    Lock lock = new ReentrantLock();

    ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void write(String key,String value){
        // lock.lock();
        rwLock.writeLock().lock();
        try{
            System.out.println(Thread.currentThread().getName()+"\t write start");
            map.put(key, value);

            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.println(Thread.currentThread().getName()+"\t write end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally{
            // lock.unlock();
            rwLock.writeLock().unlock();
        }
    }

    public void read(String key){
        String  result = "";
        // lock.lock();
        rwLock.readLock().lock();
        try{
            System.out.println(Thread.currentThread().getName()+"\t read start");
            result = map.get(key);

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
                System.out.println(Thread.currentThread().getName()+"\t read end"+"\t"+result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally{
            // lock.unlock();
            rwLock.readLock().unlock();
        }
    }


}

public class ReentrantReadWriteLockDemo {
    
    public static void main(String[] args) {
        
        MyResource myResource = new MyResource();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                myResource.write(finalI+"", finalI+"");
            } ,"w"+i).start();
        }

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                myResource.read(finalI+"");
            } ,"r"+i).start();
        }

    }
}
