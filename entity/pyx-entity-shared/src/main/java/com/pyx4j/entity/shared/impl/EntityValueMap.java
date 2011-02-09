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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.IEntity;

@SuppressWarnings("serial")
public class EntityValueMap extends HashMap<String, Object> {

    private final transient int identityHashCode;

    public EntityValueMap() {
        super();
        //Just create Unique hashCode
        this.identityHashCode = new Object().hashCode();
    }

    public EntityValueMap(int identityHashCode) {
        super();
        this.identityHashCode = identityHashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EntityValueMap)) {
            return false;
        }
        Object pk = this.get(IEntity.PRIMARY_KEY);
        if (pk == null) {
            return false;
        }
        return EqualsHelper.equals(pk, ((Map<?, ?>) other).get(IEntity.PRIMARY_KEY));
    }

    public boolean isNull() {
        return isNull(new HashSet<Map<String, Object>>());
    }

    private boolean isNull(Set<Map<String, Object>> processed) {
        processed.add(this);
        if (this.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Object> me : this.entrySet()) {
            if (me.getValue() instanceof EntityValueMap) {
                if (processed.contains(me.getValue())) {
                    // Owner/Owned binding is not empty!
                    return false;
                }
                if (!((EntityValueMap) me.getValue()).isNull(processed)) {
                    return false;
                }
            } else if (me.getValue() != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object pk = this.get(IEntity.PRIMARY_KEY);
        if (pk == null) {
            return identityHashCode;
        } else {
            return pk.hashCode();
        }
    }

    @SuppressWarnings("unchecked")
    public static void dumpMap(StringBuilder b, Map<String, Object> map, Set<Map<String, Object>> processed) {
        if (processed.contains(map)) {
            b.append("...");
            return;
        }
        boolean first = true;
        processed.add(map);
        for (Map.Entry<String, Object> me : map.entrySet()) {
            if (!first) {
                b.append(' ');
            } else {
                first = false;
            }
            b.append(me.getKey()).append("=");
            if (me.getValue() instanceof Map<?, ?>) {
                b.append('{');
                dumpMap(b, (Map<String, Object>) me.getValue(), processed);
                b.append('}');
            } else if (me.getValue() instanceof Collection<?>) {
                b.append('[');
                boolean collectionFirst = true;
                for (Object o : (Collection<?>) me.getValue()) {
                    if (collectionFirst) {
                        collectionFirst = false;
                    } else {
                        b.append(", ");
                    }
                    if (o instanceof Map<?, ?>) {
                        dumpMap(b, (Map<String, Object>) o, processed);
                    } else {
                        b.append(o);
                    }
                }
                b.append(']');
            } else {
                b.append(me.getValue());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        EntityValueMap.dumpMap(b, this, new HashSet<Map<String, Object>>());
        return b.toString();
    }

}
