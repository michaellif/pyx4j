/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-11-15
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.impl;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public abstract class EntityImplNativeHelper {

    public static IObject<?> createMemberIPrimitive_java_lang_String(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, String.class);
    }

    public static IObject<?> createMemberIPrimitive_java_util_Date(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, Date.class);
    }

    public static IObject<?> createMemberIPrimitive_java_sql_Date(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.sql.Date.class);
    }

    public static IObject<?> createMemberIPrimitive_com_pyx4j_commons_LogicalDate(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, LogicalDate.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Boolean(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Boolean.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Byte(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Byte.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Character(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Character.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Integer(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Integer.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Short(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Short.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Long(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Long.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Float(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Float.class);
    }

    public static IObject<?> createMemberIPrimitive_java_lang_Double(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, java.lang.Double.class);
    }

    public static IObject<?> createMemberIPrimitive_byteArray(SharedEntityHandler handler, String memberName) {
        return handler.lazyCreateMemberIPrimitive(memberName, PrimitiveHandler.BYTE_ARRAY_CLASS);
    }
}
