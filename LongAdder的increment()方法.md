## add(long x)

```java
public void add(long x) {
    Cell[] as; long b, v; int m; Cell a;
    if ((as = cells) != null || !casBase(b = base, b + x)) {
        boolean uncontended = true;
        if (as == null || (m = as.length - 1) < 0 ||
            (a = as[getProbe() & m]) == null ||
            !(uncontended = a.cas(v = a.value, v + x)))
            longAccumulate(x, null, uncontended);
    }
}
```
* as表示cells引用
* b表示获取的base值
* v表示期望值
* m表示cells数组的长度
* a表示当前线程命中的cell单元格
1. 当只有一个线程时，*(as = cells) != null*为false<font color="green">无需新增cell，cells为null</font>；*!casBase(b = base, b + x)* 为false<font color='green'>base为初始值0，b+x为1，cas操作成功</font>
2. 当线程足够多时，*(as = cells) != null*为false<font color="green">还没有扩容</font>；*!casBase(b = base, b + x)* 有可能为true<font color='green'>线程多，抢占资源，cas操作可能失败</font>；当cas操作出现失败时，进入循环体内部。
3. 第一次进入循环体内部，*uncontended* 这个变量用来标志是否冲突；as为null，直接下一层循环体，进入longAccumulate方法，新建cell数组<font color='green'>源码里写死了cell数组的长度为2，所以cell数组永远是2次幂</font>
4. 扩容后；*(as = cells) != null* 恒为true，*as == null* 恒为false，*(m = as.length - 1) < 0* 恒为false；*(a = as[getProbe() & m])* 即是随机获取cell数组上的槽位index<font color='green'>为null代表该槽位没有数据，直接进入longAccumulate方法进行初始化槽位</font>；*uncontended = a.cas(v = a.value, v + x)* 进行cas操作，当cas操作成功时，不会进入longAccumulate方法，反之则进入再扩容
#### 总结
1. 最初无竞争时只更新base
2. 如果竞争base失败，首次新建一个Cell[]数组
3. 当多个线程竞争同一个Cell比较激烈时，可能就要对Cell[]扩容
## longAccumulate(long x,LongBinaryOperator fn,boolean wasUncontentended)
```java
    final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                //扩容   ---具体代码后面讲解
            }
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                // Initialize table   ---具体代码后面讲解 CASE1
            }
            else if (casBase(v = base, ((fn == null) ? v + x :
                                        fn.applyAsLong(v, x))))
                break;                          // Fall back on using base
        }
    }
```
* long x 代表需要增加的值，一般默认都是1
* LongBinaryOpertor fn 代表传递的方法，默认都是null
* wasUncontended 是竞争标识，如果是false则代表有竞争。只有cells初始化之后，并且当前线程CAS失败，才会是false
#### getProbe()方法
unsafe类的方法，可以理解为得到线程的hash值
1. 通过 *getProbe()* 获取到线程的hash值；如果获取失败则重置hash值，好比一个全新的全新的线程，所以同时会将 *wasUncontended* 设置为true
2. 分为3种不同的情况不同处理

### CASE1：cells已经被初始化了
```java
if ((as = cells) != null && (n = as.length) > 0) {
    if ((a = as[(n - 1) & h]) == null) { //获取到某个坑位，如果坑位为null
        if (cellsBusy == 0) {       //坑位没锁
            Cell r = new Cell(x);   //new一个cell
            if (cellsBusy == 0 && casCellsBusy()) { //double check，然后获取锁
                boolean created = false;
                try {               
                    Cell[] rs; int m, j;
                    if ((rs = cells) != null &&
                        (m = rs.length) > 0 &&
                        rs[j = (m - 1) & h] == null) {
                        rs[j] = r;      //将数据存进去
                        created = true;
                    }
                } finally {
                    cellsBusy = 0;   //释放锁
                }
                if (created)
                    break;
                continue;           
            }
        }
        collide = false;
    }
    else if (!wasUncontended)       //代表有竞争了
        wasUncontended = true;     //重新竞争 
    else if (a.cas(v = a.value, ((fn == null) ? v + x :
                                    fn.applyAsLong(v, x))))
        break;               //写进数据
    else if (n >= NCPU || cells != as)  //已经是CPU核心数上线则不扩容
        collide = false;            
    else if (!collide)    //允许扩容
        collide = true;
    else if (cellsBusy == 0 && casCellsBusy()) {  //扩容  抢锁
        try {
            if (cells == as) {      
                Cell[] rs = new Cell[n << 1]; //扩容了
                for (int i = 0; i < n; ++i)
                    rs[i] = as[i];   //将数据copy过去
                cells = rs;
            }
        } finally {
            cellsBusy = 0;
        }
        collide = false;
        continue;                   
    }
    h = advanceProbe(h);
}
```
1. 判断当前线程hash后指向的数据位置元素是否为空；如果为空则将Cell数据放入数组中，跳出循环；如果不空则继续循环
2. cells已经初始化了，当前线程竞争失败，重新设置wasUncontended为false，再次去竞争
3. 当前线程对应的数组中有数据，也重置过hash值，这时通过CAS尝试进行累加，如果成功则直接跳出循环
4. 如果cell数组长度大于CPU最大数量，不可扩容，再次重置hash重新尝试。
5. 如果扩容意向是false，则修改为true，然后重新hash兵循环；如果长度已经大于CPU核数，会再次将扩容意向设置为false(见上一步)
6. 按位左移一位来进行扩容，扩容过后将之前数组的元素拷贝到新数组中，再重新循环执行                                                                                            
### CASE2：cells没加锁且没有初始化，则尝试加锁并初始化
```java
    if (cellsBusy == 0 && cells == as && casCellsBusy()) {
        boolean init = false;
        try {                           // Initialize table
            if (cells == as) {
                Cell[] rs = new Cell[2];
                rs[h & 1] = new Cell(x);
                cells = rs;
                init = true;
            }
        } finally {
            cellsBusy = 0;
        }
        if (init)
            break;
    }
```
* cellsBusy用来标识有无锁，0代表无锁，1代表有锁
* 我们发现代码中有 *cells == as* 和 *cells == as* 两个判断，因为场景是并发的，所以不进行double check会存在多new出cells数组的情况
1. *cellsBusy == 0 && cells == as && casCellsBusy()* 用来确认当前cells没有其他的锁，然后自己获取到锁
2. *Cell[] rs = new Cell[2]* 进行新建cells数组； *rs[h & 1] = new Cell(x)* 将x随机放入其中一个槽位
### CASE3：cells正在进行初始化，则尝试直接在base上进行累加
```java
if (casBase(v = base, ((fn == null) ? v + x :
                                        fn.applyAsLong(v, x)))){
        break;
}  
```
* 多个线程CAS一致失败，则会在base上累加；也即多个cell在初始化，同时又有很多线程要进行操作
## sum()
将所有cell的值与base中的值相加即可