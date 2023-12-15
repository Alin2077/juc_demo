package Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureFirstDemo {
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);

        // CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
        //             System.out.println(Thread.currentThread().getName());
        //             try {
        //                 TimeUnit.SECONDS.sleep(1);
        //             } catch (InterruptedException e) {
        //                 e.printStackTrace();
        //             }
        //         },newFixedThreadPool);
        
        // System.out.println(runAsync.get());

        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
                        System.out.println(Thread.currentThread().getName());
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "task over";
                },newFixedThreadPool);
        System.out.println(supplyAsync.get());

        newFixedThreadPool.shutdown();
    }

}
