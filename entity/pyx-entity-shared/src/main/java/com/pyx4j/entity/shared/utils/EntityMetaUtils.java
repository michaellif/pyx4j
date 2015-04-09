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
 * Created on Oct 9, 2014
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityMetaUtils.MemberAcceptanceFilter.Acceptance;

public class EntityMetaUtils {

    public static List<Path> getDirectMembers(Class<? extends IEntity> entityClass) {
        List<Path> members = new ArrayList<>();
        IEntity proto = EntityFactory.getEntityPrototype(entityClass);
        for (String memberName : proto.getEntityMeta().getMemberNames()) {
            members.add(proto.getMember(memberName).getPath());
        }
        return members;
    }

    public static interface MemberAcceptanceFilter {

        enum Acceptance {

            Reject,

            Accept,

            AcceptRecursively,
        }

        Acceptance acceptMember(IEntity proto, IObject<?> member, Path memberPath, MemberMeta memberMeta);

    }

    public static List<Path> getMembers(Class<? extends IEntity> entityClass, MemberAcceptanceFilter filter) {
        List<Path> members = new ArrayList<>();
        getMembersRecursively(EntityFactory.getEntityPrototype(entityClass), filter, new IdentityHashSet<IEntity>(), members);
        return members;
    }

    private static void getMembersRecursively(IEntity proto, MemberAcceptanceFilter filter, Set<IEntity> processed, List<Path> result) {
        if (processed.contains(proto)) {
            return;
        }
        processed.add(proto);
        EntityMeta em = proto.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = proto.getMember(memberName);
            Path memberPath = proto.getMember(memberName).getPath();

            Acceptance acceptance = filter.acceptMember(proto, member, memberPath, memberMeta);

            switch (acceptance) {
            case Accept:
                result.add(memberPath);
                break;
            case AcceptRecursively:
                if (acceptance == Acceptance.AcceptRecursively && memberMeta.isEntity()) {
                    getMembersRecursively((IEntity) member, filter, processed, result);
                }
                break;
            default:
                break;
            }
        }
    }
}
