package CAS;
import java.util.concurrent.TimeUnit;

/**
 * volatile特点----可见性 有序性
 * 当写一个volatile变量时 JMM会把该线程对应的本地内存中的共享变量立即刷新回主内存
 * 当读一个volatile变量时 JMM会把该线程对应的本地内存设置为无效 重新到主内存中读
 * 
 * 为什么volatile为什么保证两大特性?  ------- 内存屏障  --- 类似路障
 * 
 * 内存屏障：   ---- 其实就是一种JVM指令 会在编译时插入特定的指令 实现禁止重排序
 * CPU或编译器在对内存随机访问的操作中的一个同步点 使得此点之前的所有读写操作都执行后才可以执行此点之后的操作
 *                      内存屏障之前的所有写操作都要回写到主内存
 *                      内存屏障之后的所有读操作都能获得内存屏障之前所有写操作的最新结果
 * 
 * 内存屏幕的底层细分为四种： ---- 也是插入内存屏障的四个规则
 *      屏障类型        指令示例                    说明 
 *      Loadload       Load1;Loadload,Load2        保证load1的读取操作在load2及后续读取操作之前执行
 *      StoreStore     Store1;StoreStore;Store2    在store2及其后的写操作执行前 保证store1的写操作已刷新到主内存中
 *      LoadStore      Load1;LoadStore;Store2      在store2及其后的下操作执行前 保证load1的读操作已读取结束
 *      StoreLoad      Store1;StoreLoad;Load2      保证store1的写操作已刷新到主内存之后 load2及其后的读操作才能执行
 *顺序示例   volatile读   Loadload屏障   LoadStore屏障   普通读写
 *            普通读写    StoreStore屏障 volatile写      StoreLoad屏障 
 * 
 * 
 * ****重排序有可能影响程序的执行和实现 因此 我们有时候希望JVM不要重排序
 * ****对于编译器的重排序 JMM会根据重排序的规则 禁止t特定类型的编译器重排序
 * ****对于处理器的重排序 Java编译器在生成指令序列的适当位置 插入内存屏障指令 来禁止特定类型的处理器排序
 * 
 * volatile变量规则：  第一个操作       第二个操作：普通读写   第二个操作：volatile读   第二个操作：volatile写
 *                     普通读写         可重排                 可重排                 不可重排
 *                     volatile读       不可重排               不可重排              不可重排
 *                      volatile写      可重排                 不可重排               不可重排
 * 
 * 
 * volatile不提供原子性!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class VolatileDemo {

    // static  boolean flag = true;
    static volatile boolean flag = true;

    public static void main(String[] args) {
        
        MyNumber myNumber = new MyNumber();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myNumber.addPlusPlus();
                }
            } ,String.valueOf(i)).start();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(myNumber.number);

    }
    
    public static void m1(){

    }
}

class MyNumber{
    volatile int number;

    public void addPlusPlus(){
        number ++;
    }
}