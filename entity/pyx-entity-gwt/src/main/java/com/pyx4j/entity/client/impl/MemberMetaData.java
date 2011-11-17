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
 * Created on 2010-11-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.impl;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;

public class MemberMetaData {

    public boolean persistenceTransient;

    public boolean rpcTransient;

    public boolean detached;

    public boolean ownedRelationships;

    public boolean owner;

    public boolean embedded;

    public boolean valueClassIsNumber;

    public Class<?> valueClass;

    @SuppressWarnings("rawtypes")
    public Class<? extends IObject> objectClass;

    public ObjectClassType objectClassType;

    /**
     * See com.pyx4j.entity.annotations.StringLength
     */
    public int stringLength;

    public String format;

    public boolean useMessageFormat;

    public String nullString;

    public MemberMetaData() {

    }

    @SuppressWarnings("unchecked")
    public MemberMetaData(Class<?> valueClass, boolean isNumber) {
        this.stringLength = -1;
        this.valueClass = valueClass;
        this.objectClassType = ObjectClassType.Primitive;
        this.objectClass = (Class<? extends IObject<?>>) ((Class<?>) com.pyx4j.entity.shared.IPrimitive.class);
        this.nullString = "";
        this.valueClassIsNumber = isNumber;
    }

    // Most commonly used definitions are shared here for code size and memory optimisation.

    public static final MemberMetaData defaultStringMember = new MemberMetaData(java.lang.String.class, false);

    public static final MemberMetaData defaultBooleanMember = new MemberMetaData(java.lang.Boolean.class, false);

    public static final MemberMetaData defaultDoubleMember = new MemberMetaData(java.lang.Double.class, true);

    public static final MemberMetaData defaultIntegerMember = new MemberMetaData(java.lang.Integer.class, true);

    public static final MemberMetaData defaultDateMember = new MemberMetaData(java.util.Date.class, false);

    public static final MemberMetaData defaultSqlDateMember = new MemberMetaData(java.sql.Date.class, false);

    public static final MemberMetaData defaultLogicalDateMember = new MemberMetaData(LogicalDate.class, false);

}
