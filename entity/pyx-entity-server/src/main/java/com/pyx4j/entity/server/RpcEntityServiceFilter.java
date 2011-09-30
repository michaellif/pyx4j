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
 * Created on Feb 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.Service;

public class RpcEntityServiceFilter implements IServiceFilter {

    private static final Logger log = LoggerFactory.getLogger(RpcEntityServiceFilter.class);

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        boolean ok = false;
        try {
            filterRpcTransient(request, new IdentityHashSet<Serializable>(), true);
            ok = true;
        } finally {
            if (!ok) {
                log.error("request not valid {}", request);
            }
        }
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        boolean ok = false;
        try {
            filterRpcTransient(response, new IdentityHashSet<Serializable>(), false);
            ok = true;
        } finally {
            if (!ok) {
                log.error("response not valid {}", response);
            }
        }
        return response;
    }

    protected void filterRpcTransient(Serializable value, Set<Serializable> processed, boolean in) {
        if (value instanceof IEntity) {
            filterMembers((IEntity) value, processed, in);
        } else if (value instanceof IServiceRequest) {
            for (Serializable v : ((IServiceRequest) value).getArgs()) {
                filterRpcTransient(v, processed, in);
            }
        } else if (value instanceof EntitySearchResult<?>) {
            for (Object v : ((EntitySearchResult<?>) value).getData()) {
                if (v instanceof Serializable) {
                    filterRpcTransient((Serializable) v, processed, in);
                } else {
                    log.warn("Sending non Serializable collection value [{}]", v);
                }
            }
        } else if (value instanceof Collection<?>) {
            for (Object v : (Collection<?>) value) {
                if (v instanceof Serializable) {
                    filterRpcTransient((Serializable) v, processed, in);
                } else {
                    log.warn("Sending non Serializable collection value [{}]", v);
                }
            }
        }
    }

    protected void filterMembers(IEntity entity, Set<Serializable> processed, boolean in) {
        if (entity.isNull() || processed.contains(entity) || processed.contains(entity.getValue())) {
            return;
        }
        processed.add(entity);
        processed.add((Serializable) entity.getValue());

        entity = entity.cast();
        EntityMeta em = entity.getEntityMeta();
        if (em.isRpcTransient()) {
            throw new Error("Should not serialize " + entity.getObjectClass());
        }

        nextValue: for (Map.Entry<String, Object> me : entity.getValue().entrySet()) {
            String memberName = me.getKey();
            if (memberName.equals(IEntity.PRIMARY_KEY)) {
                if ((me.getValue() != null) && (!(me.getValue() instanceof Key))) {
                    throw new Error("Data type corruption");
                }
                continue nextValue;
            } else if (memberName.equals(IEntity.CONCRETE_TYPE_DATA_ATTR)) {
                if ((me.getValue() != null) && (!(me.getValue() instanceof IEntity))) {
                    throw new Error("Data type corruption");
                }
                continue nextValue;
            } else if (memberName.startsWith(IEntity.ATTR_PREFIX)) {
                continue;
            }

            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isRpcTransient()) {
                me.setValue(null);
            } else if (memberMeta.isEntity()) {
                filterMembers((IEntity) entity.getMember(memberName), processed, in);
            } else if (ISet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                for (IEntity value : (ISet<?>) entity.getMember(memberName)) {
                    filterMembers(value, processed, in);
                }
            } else if (IList.class.isAssignableFrom(memberMeta.getObjectClass())) {
                for (IEntity value : (IList<?>) entity.getMember(memberName)) {
                    filterMembers(value, processed, in);
                }
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                if ((me.getValue() != null) && (!(memberMeta.getValueClass().isAssignableFrom(me.getValue().getClass())))) {
                    log.error("Got Value " + memberName + " {} instead of {}", me.getValue().getClass(), memberMeta.getValueClass());
                    throw new Error("Data type corruption");
                }
            } else if (IPrimitiveSet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                for (Object value : (IPrimitiveSet<?>) entity.getMember(memberName)) {
                    if ((value != null) && (!(memberMeta.getValueClass().isAssignableFrom(value.getClass())))) {
                        log.error("Got Value " + memberName + " {} instead of {}", value.getClass(), memberMeta.getValueClass());
                        throw new Error("Data type corruption");
                    }
                }
            } else {
                throw new Error("Data type corruption");
            }
        }
    }
}
