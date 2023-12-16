package SynchronizedUp;

public class LockClearUPDemo {
    
    static Object objectLock = new Object();
    public static void main(String[] args) {
        
        LockClearUPDemo lockClearUPDemo = new LockClearUPDemo();

        for (int i = 0; i < 10; i++) {
            new Thread( () -> {
                lockClearUPDemo.m1();
            } ,"t"+i).start();
        }
    }
    public void m1(){
        // synchronized(objectLock){
        //     System.out.println("------hello LockClearUPDemo");
        // }

        //锁消除问题，JIT编译器会无视它，synchronized(o)，每次new出来的，不存在了
        Object object = new Object();

        synchronized(object){
            System.out.println("------hello LocakClearUPDemo"+"\t"+object.hashCode()+"\t"+objectLock.hashCode());
        }
    }
}
