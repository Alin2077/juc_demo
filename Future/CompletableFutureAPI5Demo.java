package Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPI5Demo {
    
    public static void main(String[] args) {
        
        CompletableFuture<Integer> supplyAsync1 = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName()+"\t --- start");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 10;
                });
  
        CompletableFuture<Integer> supplyAsync2 = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName()+"\t --- start");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 20;
                });

        CompletableFuture<Integer> result = supplyAsync1.thenCombine(supplyAsync2, (x,y) -> {
            System.out.println("combine start ");
            return x + y;
        });

        System.out.println(result.join());
    }

}
