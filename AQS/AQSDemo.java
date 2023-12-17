package AQS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AQSDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        //A B C 三个顾客，去银行办理业务，A先到，此时窗口空无一人，他优先获得办理的机会
        //A 耗时严重，长期占用窗口
        new Thread( () -> {
            lock.lock();
            try{
                System.out.println("----come in A");
                try {
                    TimeUnit.MINUTES.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally{
                lock.unlock();
            }
        } ,"A").start();

        //B是第二个顾客，B一看到受理窗口被A占用，只能去候客区等候，进入AQS队列，等待着A办理完成，
        //尝试去抢占受理窗口
        new Thread(() -> {
            lock.lock();
            try{
                System.out.println("----come in B");
            }finally{
                lock.unlock();
            }
        } ,"B").start();

        //C是第三个顾客，C一看到受理窗口被A占用，只能去候客区等候，进入AQS队列，等待着A办理完成，
        //尝试去抢占受理窗口，前面是B顾客，FIFO
        new Thread(() -> {
            lock.lock();
            try{
                System.out.println("----come in  C");
            }finally{
                lock.unlock();
            }
        } ,"C").start();
    }
}