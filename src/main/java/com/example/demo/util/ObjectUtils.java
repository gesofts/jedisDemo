package com.example.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

/**
 * Created by WCL on 2018/7/31.
 */
public class ObjectUtils  extends org.apache.commons.lang3.ObjectUtils {
    public ObjectUtils() {
    }

    public static void annotationToObject(Object annotation, Object object) {
        if(annotation != null) {
            Class annotationClass = annotation.getClass();
            Class objectClass = object.getClass();
            Method[] var4 = objectClass.getMethods();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Method m = var4[var6];
                if(StringUtils.startsWith(m.getName(), "set")) {
                    try {
                        String s = StringUtils.uncapitalize(StringUtils.substring(m.getName(), 3));
                        Object obj = annotationClass.getMethod(s, new Class[0]).invoke(annotation, new Object[0]);
                        if(obj != null && !"".equals(obj.toString())) {
                            if(object == null) {
                                object = objectClass.newInstance();
                            }

                            m.invoke(object, new Object[]{obj});
                        }
                    } catch (Exception var10) {
                        ;
                    }
                }
            }
        }

    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            if(object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return null;
    }

    public static String serialize2Str(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            if(object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return new String(baos.toByteArray());
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;

        try {
            if(bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream e = new ObjectInputStream(bais);
                return e.readObject();
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static Object unserialize(String str) {
        if(StringUtils.isEmpty(str)) {
            return null;
        } else {
            ByteArrayInputStream bais = null;

            try {
                byte[] e = str.getBytes();
                if(e != null && e.length > 0) {
                    bais = new ByteArrayInputStream(e);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return ois.readObject();
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return null;
        }
    }

    public static byte[] getBytesKey(Object object) {
        return object instanceof String?StringUtils.getBytes((String)object):serialize(object);
    }

    public static Object toObject(byte[] bytes) {
        return unserialize(bytes);
    }

    public static byte[] toBytes(Object object) {
        return serialize(object);
    }
}
