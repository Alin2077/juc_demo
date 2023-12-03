import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPI2Demo {
 
    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        // CompletableFuture.supplyAsync(() -> {
        //     try {
        //         TimeUnit.SECONDS.sleep(1);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //     System.out.println("111");
        //     return 1;
        // },threadPool).thenApply(f -> {
        //     System.out.println("222");
        //     return f + 2;
        // }).thenApply(f -> {
        //     System.out.println("333");
        //     return f +3 ;
        // }).whenComplete((v,e) -> {
        //     if(e == null){
        //         System.out.println("-----result: "+v);
        //     }
        // }).exceptionally(e -> {
        //     e.printStackTrace();
        //     return null;
        // });

        CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("111");
            return 1;
        },threadPool).handle((f,e) -> {
            int i = 10/0;
            System.out.println("222");
            return f + 2;
        }).handle((f,e) -> {
            System.out.println("333");
            return f +3 ;
        }).whenComplete((v,e) -> {
            if(e == null){
                System.out.println("-----result: "+v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

        System.out.println(Thread.currentThread().getName()+"-----do other things");

        threadPool.shutdown();
    }
}
