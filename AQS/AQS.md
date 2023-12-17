## AbstractQueuedSynchronizer前置知识
1. 公平锁和非公平锁
2. 可重入锁
3. 自旋思想
4. LockSupport
5. 数据结构之双向链表
6. 设计模式之模板设计模式
## AQS入门级别理论知识
1. 是什么？
    * 字面意思：抽象的队列同步器
    * 技术解释：
        1. 是用来实现锁或者其他同步器组件的公共基础部分的抽象实现，是<font color='green'>重量级基础框架及整个JUC体系的基石，主要用于解决锁分配给“谁”的问题</font>。
        2. 整体就是一个抽象的FIFO（先进先出）队列来完成资源获取线程的排队工作，并通过一个原子int类变量表示持有锁的状态。
2. 为什么AQS是JUC的基石？
    * 与AQS有关的常见类：ReentrantLock、CountDownLatch、ReentrantReadWriteLock、Semaphore、CyclicBarrier...
    * 进一步理解锁和同步器的关系
        1. 锁，面向锁的使用者：定义了程序员和锁交互的使用层API，隐藏了实现细节，调用即可
        2. 同步器，面向锁的实现者：Java并发大神DougLee，提出同一规范并简化了锁的实现，将其抽象出来屏蔽了同步状态管理、同步队列的管理和维护、阻塞线程排队和通知、唤醒机制等，是一切锁和同步组件实现的----公共基础部分。
3. 能干嘛？
    * 加锁会导致阻塞：有阻塞就需要排队，实现排队必然需要队列
    * 解释说明：
        1. 抢到资源的线程直接使用，抢不到资源的必然涉及一种<font color='green'>排队等候机制</font>。
        2. 如果共享资源被占用，就需要一定的阻塞等待唤醒机制来保证锁分配。这个机制主要用的是CLH队列的变体实现的，将暂时获取不到锁的线程加入到队列中，这个队列就是AQS同步队列的抽象表现。它将要请求共享资源的线程及自身的等待状态封装成队列的结点对象Node，通过CAS、自旋以及LockSupport.park()的方式，维护state变量的状态，使并发达到同步的效果。
    * 小总结：AQS同步队列的基本结构如图![AQS队列基础结构](./AQS队列基础结构.png)
## AQS源码分析前置知识
1. AQS内部体系结构
    * AQS自身：
        1. AQS的 private volatile int state 变量：类似于银行办理业务的受理窗口状态；0就是没有人，自由状态可以办理；大于等于1，有人占用窗口，等着去。
        2. AQS的CLH队列变体（CLH是单向链表，AQS使用了变体双向链表）
    * 内部类Node：
        1. Node的 volatile int waitStatus 变量：节点的等待状态，Node类中定义了常量用来表示。
        2. 内部结构：
            ```java
                static final class Node {
                    //共享模式（表示线程以共享的模式等待锁）
                    static final Node SHARED = new Node();
                    //强占模式（表示线程正在以独占的方式等待锁）
                    static final Node EXCLUSIVE = null;
                    //请求被取消，当前节点无效
                    static final int CANCELLED =  1;
                    //后继线程需要唤醒（表示线程已经准备好了，就等资源释放了）
                    static final int SIGNAL    = -1;
                    //等待condition唤醒（表示节点在等待对列中，节点线程等待唤醒）
                    static final int CONDITION = -2;
                    //共享式同步状态获取将会无条件地传播下去（当前线程处于SHARED情况下，该字段才会使用）
                    static final int PROPAGATE = -3;
                    //初始为0，状态是上面的几种
                    volatile int waitStatus;
                    //前置节点
                    volatile Node prev;
                    //后续节点
                    volatile Node next;
                    //封装的线程
                    volatile Thread thread;
                //指向下一个处于CONDITION状态的节点
                    Node nextWaiter;

                    final boolean isShared() {
                        return nextWaiter == SHARED;
                    }
                    //返回前驱节点，没有的话抛出nullpoint异常
                    final Node predecessor() throws NullPointerException {
                        Node p = prev;
                        if (p == null)
                            throw new NullPointerException();
                        else
                            return p;
                    }

                    Node() {    // Used to establish initial head or SHARED marker
                    }

                    Node(Thread thread, Node mode) {     // Used by addWaiter
                        this.nextWaiter = mode;
                        this.thread = thread;
                    }

                    Node(Thread thread, int waitStatus) { // Used by Condition
                        this.waitStatus = waitStatus;
                        this.thread = thread;
                    }
                }
            ```
