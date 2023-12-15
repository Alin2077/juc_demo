package Future;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureAPI3Demo {
    
    public static void main(String[] args) {
        
        // CompletableFuture.supplyAsync(() -> {
        //     return 1;
        // }).thenApply(f -> {
        //     return f + 2;
        // }).thenApply(f -> {
        //     return f + 3;
        // })
        // .thenAccept(r -> {
        //     System.out.println(r);
        // });
        // .thenAccept(System.out::println);

        // System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {}).join());  //结果是null
        // System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenAccept(r -> System.out.println(r)).join());// 结果是resultA  nll
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenApply(r -> r + "-resultB").join()); //结果是 resultA-resultB
        
    }

}

/**
 * void thenRun(Runnable runnable) 任务A执行完了执行B,并且B不需要A的结果
 * T thenApply(Function) 任务A执行完了执行B B需要A的结果 B有返回值
 * void thenAccept(Consumer action) 任务A执行完了执行B B需要A的结果 但是B无返回值
 */
