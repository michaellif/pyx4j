/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 25, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

class PrimaryKeyCriteriaHelper {

    private final EntityQueryCriteria<?> criteria;

    private final Object criteriaPrimaryKeys;

    PrimaryKeyCriteriaHelper(EntityQueryCriteria<?> criteria) {
        this.criteria = criteria;
        PropertyCriterion pkCriteria = criteria.getCriterion(criteria.proto().id());
        if ((pkCriteria != null) && (EnumSet.of(Restriction.EQUAL, Restriction.IN).contains(pkCriteria.getRestriction()))) {
            Serializable value = pkCriteria.getValue();
            if (value instanceof Collection) {
                criteriaPrimaryKeys = keysConvertor((Collection<?>) value);
            } else {
                criteriaPrimaryKeys = keyConvertor(value);
            }
        } else {
            criteriaPrimaryKeys = null;
        }
    }

    private static Collection<Key> keysConvertor(Collection<?> values) {
        List<Key> keys = new ArrayList<Key>();
        for (Object value : values) {
            keys.add(keyConvertor(value));
        }
        return keys;
    }

    private static Key keyConvertor(Object value) {
        if (value instanceof Key) {
            return (Key) value;
        } else if (value instanceof IEntity) {
            return ((IEntity) value).getPrimaryKey();
        } else if (value instanceof Long) {
            return new Key((Long) value);
        } else if (value instanceof String) {
            return new Key((String) value);
        } else if (value == null) {
            return null;
        } else {
            throw new IllegalArgumentException(value.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    Key getRetrievedKey(long dbPrimaryKey) {
        Key retrievedKey = new Key(dbPrimaryKey);
        if (criteriaPrimaryKeys != null) {
            if (criteriaPrimaryKeys instanceof Key) {
                // Ignore version in comparison
                if (((Key) criteriaPrimaryKeys).asLong() != retrievedKey.asLong()) {
                    throw new RuntimeException();
                }
                // Support for versioned entity, part of criteria is inside the key
                retrievedKey = (Key) criteriaPrimaryKeys;
            } else {
                Key matchingCriteriaPrimaryKey = null;
                for (Key key : ((Collection<Key>) criteriaPrimaryKeys)) {
                    if (key.asLong() == retrievedKey.asLong()) {
                        matchingCriteriaPrimaryKey = key;
                        break;
                    }
                }
                if (matchingCriteriaPrimaryKey == null) {
                    throw new RuntimeException();
                }
                // Support for versioned entity, part of criteria is inside the key
                retrievedKey = matchingCriteriaPrimaryKey;
            }
        }

        if (criteria.getVersionedCriteria() == VersionedCriteria.onlyDraft) {
            retrievedKey = retrievedKey.asDraftKey();
        }
        return retrievedKey;
    }
}
