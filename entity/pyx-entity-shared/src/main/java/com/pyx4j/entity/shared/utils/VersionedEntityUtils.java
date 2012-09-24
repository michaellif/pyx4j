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
 * Created on Mar 17, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class VersionedEntityUtils {

    public static <T extends IVersionData<?>> void setAsDraft(T entity) {
        entity.versionNumber().setValue(null);
        entity.fromDate().setValue(null);
        entity.toDate().setValue(null);
    }

    public static <T extends IVersionedEntity<?>> boolean isCurrent(T entity) {
        assert !entity.version().isNull();
        return (entity.getPrimaryKey() != null) && !entity.getPrimaryKey().isDraft() && entity.version().toDate().isNull()
                && !entity.version().fromDate().isNull();
    }

    public static boolean equalsIgnoreVersion(IVersionedEntity<?> entity1, IVersionedEntity<?> entity2) {
        if (entity2 == entity1) {
            return true;
        }
        Map<String, Object> thisValue = ((SharedEntityHandler) entity1).getValue();
        if (thisValue == null) {
            return false;
        }
        Map<String, Object> otherValue = ((SharedEntityHandler) entity2).getValue();
        if (otherValue == null) {
            return false;
        }
        if (otherValue == thisValue) {
            return true;
        }
        Key pk = (Key) thisValue.get(IEntity.PRIMARY_KEY);
        if (pk == null) {
            return false;
        }
        return pk.equalsIgnoreVersion((Key) otherValue.get(IEntity.PRIMARY_KEY))
                && (entity1.getInstanceValueClass().equals(((IEntity) entity2).getInstanceValueClass()));
    }
}
