import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * 原子类的分类：
 * 基本类型原子类：AtomicInteger、AtomicBoolean、AtomicLong
 * 数组类型原子类：AtomicIntegerArray、AtomicBooleanArray、AtomicLongArray
 * 引用类型原子类：AtomicReference、AtomicStampedReference（根据版本号可以知道修改过几次 version）、AtomicMarkableReference（简化了版本号，只知道有没有被修改过 true/false）
 * 对象的属性修改原子类：AtomicIntegerFieldUpdater、AtomicLongFieldUpdater、AtomicReferenceFieldUpdater
 * 原子操作增强类：DoubleAccumulator、DoubleAdder、LongAccumulator、LongAdder 
 */

 /**
  * 我们发现LongAdder等1.8之后新加入的类在数据量大的时候性能比之前的类高很多   ----- 为什么?
  * LongAdder是Striped64的子类
  * Striped64 中有着 Cell内部类 NCPU属性(运行环境的硬件CPU核数) base属性(long 类型) cells属性(Cell内部类的数组) cellsBusy属性(cells数组内处于自旋的Cell数组)
  *       base：类似AtmicLong中全局的value值。在没有竞争情况下数据直接累加到base上，或者cells扩容时，也需要将数据写入到base上
  *       collide：表示扩容意向，false一定不会扩容，true可能会扩容
  *       cellsBuy：初始化cells或者扩容cells需要获取锁。 0表示无锁状态 1表示已经持有了锁
  *       casCellsBusy：通过CAS操作修改cellsBusy的值，CAS成功代表获取锁，返回true
  *       NCPU：当前计算机的CPU核心数，Cell数组扩容时会使用到
  *       getProbe()：获取当前线程的hash值
  *       advanceProbe()：充值当前线程的hash值
  * 一句话表达： LongAdder的基本思路即使分散热点，将value值分散到一个Cell数组中，不同线程会命中到数组的不同槽中，各个线程只对自己槽中的那个值进行CAS操作，
  * 这样热点就被分散了，冲突的概率就小很多，如果要获取到真正的long值，只要将各个槽中的变量累加即可。
  * 在LongAdder类中真正实现累加的是-------------longAccumulate()方法---------
  */

class MyNumberR{
    AtomicInteger atomicInteger = new AtomicInteger();

    public MyNumberR(){
        
    }

    public void addPlusPlus(){
        atomicInteger.getAndIncrement();
    }

    public int get(){
        return atomicInteger.get();
    }

}

class BankAccount{
    String bankName = "CCB";
    public volatile int money = 0;

    public synchronized  void add(){
        money ++;
    }

    AtomicIntegerFieldUpdater<BankAccount> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

    //不加synchronized，保证高性能原子性
    public void transMoney(BankAccount bankAccount){
        fieldUpdater.getAndIncrement(bankAccount);
    }
}

class MyVar{
    public volatile Boolean isInit = Boolean.FALSE;

    AtomicReferenceFieldUpdater<MyVar,Boolean> referenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");

    public void init(MyVar myVar){
        if(referenceFieldUpdater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)){
            System.out.println(Thread.currentThread().getName()+"\t"+"-----start init, need 3 seconds");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"\t"+"----over init");
        }else{
            System.out.println(Thread.currentThread().getName()+"\t"+"----other init");
        }
    }
}

class ClickNumber{
    int number = 0;

    public synchronized void clickBySynchronized(){
        number ++;
    }

    AtomicLong atomicLong = new AtomicLong(0);

    public void clickByAtomicLong(){
        atomicLong.getAndIncrement();
    }

    LongAdder longAdder = new LongAdder();
    
    public void clickByLongAdder(){
        longAdder.increment();
    }

    LongAccumulator longAccumulator = new LongAccumulator((x,y) -> x+y, 0);

    public void clickByLongAccumulator(){
        longAccumulator.accumulate(1);
    }
}

public class AtomicDemo {
    
    public static final int SIZE = 50;

    static AtomicMarkableReference markableReference = new AtomicMarkableReference(100, false);

    public static final int _1W = 10000;

    public static void main(String[] args) {

        

        // updater();
        // testLongAdder();
        testLongAdderPlus();
    }

