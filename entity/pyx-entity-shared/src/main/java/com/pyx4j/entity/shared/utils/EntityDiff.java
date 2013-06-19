/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-06-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityDiff {

    public static String getChanges(IEntity ent1, IEntity ent2) {
        StringBuilder changes = new StringBuilder();
        getChanges(changes, ent1, ent2, true, new HashSet<IEntity>(), new IObject<?>[0]);
        return changes.toString();
    }

    public static String getChanges(IEntity ent1, IEntity ent2, IObject<?>... ignoreValues) {
        StringBuilder changes = new StringBuilder();
        getChanges(changes, ent1, ent2, true, new HashSet<IEntity>(), ignoreValues);
        return changes.toString();
    }

    @SuppressWarnings("unchecked")
    private static void getChanges(StringBuilder changes, IEntity ent1, IEntity ent2, boolean ownedValuesOnly, Set<IEntity> processed,
            IObject<?>... ignoreValues) {
        // Cast if required to concert instance
        ent1 = ent1.cast();
        ent2 = ent2.cast();

        if ((ent1 == ent2) || (processed.contains(ent1))) {
            return;
        }
        processed.add(ent1);

        Set<Path> ignorePath = new HashSet<Path>();
        if (ignoreValues != null) {
            for (IObject<?> ignore : ignoreValues) {
                ignorePath.add(ignore.getPath());
            }
        }

        EntityMeta em = ent1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member1 = ent1.getMember(memberName);
            if (ignorePath.contains(member1.getPath())) {
                continue;
            }

            IObject<?> member2 = ent2.getMember(memberName);
            if ((member1.getAttachLevel() == AttachLevel.Detached) || (member2.getAttachLevel() == AttachLevel.Detached)) {
                continue;
            }

            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity ent1Member = (IEntity) member1;
                IEntity ent2Member = (IEntity) member2;
                if (ent2Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (memberMeta.isEmbedded()) {
                    getChanges(changes, ent1Member, ent2Member, ownedValuesOnly, processed, new IObject<?>[0]);
                } else if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!ent1Member.equals(ent2Member)) {
                        if (changes.length() > 0) {
                            changes.append("\n");
                        }
                        changes.append(memberMeta.getCaption()).append(": ");
                        changes.append(ent1Member.getStringView());
                        changes.append(" -> ");
                        changes.append(ent2Member.getStringView());
                    }
                } else {
                    getChanges(changes, ent1Member, ent2Member, ownedValuesOnly, processed, new IObject<?>[0]);
                }
                break;
            case EntityList:
                if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
//                        if (traceFullyEqual) {
//                            log.info("--changes in member {}", memberName);
//                        }
//                        return false;
                    }
                } else {
                    getChanges(changes, (IList<IEntity>) member1, (IList<IEntity>) member2, ownedValuesOnly, processed);
                }
                break;
            case EntitySet:
                if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
//                        if (traceFullyEqual) {
//                            log.info("--changes in member {}", memberName);
//                        }
//                        return false;
                    }
                } else {
                    getChanges(changes, (ISet<IEntity>) member1, (ISet<IEntity>) member2, ownedValuesOnly, processed);
                }
                break;
            default:
                if (!EqualsHelper.equals(member1, member2)) {
                    if (changes.length() > 0) {
                        changes.append("\n");
                    }
                    changes.append(memberMeta.getCaption()).append(": ");
                    changes.append(member1.getValue());
                    changes.append(" -> ");
                    changes.append(member2.getValue());

                }
            }
        }
    }

    private static void getChanges(StringBuilder changes, ISet<IEntity> set1, ISet<IEntity> set2, boolean ownedValuesOnly, Set<IEntity> processed) {
        Iterator<IEntity> iter1 = set1.iterator();
        List<IEntity> set2copy = new Vector<IEntity>(set2);
        set1Loop: while (iter1.hasNext()) {
            IEntity ent1 = iter1.next();
            // Find first entity with the same data
            Iterator<IEntity> iter2 = set2copy.iterator();
            while (iter2.hasNext()) {
                getChanges(changes, ent1, iter2.next(), ownedValuesOnly, processed, new IObject<?>[0]);
                // Do not compare the same objects twice
                iter2.remove();
                continue set1Loop;
            }
        }
    }

    private static void getChanges(StringBuilder changes, IList<IEntity> value1, IList<IEntity> value2, boolean ownedValuesOnly, Set<IEntity> processed) {
        Iterator<IEntity> iter1 = value1.iterator();
        Iterator<IEntity> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            getChanges(changes, iter1.next(), iter2.next(), ownedValuesOnly, processed, new IObject<?>[0]);
        }
    }
}
