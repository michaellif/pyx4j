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

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.rpc.shared.Service;

public class RpcEntityServiceFilter implements IServiceFilter {

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        filterRpcTransient(request);
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        filterRpcTransient(response);
        return response;
    }

    protected void filterRpcTransient(Serializable value) {
        if (value instanceof IEntity) {
            filterTransientMembers((IEntity) value);
        } else if (value instanceof Collection<?>) {
            for (Object v : (Collection<?>) value) {
                if (v instanceof Serializable) {
                    filterRpcTransient((Serializable) v);
                }
            }
        }
    }

    protected void filterTransientMembers(IEntity entity) {
        if (entity.isNull()) {
            return;
        }
        EntityMeta em = entity.getEntityMeta();
        if (em.isRpcTransient()) {
            throw new Error("Should not serialize " + entity.getObjectClass());
        }
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (em.getMemberMeta(memberName).isRpcTransient()) {
                entity.removeMemberValue(memberName);
            } else if (memberMeta.isEntity()) {
                filterTransientMembers((IEntity) entity.getMember(memberName));
            } else if (ISet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                for (IEntity value : (ISet<?>) entity.getMember(memberName)) {
                    filterTransientMembers(value);
                }
            } else if (IList.class.isAssignableFrom(memberMeta.getObjectClass())) {
                for (IEntity value : (IList<?>) entity.getMember(memberName)) {
                    filterTransientMembers(value);
                }
            }
        }
    }
}
