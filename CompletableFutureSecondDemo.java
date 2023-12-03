import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 如果解决了FutureTask的阻塞和轮询问题
 */
public class CompletableFutureSecondDemo {
    
    public static void main(String[] args)  {
        
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
    
        try {
            CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
                        System.out.println(Thread.currentThread().getName()+"\t ---come in");
                        int nextInt = ThreadLocalRandom.current().nextInt(10);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-----after 1 s: "+ nextInt);
                        if(nextInt > 5){
                            int i = 10/0;
                        }
                        return nextInt;
                    },threadPool).whenComplete((v,e) -> {
                        if(e == null){
                            System.out.println("----complete , update value: " + v);
                        }
                    }).exceptionally(e -> {
                        e.printStackTrace();
                        System.out.println("have a exceptionally");
                        return null;
                    });
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        System.out.println(Thread.currentThread().getName()+"is do other things");

        // try {
        //     TimeUnit.SECONDS.sleep(3);
        // } catch (InterruptedException e1) {
        //     e1.printStackTrace();
        // }

    }

    private void future1() throws InterruptedException, ExecutionException{

        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName()+"\t ---come in");
                    int nextInt = ThreadLocalRandom.current().nextInt(10);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("-----after 1 s"+ nextInt);
                    return nextInt;
                });
        
        System.out.println(Thread.currentThread().getName()+"is do other things");
        
        System.out.println(supplyAsync.get());
        
    }

}
