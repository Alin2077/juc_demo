import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureWirhPoolDemo {
    
    public static void main(String[] args) {
        
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);

        try {
            CompletableFuture<Void> thenRun = CompletableFuture.supplyAsync(() -> {
                            try {TimeUnit.MILLISECONDS.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
                            System.out.println("task1 is doing on\t"+Thread.currentThread().getName());
                            return "over";
                        },newFixedThreadPool).thenRunAsync(() ->{
                            try {TimeUnit.MILLISECONDS.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
                            System.out.println("task2 is doing on\t"+Thread.currentThread().getName());
                        }).thenRun(() ->{
                            try {TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                            System.out.println("task3 is doing on\t"+Thread.currentThread().getName());
                        }).thenRun(() ->{
                            try {TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                            System.out.println("task4 is doing on\t"+Thread.currentThread().getName());
                        });
        
            System.out.println(thenRun.get(2,TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            newFixedThreadPool.shutdown();
        }
    }
}

/** 
 * thenRun() 与前一个任务用同一个线程池   ----如果上一个任务处理得太快 系统可能会基于处理最快的原则使用main线程执行后面的任务
 * thenRunAsync() 会另外选择线程池
 * 
 * thenApply、thenAccept与thenRun类似
 */