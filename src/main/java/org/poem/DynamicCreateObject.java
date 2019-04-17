package org.poem;

import javassist.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Administrator
 */
public class DynamicCreateObject {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException,
            InstantiationException{

        DynamicCreateObject dco = new DynamicCreateObject();
        Object student1 ;
        // 属性-取值map
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put( "name", "String" );
        fieldMap.put( "age", "String" );
        // 创建一个名称为Student的类
        student1 = dco.generatorObject( "Student", fieldMap );
        Field[] fields = student1.getClass().getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                System.out.println( field.getName() + "=" + dco.getFieldValue( student1, field.getName() ) );
            }
        }
    }

    /**
     * 为对象动态增加属性，并同时为属性赋值
     *
     * @param className 需要创建的java类的名称
     * @param fieldMap  字段-字段值的属性map，需要添加的属性
     * @return
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private Object generatorObject(String className, Map<String, String> fieldMap)
            throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {

        // 获取javassist类池
        ClassPool pool = ClassPool.getDefault();
        // 创建javassist类
        CtClass ctClass = pool.makeClass( className );
        // 为创建的类ctClass添加属性
        Iterator it = fieldMap.entrySet().iterator();
        // 遍历所有的属性
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = (String) entry.getKey();
            String fieldValue = (String) entry.getValue();
            // 增加属性，这里仅仅是增加属性字段
            CtField ctField = new CtField( pool.get( toJavadatatype(fieldValue)), fieldName, ctClass );
            ctField.setModifiers( Modifier.PUBLIC );
            ctClass.addField( ctField );
        }

        // 为创建的javassist类转换为java类
        Class c = ctClass.toClass();
        // 为创建java对象
        Object newObject = c.newInstance();
        return newObject;
    }

    /**
     * 获取对象属性赋值
     *
     * @param dObject
     * @param fieldName 字段别名
     * @return
     */
    private Object getFieldValue(Object dObject, String fieldName) {
        Object result = null;
        try {
            // 获取对象的属性域
            Field fu = dObject.getClass().getDeclaredField( fieldName );
            try {
                // 设置对象属性域的访问属性
                fu.setAccessible( true );
                // 获取对象属性域的属性值
                result = fu.get( dObject );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 给对象属性赋值
     *
     * @param dObject
     * @param fieldName
     * @param val
     * @return
     */
    private Object setFieldValue(Object dObject, String fieldName, Object val) {
        Object result = null;
        try {
            // 获取对象的属性域
            Field fu = dObject.getClass().getDeclaredField( fieldName );
            try {
                // 设置对象属性域的访问属性
                fu.setAccessible( true );
                // 设置对象属性域的属性值
                fu.set( dObject, val );
                // 获取对象属性域的属性值
                result = fu.get( dObject );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * hive 类型转化为对象
     * @param attrType
     * @return
     */
    private static String toJavadatatype(String attrType) {
        switch (attrType) {
            case HiveTypeConstant.TYPE_INT:
                return Integer.class.getName();
            case HiveTypeConstant.TYPE_BOOLEAN:
                return Boolean.class.getName();
            case HiveTypeConstant.TYPE_STRING:
                return String.class.getName();
            case HiveTypeConstant.TYPE_TINYINT:
                return String.class.getName();
            case HiveTypeConstant.TYPE_SMALLINT:
                return Short.class.getName();
            case HiveTypeConstant.TYPE_BIGINT:
                return Long.class.getName();
            case HiveTypeConstant.TYPE_DOUBLE:
                return Double.class.getName();
            case HiveTypeConstant.TIMESTAMP:
                return String.class.getName();
            case HiveTypeConstant.TYPE_FLOAT:
                return Float.class.getName();
            case HiveTypeConstant.Date:
                return Date.class.getName();
            default:
                return Object.class.getName();
        }
    }

}
