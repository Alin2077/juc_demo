package ThreadLocal;

class MyObject{

    //这个方法一般不用复写
    @Override
    protected void finalize() throws Throwable{
        //在对象被不可撤销的丢弃之前执行清理操作
        System.out.println("------invoke finalize");
    }
}

public class ReferenceDemo {

    public static void main(String[] args) {
        //强引用
        // MyObject myObject = new MyObject();
        // System.out.println("gc before: "+myObject);
        // myObject = null;
        // System.gc(); //人工开始gc，一般不用
        // System.out.println("gc after: "+myObject);

        //软引用
        // SoftReference<MyObject> softReference = new SoftReference<>(new MyObject());
        // System.out.println("----softReference: "+softReference.get());
        // System.gc();
        // try {
        //     TimeUnit.SECONDS.sleep(1);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // System.out.println("----gc after rom enough: "+softReference.get());

        //弱引用
        // WeakReference<MyObject> weakReference = new WeakReference<>(new MyObject());
        // System.out.println("----gc before: "+weakReference.get());
        // System.gc();
        // try {
        //     TimeUnit.SECONDS.sleep(1);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // System.out.println("-----gc after: "+weakReference.get());

        //虚引用
        // MyObject myObject = new MyObject();
        // ReferenceQueue<MyObject> queue = new ReferenceQueue<>();
        // PhantomReference<MyObject> phantomReference = new PhantomReference<>(myObject, queue);
        // System.out.println("-----phantom: "+phantomReference.get());
        // System.gc();
        // new Thread( () -> {
        //     while (true) {
        //         Reference<? extends MyObject> poll = queue.poll();
        //         if(poll != null){
        //             System.out.println("----join queue");
        //             break;
        //         }
        //     }
        // } ,"t1").start();
    }
}