    public static void testLongAdderPlus(){
        ClickNumber clickNumber = new ClickNumber();
        long startTime;
        long endTime;

        CountDownLatch countDownLatch1 = new CountDownLatch(SIZE);
        CountDownLatch countDownLatch2 = new CountDownLatch(SIZE);
        CountDownLatch countDownLatch3 = new CountDownLatch(SIZE);
        CountDownLatch countDownLatch4 = new CountDownLatch(SIZE);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100 * _1W; j++) {
                        clickNumber.clickBySynchronized();
                    }
                } finally {
                    countDownLatch1.countDown();
                }

            }, "t" + String.valueOf(i)).start();
        }
        try {
            countDownLatch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("use: "+(endTime-startTime)+" ms \t number: "+clickNumber.number);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100 * _1W; j++) {
                        clickNumber.clickByAtomicLong();
                    }
                } finally {
                    countDownLatch2.countDown();
                }

            }, "t" + String.valueOf(i)).start();
        }
        try {
            countDownLatch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("use: "+(endTime-startTime)+" ms \t number: "+clickNumber.atomicLong.get());

        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100 * _1W; j++) {
                        clickNumber.clickByLongAdder();
                    }
                } finally {
                    countDownLatch3.countDown();
                }

            }, "t" + String.valueOf(i)).start();
        }
        try {
            countDownLatch3.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("use: "+(endTime-startTime)+" ms \t number: "+clickNumber.longAdder.sum());

        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 100 * _1W; j++) {
                        clickNumber.clickByLongAccumulator();
                    }
                } finally {
                    countDownLatch4.countDown();
                }

            }, "t" + String.valueOf(i)).start();
        }
        try {
            countDownLatch4.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("use: "+(endTime-startTime)+" ms \t number: "+clickNumber.longAccumulator.get());
    }

    public static void testLongAdder(){
        LongAdder longAdder = new LongAdder();

        longAdder.increment();
        longAdder.increment();
        longAdder.increment();

        System.out.println(longAdder.sum());

        LongAccumulator longAccumulator = new LongAccumulator( (x,y) -> x+y ,0);
        longAccumulator.accumulate(1);
        longAccumulator.accumulate(3);
        System.out.println(longAccumulator.get());

    }

    public static void update2(){
        MyVar myVar = new MyVar();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                myVar.init(myVar);
            } ,"t"+i).start();
        }
    }

    /**
     * 对象的属性修改原子类
     * 更加细度的原子更新
     * 使用目的：以一种线程安全的方式操作非线程安全对象内的某些字段 **********!!!!!!!!
     *          在我们编程的时候，某些类中经常是只有个别字段会经历并发更新,如果用synchronized之类的锁，针对的是对象，粒度太大
     * 使用要求：1.更新的对象属性必须使用public volatile修饰符。
     *          2.因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性。
     * 
     */
    public static void updater(){

        BankAccount bankAccount = new BankAccount();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new Thread( () -> {
                try{
                    for (int j = 0; j < 1000; j++) {
                        // bankAccount.add();
                        bankAccount.transMoney(bankAccount);
                    }
                }finally{
                    countDownLatch.countDown();;
                }
            } ,"t"+i).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+"\t"+"result: "+bankAccount.money);

    }

    public static void refer(){
         new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName()+"\t"+marked);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            markableReference.compareAndSet(100, 1000, marked, !marked);
        } ,"t1").start();

        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName()+"\t"+marked);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean compareAndSet = markableReference.compareAndSet(100, 2000, marked, !marked);
            System.out.println(Thread.currentThread().getName()+"\t"+compareAndSet);
            System.out.println(Thread.currentThread().getName()+"\t"+markableReference.isMarked()+"\t"+markableReference.getReference());
        } ,"t2").start();
    }
    
    public static void array(){
        // AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[5]);
        // AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(5);
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1,2,3,4,5});

        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.println(atomicIntegerArray.get(i));
        }
        System.out.println();

        int tmpInt = 0;

        tmpInt = atomicIntegerArray.getAndSet(0, 1122);
        System.out.println(tmpInt+"\t"+atomicIntegerArray.get(0));

        tmpInt = atomicIntegerArray.getAndIncrement(0);
        System.out.println(tmpInt+"\t"+atomicIntegerArray.get(0));
    }

    public static void integer(){
        MyNumberR myNumber = new MyNumberR();
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);
        
        for (int i = 0; i < SIZE; i++) {
            new Thread( () -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        myNumber.addPlusPlus();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            },"t"+i).start();
        }

        // try {
        //     TimeUnit.SECONDS.sleep(2);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName());
        System.out.println(myNumber.get());
    }
    
}
