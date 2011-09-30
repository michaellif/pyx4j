/*
 * Pyx4j framework
 * Copyright (C) 2007 pyx4j.com.
 *
 * @author vlads
 * @version $Id$
 */
package org.apache.commons.lang.builder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

/**
 * Created on 11-Dec-07
 */
public class DeepToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    public static final ToStringStyle STYLE = new DeepToStringStyle(true, true);

    public static final ToStringStyle STYLE_NO_IDENTITY = new DeepToStringStyle(true, false);

    public static final ToStringStyle STYLE_NO_HASHCODE = new DeepToStringStyle(false, false);

    private static final String tab = "    ";

    private final boolean useHashCode;

    private final boolean useIdentityHashCode;

    private static ThreadLocal<Integer> identLevel = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(0);
        }
    };

    private DeepToStringStyle(boolean useHashCode, boolean useIdentityHashCode) {
        super();
        this.useHashCode = useHashCode;
        this.useIdentityHashCode = useIdentityHashCode;
        this.setContentStart("{");
        this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + tab);
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd("}");
        this.setUseShortClassName(true);
    }

    static void appendIdent(StringBuffer buffer) {
        int l = identLevel.get().intValue();
        for (int i = 0; i < l; i++) {
            buffer.append(tab);
        }
    }

    static int getIdent() {
        return identLevel.get().intValue();
    }

    static void setIdent(int value) {
        identLevel.set(value);
    }

    private static void changeIdent(int inc) {
        int l = identLevel.get().intValue();
        l += inc;
        identLevel.set(l);
    }

    static void identInc() {
        changeIdent(1);
    }

    static void identDec() {
        changeIdent(-1);
    }

    static boolean isSimpleType(Object value) {
        if (value instanceof String) {
            return true;
        } else if (value instanceof Number) {
            return true;
        } else if (value instanceof Date) {
            return true;
        } else if (value instanceof Character) {
            return true;
        } else if (value instanceof Boolean) {
            return true;
        } else if (value.getClass().isPrimitive()) {
            return true;
        }
        return false;
    }

    public static boolean canUseToString(Object value) {
        if (isSimpleType(value)) {
            return true;
        } else if (value instanceof Map) {
            return false;
        } else if (value instanceof Collection) {
            return false;
        }

        try {
            Method method = value.getClass().getMethod("toString");
            if (method.getDeclaringClass().equals(Object.class)) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable e) {
        }

        return false;
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if (isSimpleType(value)) {
            buffer.append(value.toString());
            return;
        }
        if (value instanceof StringBuffer) {
            buffer.append(value);
        } else if (value instanceof Enum<?>) {
            buffer.append(ReflectionToStringBuilder.toString(value, ToStringStyle.SIMPLE_STYLE));
            return;
        }

        DeepReflectionToStringBuilder b = DeepReflectionToStringBuilder.getBuilder();
        if (b != null) {
            b.appendDetail(value);
        } else {
            super.appendDetail(buffer, fieldName, value);
        }
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
        this.appendDetail(buffer, fieldName, (Object) coll);
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Map map) {
        this.appendDetail(buffer, fieldName, (Object) map);
    }

    @Override
    protected void appendContentStart(StringBuffer buffer) {
        super.appendContentStart(buffer);
        identInc();
    }

    @Override
    protected void appendFieldSeparator(StringBuffer buffer) {
        super.appendFieldSeparator(buffer);
        appendIdent(buffer);
    }

    @Override
    protected void removeLastFieldSeparator(StringBuffer buffer) {
        int len = buffer.length();
        int id = identLevel.get().intValue();
        int sepLen = id * tab.length();
        boolean match = true;
        for (int i = 0; i < sepLen; i++) {
            if (buffer.charAt(len - 1 - i) != ' ') {
                match = false;
                break;
            }
        }
        if (match) {
            buffer.setLength(len - sepLen);
        }

        super.removeLastFieldSeparator(buffer);
    }

    @Override
    public void appendContentEnd(StringBuffer buffer) {
        buffer.append(SystemUtils.LINE_SEPARATOR);
        appendIdent(buffer);
        super.appendContentEnd(buffer);
        identDec();
    }

    @Override
    protected void appendIdentityHashCode(StringBuffer buffer, Object object) {
        if (object != null) {
            if (useHashCode || useIdentityHashCode) {
                buffer.append('@');
            }
            int implHashCode = 0;
            if (useHashCode) {
                implHashCode = object.hashCode();
                buffer.append(Integer.toHexString(implHashCode));
            }
            if (useIdentityHashCode) {
                int identityHashCode = System.identityHashCode(object);
                if ((!useHashCode) || implHashCode != identityHashCode) {
                    buffer.append('(');
                    buffer.append(Integer.toHexString(identityHashCode));
                    buffer.append(')');
                }
            }
        }
    }
}
