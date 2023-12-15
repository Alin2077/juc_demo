package Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTaskGetDemo {
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        FutureTask<String> futureTask = new FutureTask<String>(() -> {
                    System.out.println(Thread.currentThread().getName()+"\t -------come in");
                    //暂停几秒
                    TimeUnit.SECONDS.sleep(5);
                    return " task over";
                });
        Thread t1 = new Thread(futureTask,"t1");
        t1.start();

        System.out.println(Thread.currentThread().getName()+"\t ---do other things");
        // System.out.println(futureTask.get(3,TimeUnit.SECONDS));

        while (true) {
            if(futureTask.isDone()){
                System.out.println(futureTask.get());
                break;
            }else{
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.println("processing...");
            }
        }

    }

    /**
     * 1.get容易导致程序阻塞，一般建议放在程序最后，一旦调用会阻塞当前线程等待future的返回
     * 2.假如我不愿意等待很长时间，我希望过时不候，可以自动离开
     * 3.isDone轮询
     */
    
}