## AQS源码深度讲解和分析（以ReentrantLock为例）
1. ReentrantLock详解
    * 构造方法：
        ```java
        public ReentrantLock() {
        sync = new NonfairSync();
        }

        public ReentrantLock(boolean fair) {
            sync = fair ? new FairSync() : new NonfairSync();
        }
        // FairSync 和 NonfairSync 继承自Sync类
        ```
    * lock方法：
        ```java
        public void lock() {
            sync.lock();
        }
        ```
        ```java
        static final class NonfairSync extends Sync {
            private static final long serialVersionUID = 7316153563782823691L;

            /**
             * Performs lock.  Try immediate barge, backing up to normal
             * acquire on failure.
             */
            final void lock() {
                if (compareAndSetState(0, 1))
                    setExclusiveOwnerThread(Thread.currentThread());
                else
                    acquire(1);
            }
        }
        ```
        ```java
        static final class FairSync extends Sync {

            final void lock() {
                acquire(1);
            }
        }
        ```
        ```java
        public final void acquire(int arg) {
            if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
                selfInterrupt();
        }
        ```
        可以看到lock()方法最终都需要调用到AQS内部的方法。
        ```java
        //此方法是AQS类定义的一个模板方法。FairSync类重写了此方法
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        ```
        ```java
        //此方法是AQS类定义的一个模板方法。NotfairSync类重写了此方法
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }

        //此方法位于ReentrantLock类内部
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        ```
        从上面的代码中可以看到，FairSync和NotfairSync的不同之处至于<font    color='green'>!hasQueuedPredecessors()</font>，这个方法的作用就是查看前面是否有节点：
        ```java
        public final boolean hasQueuedPredecessors() {
            // The correctness of this depends on head being initialized
            // before tail and on head.next being accurate if the current
            // thread is first in queue.
            Node t = tail; // Read fields in reverse initialization order
            Node h = head;
            Node s;
            return h != t &&
                ((s = h.next) == null || s.thread != Thread.currentThread());
        }
        ```
        * 公平锁：公平锁讲究先来后到，线程在获取锁时，如果这个锁的等待队列中已经有线程在等待，那么当前线程会进入等待队列中。
        * 非公平锁：不管是否有等待对俩，如果可以获取锁则立刻占用锁对象。也就是说队列的第一个排队线程苏醒后，不一定就是排头的这个线程获得锁，它还是需要参加竞争锁（存在线程竞争的情况下），后来的线程可能不讲武德插队抢锁了。
