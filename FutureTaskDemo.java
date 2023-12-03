import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {

    public static void main(String[] args) {
        
     FutureTask<String> futureTask = new FutureTask<>(new MyThread());

     Thread t1 = new Thread(futureTask,"t1");

     t1.start();

     try {
        System.out.println(futureTask.get());
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    }
    
}

class MyThread implements Callable<String>{

    @Override
    public String call() throws Exception {
        
        System.out.println("into Callable Func");

        return "return Callable Func";
        
    }

}