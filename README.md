# `OkHttp`手写实现

[TOC]

本文手写实现了`OkHttp`网络访问请求框架，只是学习框架思想的`demo`，仅供学习使用。

## 一、实现效果

实现效果如下图所示：

![image](https://github.com/tianyalu/NeOkHttpManualImpl/raw/master/show/show.gif)

## 二、实现步骤

`OkHttp`的实现中主要用到了等待队列和执行队列，以及责任链模式实现的各种拦截器，执行时序图可以概括如下：

![image](https://github.com/tianyalu/NeOkHttpManualImpl/raw/master/show/okhttp_execute_sequence.png)

## 三、`OkHttp`连接池

### 3.1 连接池使用缘由

`Socket`的连接和断开需要经历三次握手和四次挥手，消耗较大，如果访问量大的话消耗会急剧增加，所以考虑使用连接池对访问相同域名服务器的`Socket`进行复用。

![image](https://github.com/tianyalu/NeOkHttpManualImpl/raw/master/show/okhttp_connection_pool.png)

### 3.2 连接对象回收逻辑

复用最关键的点是对连接池中的连接对象回收的问题，本文每次在连接池中加入连接对象时执行清理机制，总体思路如下：设置一个最大闲置时间，如`keepAlive` = 1分钟，循环检查当前队列中所有连接的最大闲置时间，超出设置允许的最大时间`keepAlive`的直接回收；小于该时间的，等待`delta`时间后（即达到`keepAlive`时间后），再次执行清理操作，直到队列中没有连接为止。

### 3.2.1 执行清理操作使用的线程池

本文使用无界缓存复用线程池。

```java
    /**
     * 线程池，为清理机制工作
     * 参数1：0            核心线程数 0
     * 参数2：MAX_VALUE    线程池中最大值
     * 参数3：60           单位值
     * 参数4：秒钟          时 分 秒
     * 参数5：队列          SynchronousQueue
     *
     * 执行任务大于（核心线程数） 启用（60s闲置时间）
     * 60秒闲置时间，没有过，复用之前的线程， 60秒过的，新实例化
     */
    private Executor threadExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "线程池标记...");
                    thread.setDaemon(true); //设置为守护线程
                    return thread;
                }
            });
```

#### 3.2.2 执行清理操作的时机

一旦有新连接放入连接池的时候，就需要去检查连接池中需要释放清理的对象。

```java
    //添加
    public synchronized void putConnection(HttpConnection httpConnection) {
        //一旦put的时候，就需要去检查连接池中需要释放清理的对象
        if(!cleanRunnableFlag) {
            cleanRunnableFlag = true;
            //启动检查清理机制
            threadExecutor.execute(runnable);
        }
        httpConnectionDeque.add(httpConnection);
        int size = httpConnectionDeque.size();
        Log.d(TAG, "putConnection size: " + size);
    }
```

#### 3.2.3 执行清理操作的任务

```java
    //任务，为清理机制工作
    //专门去检查连接池中的连接对象，清理连接池中的连接对象
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                //下次的检查时间(动态变化)
                long nextCheckCleanTime = clean(System.currentTimeMillis());
                if(nextCheckCleanTime == -1) {
                    //全部结束
                    cleanRunnableFlag = false;
                    return;
                }
                //nextCheckCleanTime = 30 下一次的检查时间（动态变化）
                if(nextCheckCleanTime > 0) {
                    //等待一段时间后，再去检查是否需要清理的对象
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(nextCheckCleanTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    };
```

#### 3.2.4 执行清理操作以及计算距离下次清理的时间

清理闲置连接的思想：循环检查当前队列中所有连接的最大闲置时间，超出设置允许的最大时间`keepAlive`的直接回收；小于该时间的，等待`delta`时间后（即达到`keepAlive`时间后），再次执行清理操作，直到队列中没有连接为止。

```java
private long clean(long currentTimeMillis) {
  //定义最终最大的闲置时间 result
  long idleRecordSave = -1;
  synchronized (this) {
    Iterator<HttpConnection> iterator = httpConnectionDeque.iterator();
    //遍历 池子 容器
    while (iterator.hasNext()) {
      HttpConnection httpConnection = iterator.next();
      //计算出来的闲置时间
      long idleTime = currentTimeMillis - httpConnection.getLastUseTime();
      if(idleTime > keepAlive) {
        //清理 移除对象
        iterator.remove();
        //关闭socket
        httpConnection.recycleSocket();
        continue; //继续检查后续是否有连接对象需要清理
      }

      //得到最长闲置时间
      if(idleRecordSave < idleTime) { //证明计算出来的闲置时间idleTime是合格的（走到这里的idleTime都是<=keepAlive的）
        idleRecordSave = idleTime;
      }
    } //while end
    //循环完毕之后，代表最终闲置时间idleRecordSave终于计算出来了（动态变化）
    if(idleRecordSave >= 0) {
      //keepAlive = 60S
      return (keepAlive - idleRecordSave);
    }
  }
  //返回-1，表示没有计算好，连接池中没有连接对象，则不作任何操作，马上结束所有任务
  return idleRecordSave;
}
```

