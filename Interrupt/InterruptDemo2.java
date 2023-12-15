package Interrupt;
import java.util.concurrent.TimeUnit;

public class InterruptDemo2 {

    public static void main(String[] args) {
        
        //实例方法interrupt()仅仅时设置线程的中断状态为true 不会停止线程
        Thread t1 = new Thread( () -> {
            for(int i = 1; i <= 300; i++){
                System.out.println("----- "+i);
            }
            System.out.println("t1 interrupt is 03"+ Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();

        System.out.println("t1 interrupt is 01"+ t1.isInterrupted());//false

        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.interrupt();
        System.out.println("t1 interrupt is 02"+ t1.isInterrupted());

        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("t1 interrupt is 04"+ t1.isInterrupted());  //为什么是false了?--------因为线程已经不活动了
    }
}
