

public class LockSyncDemo {
    

    Object object = new Object();

    // public void m1(){
    //     synchronized (object){
    //         System.out.println("hello synchronized");
    //     }
    // }

    // public synchronized void m2(){
    //     System.out.println("hello synchronized");
    // }

    public static synchronized void m3(){
        System.out.println("hello synchronized");
    }

    public static void main(String[] args) {
        
    }
    
}
