import java.util.concurrent.TimeUnit;

/**
 * 通过不同情况的synchronized组合比较,认识不同情况下的synchronized特点
 * 
 * 13组合--------1先执行    ??? 第一个线程获取锁之后 第二个线程需要等待第一个线程释放锁
 * 14组合--------4先执行    ??? 不是同一个对象 获取的是不同的锁  ------>  普通的synchronized是对象锁
 * 17组合--------7先执行    ??? 7不需要获取锁 不受影响
 * 
 * 25组合--------2先执行    ??? 与13组合类似
 * 26组合--------2先执行    ??? 不是同一个对象 但仍然遵守锁的获取顺序  -------> static的synhronized是类锁
 * 27组合--------7先执行    ??? 与17组合类似
 * 
 * 23组合--------3先执行    ??? 类锁和对象锁 是两种锁
 * 24组合--------4先执行    ??? 类锁和对象锁 是两种锁 而且此时是不同对象
 * 
 * 245组合--------??? 应该是425  true
 * 246组合--------??? 应该是426  true
 * 135组合--------??? 应该是513  false   35是在同一个线程中执行的 虽然5只需要获取类锁 但由于3需要获取对象锁 故仍需要等待1执行完成
 *                                      如果是153组合呢? 会不会因为5在前 可以先获取类锁?  实践证明可以
 */
public class SynchronizedFristDemo {


    public static void main(String[] args) {
        
        Demo demo1 = new Demo();
        Demo demo2 = new Demo();

        new Thread(() -> {

            demo1.commonFunc1();  // 1
            // demo1.staticFunc1();  // 2

        }).start();
        
        //主线程等待200ms,为了让上一个线程线获取到锁
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            demo1.staticFunc2();  //5
            demo1.commonFunc2();  //3
            // demo2.commonFunc2();  //4
            
            // demo2.staticFunc2();  //6
            // demo1.unlockFunc();   //7

        }).start();

    }
    
}

class Demo{

    public static synchronized void staticFunc1(){

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("----staticFunc1");

    }

    public static synchronized void staticFunc2(){

        System.out.println("----staticFunc2");

    }

    public synchronized void commonFunc1(){

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("----commonFunc1");

    }

    public synchronized void commonFunc2(){

        System.out.println("----commonFunc2");

    }

    public void unlockFunc(){

        System.out.println("----unlockFunc");

    }

    public void syBlock(){
        synchronized(this){
            System.out.println("this is a sychronized block");
        }
    }
    /**
     * synchronized的三种使用方法
     * 1 普通同步实例方法
     * 2 静态同步方法
     * 3 同步代码块 对括号内的对象加锁
     */

}
