package org.twistedcode.ssw810.simpleorm;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 10/29/12
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleORM {
    public static Object Rehydrate(ResultSet rs, Class clazz) {
        Object obj = new Object(); // obj is already null no need to initialize to null
        obj = java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new java.lang.Class[]{clazz}, new SimpleORMHandler(rs, obj));
        return obj;
    }
}
