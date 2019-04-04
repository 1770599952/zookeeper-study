package com.zookeeper.demo;

import org.apache.zookeeper.ZooKeeper;

public class ZkDelete {
    private static ZooKeeper zk;
    private static ZooKeeperConnection conn;

    public static void main(String[] args) {
        String host = "192.168.119.128:2183";

        // znode path
        String path = "/MyFirstZnode"; // Assign path to znode

        // data in byte array
        byte[] data = "My first zookeeper app".getBytes(); // Declare data

        try {
            conn = new ZooKeeperConnection();
            zk = conn.connect(host);

            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage()); //Catch error message
        }
    }

    public void deleteByForce(String path) {

    }

    public void deleteByVersion() {

    }
}