2. 非公平锁详解
    * lock() --> acquire(1) -->  !tryAcquire(1) && acquireQueued(addWaiter(Node.EXCLSIVE),1) --> selfInterrupt()
    * tryAcquire(1)：交由子类FairSync实现
        ```java
        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {//如果资源被释放，则直接CAS尝试抢占资源
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {//如果是当前线程持有锁
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        ```
    * addWaiter(Node.EXCLSIVE)：
        ```java
        private Node addWaiter(Node mode) {
            Node node = new Node(Thread.currentThread(), mode);//新增节点
            Node pred = tail;   //获取当前AQS队列的尾节点
            if (pred != null) { //若当前AQS队列的尾节点不为null，即AQS队列中有线程在排队
                node.prev = pred; //将新增节点的prev属性存储之前的尾节点
                if (compareAndSetTail(pred, node)) {
                    pred.next = node;
                    return node;
                }
            }
            enq(node);  //若AQS队列中没有线程则执行enq()方法
            return node;
        }
        ```
        调用enq()方法进行入队操作
        ```java
        private Node enq(final Node node) {
            for (;;) {
                Node t = tail;  //获取尾节点
                if (t == null) { // 尾节点为null，则必须new节点
                    if (compareAndSetHead(new Node()))
                        tail = head; //AQS中的tail属性存储 new 出的Node节点，这个 new 出的节点被成为哨兵节点，作用就是占位，因为new出的Node默认的waitStatus为0
                } else { //尾节点不为null
                    node.prev = t;//将新增节点的prev属性存储尾节点的属性
                    if (compareAndSetTail(t, node)) {//CAS修改AQS的tail属性，改为新增节点
                        t.next = node;  //如果修改AQS的tail属性成功，则将之前尾节点的next属性存储新增节点
                        return t;
                    }
                }
            }
        }
        ```
    * acquireQueued(addWaiter(Node.EXCLSIVE),1)：
        ```java
        final boolean acquireQueued(final Node node, int arg) {// 接收 新增节点 占用状态 两个参数
            boolean failed = true;
            try {
                boolean interrupted = false;  //中断位，默认不中断
                for (;;) {
                    final Node p = node.predecessor(); //获取新增节点的前置节点，成为P
                    if (p == head && tryAcquire(arg)) { //P是头节点 并且 再尝试抢资源成功进入
                        setHead(node);
                        p.next = null; // help GC
                        failed = false;
                        return interrupted;
                    }
                    //P不是头节点或者再尝试抢夺资源失败则往下走
                    if (shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt())
                        interrupted = true;
                }
            } finally {
                if (failed)  //异常情况退出队列
                    cancelAcquire(node);
            }
        }
        ```
        ```java
        private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
            int ws = pred.waitStatus; //获取到前置节点P的waitStatus
            if (ws == Node.SIGNAL)// 判断前置节点P的waitStatus是否等于-1
                return true;

            if (ws > 0) {
                
                do {
                    node.prev = pred = pred.prev;
                } while (pred.waitStatus > 0);
                pred.next = node;
            } else {
                //前置节点的waitStatus为0时，将waitSatus改成 -1
                compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
            }
            return false;
        }
        ```
        ```java
        private final boolean parkAndCheckInterrupt() {
            LockSupport.park(this);  //中断当前线程，实现在队列中停止
            return Thread.interrupted();
        }
        ```
        ```java
        //出现异常情况，退出AQS
        private void cancelAcquire(Node node) { //传入的是当前新增的节点
            
            if (node == null)
                return;

            node.thread = null;  //将节点的thread清空

            Node pred = node.prev;  //获取节点的上一个节点
            while (pred.waitStatus > 0) //只有上一个节点也被取消了，才会 >0
                node.prev = pred = pred.prev;

            Node predNext = pred.next;  //获取到上一个节点的下一个节点

            node.waitStatus = Node.CANCELLED;  //将当前节点的waitStatus改为1

            
            if (node == tail && compareAndSetTail(node, pred)) {
                //如果要退出的是尾节点，那么需要把前面的节点设置为尾节点
                compareAndSetNext(pred, predNext, null);
            } else {

                int ws;
                if (pred != head &&  //上一个节点不为 头节点
                    ((ws = pred.waitStatus) == Node.SIGNAL || // 上一个节点的waitStatus是-1或能改成-1
                    (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                    pred.thread != null) {
                    Node next = node.next;  //获取到当前节点的下一节点
                    if (next != null && next.waitStatus <= 0)
                        compareAndSetNext(pred, predNext, next);
                } else {
                    unparkSuccessor(node);
                }

                node.next = node;  //当前节点的node指向自己，为了GC
            }
        }
        ```
    * unlock() --> sync.release(1) --> tryRelease(arg) --> unparkSuccessor
    * sync.release(1)
        ```java
        public final boolean release(int arg) {
            if (tryRelease(arg)) {  //解锁成功则进入循环体
                Node h = head;  //获取头节点
                if (h != null && h.waitStatus != 0)
                    unparkSuccessor(h);
                return true;
            }
            return false;
        }
        ```
    * tryRelease()
        ```java
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
        ```
    * unparkSuccessor()
        ```java
        private void unparkSuccessor(Node node) { //node为AQS队列中的头节点
            
            int ws = node.waitStatus;  
            if (ws < 0)
                compareAndSetWaitStatus(node, ws, 0); //重新将头节点的waitStatus改为0

            Node s = node.next;  //获取AQS的第二个节点
            if (s == null || s.waitStatus > 0) {
                s = null;
                for (Node t = tail; t != null && t != node; t = t.prev)
                    if (t.waitStatus <= 0)
                        s = t;
            }
            if (s != null)
                LockSupport.unpark(s.thread);
        }
        ```

