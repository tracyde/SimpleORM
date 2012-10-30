package org.twistedcode.ssw810.simpleorm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 10/29/12
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleORMHandler implements InvocationHandler {
    private ResultSet resultSet;
    private ArrayList<String> columnNames = null;
    private Object realObj;
    private Map<String, Object> changeLog = new HashMap<String, Object>();

    public SimpleORMHandler(ResultSet rs, Object real) {
        resultSet = rs;
        try {
            columnNames = getColumnNames(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        realObj = real;
    }

    public static ArrayList getColumnNames(ResultSet rs) throws SQLException {
        ArrayList<String> rsColumns = new ArrayList<String>();
        if (rs == null) {
            return rsColumns;
        }
        // get result set meta data
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();
        // get the column names; column indexes start from 1
        for (int i = 1; i < numberOfColumns + 1; i++) {
            String columnName = rsMetaData.getColumnName(i);
            rsColumns.add(columnName);
        }
        return rsColumns;
    }

    public static void printGettersSetters(Class aClass){
        Method[] methods = aClass.getMethods();

        for(Method method : methods){
            if(isGetter(method)) System.out.println("getter: " + method);
            if(isSetter(method)) System.out.println("setter: " + method);
        }
    }

    public static boolean isGetter(Method method){
        if(!method.getName().startsWith("get"))      return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter(Method method){
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length != 1) return false;
        return true;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String method_name = method.getName();
        Class<?>[] classes = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();

        if (isGetter(method)) {
            //System.out.println("getter: " + method);
            for (String columnName : columnNames) {
                if (method_name.substring(3).toLowerCase().equals(columnName.toLowerCase())) {
                    return resultSet.getObject(columnName);
                }
            }
        }

        if (isSetter(method)) {
            System.out.println("setter: " + method);
            for (String columnName : columnNames) {
                if (method_name.substring(3).toLowerCase().equals(columnName.toLowerCase())) {
                    changeLog.put(columnName, args);
                }
            }
        }

        if (method_name.toLowerCase().equals("printchanges")) {
            Iterator it = changeLog.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " = " + pairs.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        return null;
    }
}
