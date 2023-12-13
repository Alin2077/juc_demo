package ThreadLocal;

import java.util.Random;
import java.util.concurrent.TimeUnit;

class House{
    int saleCount = 0;
    public synchronized void saleHouse(){
        saleCount++;
    }

    ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial( () -> 0 );

    public void saleVolumeByThreadLocal(){
        saleVolume.set(1+saleVolume.get());
    }
}

public class ThreadLocalDemo {
    
    public static void main(String[] args) {
        House house = new House();

        for (int i = 0; i < 5; i++) {
            new Thread( () -> {
               int size = new Random().nextInt(5)+1;
            //    System.out.println(size);
               try {
                for (int j = 0; j < size; j++) {
                     house.saleHouse();
                     house.saleVolumeByThreadLocal();
                }
                System.out.println(Thread.currentThread().getName()+"\t"+"sale: "+house.saleVolume.get());
               } finally {
                    house.saleVolume.remove();  //每次用完记得remove。不然高并发下可能出现逻辑混乱和内存泄漏(例如线程池)
               }
            } ,"t"+i).start();
        }

        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName()+"\t"+"sale Count Sum："+house.saleCount);

    }
}
