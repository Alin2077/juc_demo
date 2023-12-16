package SynchronizedUp;

public class LockBigDemo {
    
    static Object objectLock = new Object();
    public static void main(String[] args) {
        
        //加了这么多次synchronized，没有意义，针对的都是同一个对象，JIT会将这些锁合并
        new Thread( () -> {
            synchronized(objectLock){
                System.out.println("11111");
            }
            synchronized(objectLock){
                System.out.println("22222");
            }
            synchronized(objectLock){
                System.out.println("33333");
            }
            synchronized(objectLock){
                System.out.println("44444");
            }
        } ,"t1").start();
    }
}
