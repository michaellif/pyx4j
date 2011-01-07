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
package com.pyx4j.entity.rebind;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.impl.MemberMetaData;

public class MemberMetaDataGeneration extends MemberMetaData {

    public String valueClassSourceName;

    public String objectClassSourceName;

    public boolean isDataEquals(MemberMetaData other) {
        return ((persistenceTransient == other.persistenceTransient)

        && (rpcTransient == other.rpcTransient)

        && (detached == other.detached)

        && (ownedRelationships == other.ownedRelationships)

        && (owner == other.owner)

        && (embedded == other.embedded)

        && (stringLength == other.stringLength)

        && (objectClassSourceName.equals(other.objectClass.getName()))

        && (useMessageFormat == other.useMessageFormat)

        && (CommonsStringUtils.equals(format, other.format))

        && (CommonsStringUtils.equals(nullString, other.nullString))

        && (valueClassIsNumber == other.valueClassIsNumber)

        && (isSameClass(valueClassSourceName, other.valueClass)));

    }

    private static boolean isSameClass(String sourceName, Class<?> valueClass) {
        if (sourceName.equals(valueClass.getName())) {
            return true;
        } else if (valueClass.equals(byte[].class)) {
            return sourceName.equals("byte[]");
        } else {
            return false;
        }
    }
}
