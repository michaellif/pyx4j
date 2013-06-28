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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
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
        Processing p = new Processing();
        p.getChanges(ent1, ent2, new DiffPath());
        return p.changes.toString();
    }

    public static String getChanges(IEntity ent1, IEntity ent2, IObject<?>... ignoreMembers) {
        Processing p = new Processing(ignoreMembers);
        p.getChanges(ent1, ent2, new DiffPath());
        return p.changes.toString();
    }

    private static class DiffPath {

        String name;

        DiffPath() {
            name = "";
        }

        DiffPath(DiffPath path, String name) {
            if (path.name.length() > 0) {
                this.name = path + "." + name;
            } else {
                this.name = name;
            }
        }

        @Override
        public String toString() {
            return name;
        }

    }

    private static class Processing {

        StringBuilder changes = new StringBuilder();

        boolean ownedValuesOnly = true;

        final Set<IEntity> processed = new HashSet<IEntity>();

        final Set<Path> ignorePath;

        Processing() {
            ignorePath = Collections.<Path> emptySet();
        }

        Processing(IObject<?>[] ignoreMembers) {
            ignorePath = toIgnorePath(ignoreMembers);
        }

        private static Set<Path> toIgnorePath(IObject<?>[] ignoreMembers) {
            Set<Path> ignorePath = new HashSet<Path>();
            if (ignoreMembers != null) {
                for (IObject<?> ignore : ignoreMembers) {
                    ignorePath.add(ignore.getPath());
                }
            }
            return ignorePath;
        }

        @SuppressWarnings("unchecked")
        void getChanges(IEntity ent1, IEntity ent2, DiffPath path) {
            // Cast if required to concert instance
            ent1 = ent1.cast();
            ent2 = ent2.cast();

            if ((ent1 == ent2) || (processed.contains(ent1))) {
                return;
            }
            processed.add(ent1);

            EntityMeta em = ent1.getEntityMeta();
            for (String memberName : em.getMemberNames()) {
                MemberMeta memberMeta = em.getMemberMeta(memberName);
                if (memberMeta.isTransient()) {
                    continue;
                }
                IObject<?> member1 = ent1.getMember(memberName);
                if (ignorePath.contains(member1.getPath())) {
                    continue;
                }

                IObject<?> member2 = ent2.getMember(memberName);
                if ((member1.getAttachLevel() == AttachLevel.Detached) || (member2.getAttachLevel() == AttachLevel.Detached)) {
                    continue;
                }

                boolean logTransient = memberMeta.isLogTransient();

                switch (memberMeta.getObjectClassType()) {
                case Entity:
                    IEntity ent1Member = (IEntity) member1;
                    IEntity ent2Member = (IEntity) member2;
                    if (ent2Member.isNull() && ent2Member.isNull()) {
                        continue;
                    } else if (memberMeta.isEmbedded()) {
                        getChanges(ent1Member, ent2Member, path);
                    } else if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                        if (!ent1Member.equals(ent2Member)) {
                            addChanges(path, memberMeta.getCaption(), logTransient, ent1Member.getStringView(), ent2Member.getStringView());
                        }
                    } else {
                        getChanges(ent1Member, ent2Member, new DiffPath(path, memberMeta.getCaption()));
                    }
                    break;
                case EntityList:
                    if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                        if (!EqualsHelper.equals((List<?>) member1, (List<?>) member2)) {
                            getReferenceChanges((IList<IEntity>) member1, (IList<IEntity>) member2, new DiffPath(path, memberMeta.getCaption()));
                        }
                    } else {
                        getChanges((IList<IEntity>) member1, (IList<IEntity>) member2, new DiffPath(path, memberMeta.getCaption()));
                    }
                    break;
                case EntitySet:
                    if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                        if (!EqualsHelper.equals((Collection<?>) member1, (Collection<?>) member2)) {
                            getReferenceChanges((ISet<IEntity>) member1, (ISet<IEntity>) member2, new DiffPath(path, memberMeta.getCaption()));
                        }
                    } else {
                        getChanges((ISet<IEntity>) member1, (ISet<IEntity>) member2, new DiffPath(path, memberMeta.getCaption()));
                    }
                    break;
                default:
                    if (!EqualsHelper.equals(member1, member2)) {
                        addChanges(path, memberMeta.getCaption(), logTransient, member1.getValue(), member2.getValue());
                    }
                }
            }
        }

        private void addChanges(DiffPath path, String name, boolean logTransient, Object v1, Object v2) {
            if (changes.length() > 0) {
                changes.append("\n");
            }
            changes.append(CommonsStringUtils.nvl_concat(path.name, name, "."));
            changes.append(": ");
            if (logTransient) {
                changes.append("** -> **");
            } else {
                changes.append(v1);
                changes.append(" -> ");
                changes.append(v2);
            }
        }

        private void getReferenceChanges(Collection<IEntity> set1, Collection<IEntity> set2, DiffPath path) {
            Iterator<IEntity> iter1 = set1.iterator();
            List<IEntity> set2copy = new Vector<IEntity>(set2);
            while (iter1.hasNext()) {
                IEntity ent1 = iter1.next();
                if (set2copy.contains(ent1)) {
                    set2copy.remove(ent1);
                } else {
                    // removed
                    addChanges(path, null, false, ent1.getStringView(), "");
                }
            }
            // Added
            for (IEntity ent2 : set2copy) {
                addChanges(path, null, false, "", ent2.getStringView());
            }
        }

        private void getChanges(ISet<IEntity> set1, ISet<IEntity> set2, DiffPath path) {
            Iterator<IEntity> iter1 = set1.iterator();
            List<IEntity> set2copy = new Vector<IEntity>(set2);
            set1Loop: while (iter1.hasNext()) {
                IEntity ent1 = iter1.next();
                // Find first entity with the same data
                Iterator<IEntity> iter2 = set2copy.iterator();
                while (iter2.hasNext()) {
                    getChanges(ent1, iter2.next(), path);
                    // Do not compare the same objects twice
                    iter2.remove();
                    continue set1Loop;
                }
            }
        }

        private void getChanges(IList<IEntity> value1, IList<IEntity> value2, DiffPath path) {
            Iterator<IEntity> iter1 = value1.iterator();
            Iterator<IEntity> iter2 = value2.iterator();
            for (; iter1.hasNext() && iter2.hasNext();) {
                getChanges(iter1.next(), iter2.next(), path);
            }
        }

    }
}
