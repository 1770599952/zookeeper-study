package com.zookeeper.demo;

import org.apache.logging.log4j.util.Strings;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper分布式锁初体验 - 1
 */
public class DistributedLockImpl implements DistributedLock {

    /**
     * zookeeper集群
     */
    private ZooKeeper zooKeeper;
    /**
     * 根节点
     */
    private String lockBasePath;
    /**
     * 竞争节点
     */
    private String ourLockPath;
    /**
     * 当前线程锁定节点
     */
    private String currentLockNode;
    /**
     * 当前线程观察节点
     */
    private String currentWatchNode;
    /**
     * 等待超时时间
     */
    private int SESSION_TIMEOUT = 100000;
    /**
     * 当前线程等待节点时，用该锁进行阻塞，等该节点等待的上一节点删除后，释放该锁。
     */
    private CountDownLatch countDownLatch;

    public DistributedLockImpl(String host, String lockBasePath, String ourLockPath) {

        if (StringUtils.isEmpty(lockBasePath) || StringUtils.isEmpty(ourLockPath)) {
            throw new LockingException("lockBasePath or ourLockPath is null");
        }

        try {
            ZooKeeperConnection zooKeeperConnection = new ZooKeeperConnection();
            this.zooKeeper = zooKeeperConnection.connect(host);
            this.lockBasePath = lockBasePath;
            this.ourLockPath = ourLockPath;
            this.countDownLatch = new CountDownLatch(1);
            ensureLockBasePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void ensureLockBasePath() throws Exception {
        String NULL_STR = "";
        if (zooKeeper.exists(lockBasePath, true) == null) {
            zooKeeper.create(lockBasePath, NULL_STR.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    @Override
    public void lock() throws Exception {
        System.out.println("Thread ：" + Thread.currentThread() + " 开始获取锁：");
        if (tryLock()) {
            return;
        } else {
            waitForLock(SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
            return;
        }
    }

    private boolean waitForLock(int SESSION_TIMEOUT, TimeUnit milliseconds) throws Exception {
        String currentWatchNodePath = lockBasePath + "/" + currentWatchNode;
        Stat stat = zooKeeper.exists(currentWatchNodePath, true);
        System.out.println(Thread.currentThread().getName() + " 线程等锁" + currentWatchNode);
        this.countDownLatch.await();
        return checkLock();
    }

    private boolean checkLock() throws Exception {
        List<String> childrenNodes = zooKeeper.getChildren(lockBasePath, false);
        Collections.sort(childrenNodes);
        System.out.println("当前最小节点为:" + childrenNodes.get(0));
        if (Strings.isNotBlank(currentLockNode) && Strings.isNotEmpty(childrenNodes.get(0)) && currentLockNode.equals(lockBasePath + "/" + childrenNodes.get(0))) {
            System.out.println(Thread.currentThread().getName() + "  get Lock...");
            return true;
        }

        int index = childrenNodes.indexOf(currentLockNode.substring(currentLockNode.lastIndexOf("/") + 1));
        currentWatchNode = childrenNodes.get(index - 1);

        System.out.println("当前 Thraed " + Thread.currentThread() + " watch node 为：" + currentWatchNode);

        Stat stat = zooKeeper.exists(lockBasePath + "/" + currentWatchNode, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDeleted) {
                    System.out.println(currentWatchNode + " countdown");
                    countDownLatch.countDown();
                }
            }
        });
        return false;
    }

    @Override
    public boolean tryLock() throws Exception {
        currentLockNode = zooKeeper.create(lockBasePath + "/" + ourLockPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        return checkLock();
    }

    @Override
    public void unlock() throws LockingException {
        try {
            System.out.println(Thread.currentThread() + "释放锁！");
            zooKeeper.delete(currentLockNode, -1);
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
