## Object obj = new Object()的理解？
* where：JVM的堆中 新生代-
* what：对象头、实例数据、对齐填充
    1. 对象头：由对象标记（MarkWord）、类元信息（类型指针）组成。数组对象的对象头多了一个数组长度的信息。
        * 对象标志（底层代码中写作markOop）：存储有对象hash码、GC标记、GC次数、同步锁标记、偏向锁持有者。在64位系统中占8个字节。
        * 类元信息（底层代码中写作klassOop）：存储的是指向该队对象类元数据(klass)的首地址。在64位系统中占8个字节，但默认JVM会开启压缩指针，会被压缩到4个字节。
    2. 实例数据：存放类的属性（Field）数据信息，包括父类的属性信息。
    3. 对齐填充：确保整个对象的大小为8字节的倍数。
## MarkWord的存储结构(按锁不同分类)
<table border='1'>
<tr>
<th rowspan='2'>锁状态</th>
<th>25bit</th>
<th>31bit</th>
<th>1bit</th>
<th>4bit</th>
<th>1bit</th>
<th>2bit</th>
</tr>
<tr>
<th></th>
<th></th>
<th>cms_free</th>
<th>分代年龄</th>
<th>偏向锁</th>
<th>锁标志位</th>
</tr>
<tr>
<td>无锁</td>
<td>unused</td>
<td>hashCode</td>
<td></td>
<td></td>
<td>0</td>
<td>01</td>
</tr>
<tr>
<td>偏向锁</td>
<td colspan='2'>ThreadID(54bit)Epoch(2bit)</td>
<td></td>
<td></td>
<td>1</td>
<td>01</td>
</tr>
<tr>
<td>轻量级锁</td>
<td colspan='5'><center>指向栈中锁的记录的指针</center></td>
<td>00</td>
</tr>
<tr>
<td>重量级锁</td>
<td colspan='5'><center>指向重量级锁的指针</center></td>
<td>10</td>
</tr>
<tr>
<td>GC标志</td>
<td colspan='5'><center>空</center></td>
<td>11</td>
</tr>
</table>