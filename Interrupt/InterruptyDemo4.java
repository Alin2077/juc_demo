package Interrupt;


public class InterruptyDemo4 {
    
    public static void main(String[] args) {
        
        System.out.println(Thread.currentThread().getName()+ "\t "+Thread.interrupted());
        System.out.println(Thread.currentThread().getName()+ "\t "+Thread.interrupted());
        System.out.println("-----1");
        Thread.currentThread().interrupt(); //设置为true
        System.out.println("----2");
        System.out.println(Thread.currentThread().getName()+ "\t "+Thread.interrupted());
        System.out.println(Thread.currentThread().getName()+ "\t "+Thread.interrupted());
    }
}
