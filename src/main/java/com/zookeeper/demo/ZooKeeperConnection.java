package com.zookeeper.demo;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;

/**
 *  zookeeper
 */
public class ZooKeeperConnection {

    private ZooKeeper zoo;
    private final int TIME_OUT_TIME = 5000;
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws Exception {

        zoo = new ZooKeeper(host, TIME_OUT_TIME, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();
        return zoo;
    }

    public void close() throws Exception {
        zoo.close();
    }

    public static void main(String[] args) throws Exception{
        String host = "192.168.119.128:2183";
        ZooKeeperConnection zooKeeperConnection = new ZooKeeperConnection();
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = zooKeeperConnection.connect(host);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("当前Zookeeper对象的信息为:" + zooKeeper);
        zooKeeperConnection.close();
    }
}
