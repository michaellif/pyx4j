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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityGraph {

    protected static final Logger log = LoggerFactory.getLogger(EntityGraph.class);

    public static interface ApplyMethod {

        public void apply(IEntity entity);

    }

    public static void applyRecursively(IEntity entity, ApplyMethod method) {
        applyRecursively(entity, method, new HashSet<IEntity>(), new HashSet<Map<String, Object>>());
    }

    public static void applyRecursivelyAllObjects(IEntity entity, ApplyMethod method) {
        applyRecursively(entity, method, new IdentityHashSet<IEntity>(), new IdentityHashSet<Map<String, Object>>());
    }

    private static void applyRecursively(IEntity entity, ApplyMethod method, Set<IEntity> processed, Set<Map<String, Object>> processedValues) {
        if (processed.contains(entity) || processedValues.contains(entity.getValue())) {
            return;
        }
        method.apply(entity);
        processed.add(entity);
        if (entity.isNull()) {
            return;
        }
        processedValues.add(entity.getValue());

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isEntity()) {
                applyRecursively((IEntity) entity.getMember(memberName), method, processed, processedValues);
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (ISet<?>) entity.getMember(memberName)) {
                    applyRecursively(value, method, processed, processedValues);
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (IList<?>) entity.getMember(memberName)) {
                    applyRecursively(value, method, processed, processedValues);
                }
            }
        }
    }

    private static final boolean traceFullyEqual = false;

    public static boolean fullyEqual(IEntity ent1, IEntity ent2) {
        if (!EqualsHelper.equals(ent1, ent2)) {
            if (traceFullyEqual) {
                log.info("--changes\n{}\n!=\n{}", ent1, ent2);
            }
            return false;
        }
        return fullyEqualValues(ent1, ent2);
    }

    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2) {
        // Cast if required to concert instance
        ent1 = ent1.cast();
        ent2 = ent2.cast();

        EntityMeta em = ent1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isEntity()) {
                IEntity ent1Member = (IEntity) ent1.getMember(memberName);
                IEntity ent2Member = (IEntity) ent2.getMember(memberName);
                if (ent2Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (ent1Member.getMeta().isEmbedded()) {
                    if (!fullyEqualValues(ent1Member, ent2Member)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                } else if (!fullyEqual(ent1Member, ent2Member)) {
                    if (traceFullyEqual) {
                        log.info("--changes in member {}", memberName);
                    }
                    return false;
                }
            } else if (!EqualsHelper.equals(ent1.getMember(memberName), ent2.getMember(memberName))) {
                if (traceFullyEqual) {
                    log.info("--changes in member {}", memberName);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Compare ignoring addition of ID's in Entity ent2
     * 
     * @param clientSide
     * @param received
     *            the entity after save in DB. new Pk may be assigned to values.
     * @return the path to the first different value or null
     */
    public static Path getChangedDataPath(IEntity clientSide, IEntity received) {
        return getChangedDataPath(clientSide, received, new HashSet<IEntity>());
    }

    @SuppressWarnings("unchecked")
    private static Path getChangedDataPath(IEntity ent1, IEntity ent2, Set<IEntity> processed) {
        if ((ent1 == ent2) || (processed.contains(ent1))) {
            return null;
        } else if ((!ent1.getInstanceValueClass().equals(ent2.getInstanceValueClass()))) {
            return ent1.getPath();
        }
        processed.add(ent1);

        // Cast if required to concert instance
        ent1 = ent1.cast();
        ent2 = ent2.cast();

        EntityMeta em = ent1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            Path p;
            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity ent1Member = (IEntity) ent1.getMember(memberName);
                IEntity ent2Member = (IEntity) ent2.getMember(memberName);
                if (ent2Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (ent1Member.getMeta().isEmbedded()) {
                    if ((p = getChangedDataPath(ent1Member, ent2Member, processed)) != null) {
                        return p;
                    }
                } else if (!memberMeta.isOwnedRelationships()) {
                    if (!(processed.contains(ent1)) && (!ent1Member.equals(ent2Member))) {
                        return ent1.getMember(memberName).getPath();
                    }
                } else if ((p = getChangedDataPath(ent1Member, ent2Member, processed)) != null) {
                    return p;
                }
                break;
            case EntitySet:
                if ((p = getChangedDataPath((ISet<IEntity>) ent1.getMember(memberName), (ISet<IEntity>) ent2.getMember(memberName), processed)) != null) {
                    return p;
                }
                break;
            case EntityList:
                if ((p = getChangedDataPath((IList<IEntity>) ent1.getMember(memberName), (IList<IEntity>) ent2.getMember(memberName), processed)) != null) {
                    return p;
                }
                break;
            default:
                if (memberName.equals(IEntity.PRIMARY_KEY)) {
                    if ((ent1.getPrimaryKey() != null) && !EqualsHelper.classEquals(ent1.getPrimaryKey(), ent2.getPrimaryKey())) {
                        return ent1.getMember(memberName).getPath();
                    }
                } else if (!EqualsHelper.equals(ent1.getMember(memberName), ent2.getMember(memberName))) {
                    return ent1.getMember(memberName).getPath();
                }
            }
        }
        return null;
    }

    //TODO, this is not proper implementation in regards to ID
    private static Path getChangedDataPath(ISet<IEntity> set1, ISet<IEntity> set2, Set<IEntity> processed) {
        if (set1.size() != set2.size()) {
            return set1.getPath();
        }
        Iterator<IEntity> iter1 = set1.iterator();
        while (iter1.hasNext()) {
            IEntity ent1 = iter1.next();
            if (!set2.contains(ent1)) {
                return ent1.getPath();
            }
        }
        return null;
    }

    private static Path getChangedDataPath(IList<IEntity> value1, IList<IEntity> value2, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return value1.getPath();
        }
        Iterator<?> iter1 = value1.iterator();
        Iterator<?> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            Path p;
            if ((p = getChangedDataPath((IEntity) iter1.next(), (IEntity) iter2.next(), processed)) != null) {
                return p;
            }
        }
        return null;
    }

    public static <E extends IEntity> boolean hasBusinessDuplicates(ICollection<E, ?> entityList) {
        for (E item : entityList) {
            for (E otherItem : entityList) {
                if ((!item.equals(otherItem)) && item.businessEquals(otherItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean memebersEquals(IEntity ent1, IEntity ent2, IPrimitive<?>... protoValues) {
        for (IPrimitive<?> member : protoValues) {
            if (!ent1.getMember(member.getFieldName()).equals(ent2.getMember(member.getFieldName()))) {
                return false;
            }
        }
        return true;
    }

    public static void memebersCopy(IEntity src, IEntity dst, IPrimitive<?>... protoValues) {
        for (IPrimitive<?> member : protoValues) {
            String memberFieldName = member.getFieldName();
            Object v = src.getMember(memberFieldName).getValue();
            dst.setMemberValue(memberFieldName, v);
        }
    }

    public static <F extends IEntity, S extends F, D extends F> void copyFragment(Class<F> fragmentClass, S src, D dst) {
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            dst.setMemberValue(memberName, src.getMemberValue(memberName));
        }
    }

    public static <D extends IEntity, S extends D> boolean update(S src, D dst) {
        return update(src, dst, new HashSet<IEntity>());
    }

    @SuppressWarnings("unchecked")
    public static boolean update(IEntity src, IEntity dst, Set<IEntity> processed) {
        if ((src == dst) || (processed.contains(src))) {
            return false;
        } else if ((!src.getInstanceValueClass().equals(dst.getInstanceValueClass()))) {
            return false;
        }
        processed.add(src);

        // Cast if required to concert instance
        src = src.cast();
        dst = dst.cast();

        boolean updated = false;

        EntityMeta em = src.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity entSrcMember = (IEntity) src.getMember(memberName);
                IEntity entDstMember = (IEntity) dst.getMember(memberName);
                if (entDstMember.isNull() && entDstMember.isNull()) {
                    continue;
                } else if (memberMeta.isOwnedRelationships()) {
                    updated |= update(entSrcMember, entDstMember, processed);
                } else {
                    if (update(entSrcMember, entDstMember, processed)) {
                        entDstMember.setPrimaryKey(entSrcMember.getPrimaryKey());
                        updated = true;
                    }
                }
                break;
            case EntitySet:
                updated |= update((ISet<IEntity>) src.getMember(memberName), (ISet<IEntity>) dst.getMember(memberName), processed);
                break;
            case EntityList:
                updated |= update((IList<IEntity>) src.getMember(memberName), (IList<IEntity>) dst.getMember(memberName), processed);
            default:
                if (memberName.equals(IEntity.PRIMARY_KEY)) {
                    if ((src.getPrimaryKey() != null) && !EqualsHelper.classEquals(src.getPrimaryKey(), dst.getPrimaryKey())) {
                        dst.setPrimaryKey(src.getPrimaryKey());
                        updated = true;
                    }
                } else if (!EqualsHelper.equals(src.getMemberValue(memberName), dst.getMemberValue(memberName))) {
                    dst.setMemberValue(memberName, src.getMemberValue(memberName));
                    updated = true;
                }
            }
        }
        return updated;
    }

    public static boolean update(ICollection<IEntity, ?> src, ICollection<IEntity, ?> dst, Set<IEntity> processed) {
        boolean updated = false;
        for (IEntity srcItem : (ICollection<IEntity, ?>) src) {
            //find
            boolean found = false;
            for (IEntity dstItem : dst) {
                if (srcItem.equals(dstItem) || srcItem.businessEquals(dstItem)) {
                    found = true;
                    updated |= update(srcItem, dstItem, processed);
                    break;
                }
            }
            if (!found) {
                dst.add(srcItem);
                updated = true;
            }
        }
        return updated;
    }

    /**
     * @param entity
     * @return an copy of the provided entity with <code>id</code>s
     */
    public static <E extends IEntity> E businessDuplicate(E entity) {
        final E copy = entity.duplicate();
        applyRecursively(copy, new ApplyMethod() {
            @Override
            public void apply(IEntity entity) {
                if (entity == copy || entity.getMeta().isOwnedRelationships()) {
                    entity.id().set(null);
                }
            }
        });

        return copy;
    }
}
