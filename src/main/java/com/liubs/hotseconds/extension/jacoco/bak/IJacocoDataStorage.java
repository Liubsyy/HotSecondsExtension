package com.liubs.hotseconds.extension.jacoco.bak;


/**
 * jacoco数据持久化和读取
 */
public interface IJacocoDataStorage {

    boolean storage();

    boolean readData();
}
