package com.liubs.hotseconds.extension.patch;

import java.lang.reflect.Field;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;

/**
 * @author Liubsyy
 * @date 2024/7/9
 **/
public class DynamicDataSourcePatch {

    /**
     * 拷贝DynamicDataSourceCreatorAutoConfiguration中的 properties字段到HotSwapAgent代理对象中
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {

        try {
            if (!source.getClass().getName().contains(DynamicDataSourceCreatorAutoConfiguration.class.getName())) {
                return;
            }

            //不存在properties字段，直接退出
            Field propertiesField = null;
            try {
                propertiesField = DynamicDataSourceCreatorAutoConfiguration.class.getDeclaredField("properties");
            } catch (NoSuchFieldException e) {
                return;
            }
            propertiesField.setAccessible(true);

            for(Class<?> kclass = source.getClass(); kclass != null; kclass = kclass.getSuperclass()) {
                if (kclass == DynamicDataSourceCreatorAutoConfiguration.class) {

                    propertiesField.set(target, propertiesField.get(source));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
