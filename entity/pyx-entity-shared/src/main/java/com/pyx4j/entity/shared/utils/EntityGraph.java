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
 * Created on 2010-04-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityGraph {

    public static interface ApplyMethod {

        public void apply(IEntity entity);

    }

    public static void applyRecursively(IEntity entity, ApplyMethod method) {
        applyRecursively(entity, method, new HashSet<IEntity>());
    }

    public static void applyRecursively(IEntity entity, ApplyMethod method, Set<IEntity> processed) {
        if (processed.contains(entity)) {
            return;
        }
        method.apply(entity);
        processed.add(entity);
        if (entity.isNull()) {
            return;
        }
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isEntity()) {
                applyRecursively((IEntity) entity.getMember(memberName), method, processed);
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (ISet<?>) entity.getMember(memberName)) {
                    applyRecursively(value, method, processed);
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (IList<?>) entity.getMember(memberName)) {
                    applyRecursively(value, method, processed);
                }
            }
        }
    }

    public static boolean fullyEqual(IEntity ent1, IEntity ent2) {
        if (!EqualsHelper.equals(ent1, ent2)) {
            //            System.out.println("--changes\n" + ent1 + "\n!=\n" + ent2);
            return false;
        }
        return fullyEqualValues(ent1, ent2);
    }

    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2) {
        Map<String, Object> otherValue = ent2.getValue();
        for (Map.Entry<String, Object> me : ent1.getValue().entrySet()) {
            if (me.getValue() instanceof Map) {
                IEntity ent1Memeber = (IEntity) ent1.getMember(me.getKey());
                if (ent1Memeber.getMeta().isEmbedded()) {
                    if (!fullyEqualValues(ent1Memeber, (IEntity) ent2.getMember(me.getKey()))) {
                        return false;
                    }
                } else if (!fullyEqual(ent1Memeber, (IEntity) ent2.getMember(me.getKey()))) {
                    return false;
                }
            } else if (!EqualsHelper.equals(me.getValue(), otherValue.get(me.getKey()))) {
                //                System.out.println("--changes " + ent1.getEntityMeta().getCaption() + "." + me.getKey() + " " + me.getValue() + "!="
                //                        + otherValue.get(me.getKey()));
                return false;
            }
        }
        return true;
    }
}
