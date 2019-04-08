package com.zookeeper.quickstart;


public interface DistributedLock {

    void lock() throws Exception;

    boolean tryLock() throws Exception;

    void unlock() throws LockingException;

    class LockingException extends RuntimeException {

        public LockingException(String msg) {
            super(msg);
        }

        public LockingException(String msg, Exception e) {
            super(msg, e);
        }
    }
}
