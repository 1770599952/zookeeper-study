package com.zookeeper.demo;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistributedLockImplTest {

    DistributedLock distributedLock = new DistributedLockImpl("192.168.119.128:2183", "/lockbasepath", "lock_");

    @Test
    public void lock() throws Exception {
        distributedLock.lock();
    }

    @Test
    public void tryLock() {
    }

    @Test
    public void unlock() {

    }

    @Test
    public void testMain() {
        Runnable runnable = new Runnable() {
            public void run() {
                DistributedLock lock = null;
                lock = new DistributedLockImpl("192.168.119.128:2183", "/lockbasepath", "lock");
                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + "正在运行");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        };

        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }
}