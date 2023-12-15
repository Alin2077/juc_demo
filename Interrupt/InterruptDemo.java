package Interrupt;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class InterruptDemo {
    

    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) {
        
        Thread t1 = new Thread( () -> {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    System.out.println(Thread.currentThread().getName()+"\t interrupt has been edit, so stop");
                    break;
                }
                System.out.println(" t1 ------ hello interrupt");
            }
        },"t1");
        t1.start();

        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            t1.interrupt();
        },"t2").start();

    }

    private static void m1(){
        new Thread( () -> {

            while (true) {
                // if(isStop){
                if(atomicBoolean.get()){
                    System.out.println(Thread.currentThread().getName()+"\t isStop has been edit, so stop");
                    break;
                }
                System.out.println(" t1 ------ hello volatile");
            }

        },"t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread( () -> {
            // isStop = true;
            atomicBoolean.set(true);
        },"t2").start();
    }

}
