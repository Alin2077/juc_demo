package Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * future配异步线程池能显著提高代码性能
 */
public class FutureTaskPoolDemo {
    
    public static void main(String[] args) {
        
        //三个任务，异步线程来处理
        long startTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        FutureTask<String> futureTask1 = new FutureTask<>(() -> {
            try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            return "task1 over";
        });

        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            try {TimeUnit.MILLISECONDS.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
            return "task1 over";
        });

        threadPool.submit(futureTask1);
        threadPool.submit(futureTask2);

        try {
            System.out.println(futureTask1.get());
            System.out.println(futureTask2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        
        try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        
        threadPool.shutdown();

        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:"+(endTime - startTime)+ " ms");

        System.out.println(Thread.currentThread().getName()+"\t ----end");
    }

    private void m1(){

        //3个任务，只有一个线程main处理
        long startTime = System.currentTimeMillis();

        try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        try {TimeUnit.MILLISECONDS.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
        try {TimeUnit.MILLISECONDS.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:"+(endTime - startTime)+ " ms");

        System.out.println(Thread.currentThread().getName()+"\t ----end");

    }

}


