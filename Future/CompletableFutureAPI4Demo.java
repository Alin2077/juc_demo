package Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPI4Demo {
    
    public static void main(String[] args) {

        CompletableFuture<String> playA = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "playA";
                });

        CompletableFuture<String> playB = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "playB";
                });

        
        CompletableFuture<String> applyToEither = playA.applyToEither(playB, f -> {
                    return f + "is winer";
                });

        System.out.println(Thread.currentThread().getName()+"\t"+applyToEither.join());
    }
}
