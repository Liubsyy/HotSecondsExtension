package com.liubs.hotseconds.extension.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.session.Configuration;

/**
 * MyBatis Plus 刷新mapper接口，可支持新增mapper接口
 * 根据MyBatisPlus 源码 com.baomidou.mybatisplus.core.MybatisConfiguration 改编
 * @author Liubsyy
 * @date 2023/7/10 13:09
 */
public class MyBatisPlusMapperUpdate {

    public static void refreshMapper(Configuration configuration,Class<?> mapperClass) {
        if(!(configuration instanceof MybatisConfiguration)) {
            return;
        }
        MybatisConfiguration plugConfiguration = (MybatisConfiguration)configuration;

        plugConfiguration.addNewMapper(mapperClass);
    }


}
