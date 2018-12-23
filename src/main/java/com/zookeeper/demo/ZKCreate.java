package com.zookeeper.demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZKCreate {
    private static ZooKeeper zk;
    private static ZooKeeperConnection conn;

    // Method to create znode in zookeeper ensemble
    public static void create(String path, byte[] data) throws
            KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
    }

    public static void main(String[] args) {
        String host = "192.168.119.128:2183";

        // znode path
        String path = "/MyFirstZnode"; // Assign path to znode

        // data in byte array
        byte[] data = "My first zookeeper app".getBytes(); // Declare data

        try {
            conn = new ZooKeeperConnection();
            zk = conn.connect(host);
            create(path, data); // Create the data to the specified path
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage()); //Catch error message
        }
    }
}
