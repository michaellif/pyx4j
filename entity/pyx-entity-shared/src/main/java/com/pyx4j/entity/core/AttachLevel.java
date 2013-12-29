/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 8, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core;

import java.io.Serializable;

public enum AttachLevel implements Serializable {

    /**
     * Data not retrieved.
     * 
     * Use Persistence.service().retrieveMember(entity.memeber()) to load the data.
     * Persistence.ensureRetrieveMember(entity.memeber())
     */
    Detached,

    CollectionSizeOnly,

    /**
     * Only PrimaryKey and type information is present, other data was not retrieved.
     */
    IdOnly,

    ToStringMembers,

    Attached;

    public static AttachLevel getDefault(ObjectClassType objectClassType) {
        switch (objectClassType) {
        case Entity:
            return AttachLevel.IdOnly;
        case EntitySet:
        case EntityList:
            return AttachLevel.IdOnly;
        case Primitive:
        case PrimitiveSet:
            //return AttachLevel.Detached;
            throw new IllegalArgumentException("Detached for Primitive not implemented");
        default:
            throw new IllegalArgumentException();
        }
    }

}