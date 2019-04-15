package cn.rf.hz.web.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.primitives.Primitives;

/**
 * @author Administrator
 * @version
 */
public class ProtostuffUtil {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> byte[] serialize(T obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message;
            if (Primitives.isWrapperType(cls)) {
                message = (T) getDefaultValue(cls);
            } else {
                message = cls.newInstance();
            }
            Schema schema = RuntimeSchema.getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static Object getDefaultValue(Class cls) {
        if (Integer.class.equals(cls)) {
            return new Integer(0);
        } else if (Long.class.equals(cls)) {
            return new Long(0L);
        } else if (Boolean.class.equals(cls)) {
            return new Boolean(false);
        } else if (Character.class.equals(cls)) {
            return new Character((char) 0);
        } else if (Byte.class.equals(cls)) {
            return new Byte((byte) 0);
        } else if (Short.class.equals(cls)) {
            return new Short((short) 0);
        } else if (Float.class.equals(cls)) {
            return new Float(0f);
        } else if (Double.class.equals(cls)) {
            return new Double(0d);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Long.class.isPrimitive());
    }
}
