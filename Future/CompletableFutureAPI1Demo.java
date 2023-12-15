package Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletableFutureAPI1Demo {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "over";
                });

        // System.out.println(supplyAsync.get()); 可能会抛出异常
        // System.out.println(supplyAsync.get(2L, TimeUnit.SECONDS)); 超时获取不到则抛出异常
        // System.out.println(supplyAsync.join()); //不会抛出异常,作用与get一样
        // String now = supplyAsync.getNow("xxx");  //调用getNow时,如果计算完成了,则返回结果,如果没有玩成则返回传入的参数
        // boolean complete = supplyAsync.complete("xxx");  //调用时如果计算未完成则将传入的参数作为结果,此时用join或get方法会获取到传入的参数
        
    }
}
