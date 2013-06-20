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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityGraph {

    protected static final Logger log = LoggerFactory.getLogger(EntityGraph.class);

    public static interface ApplyMethod {

        /**
         * @return true if we need to go recursively inside this entity.
         */

        public boolean apply(IEntity entity);

    }

    public static void applyRecursively(IEntity entity, ApplyMethod method) {
        applyRecursively(entity, method, new HashSet<IEntity>(), new IdentityHashSet<Map<String, Serializable>>());
    }

    public static void applyRecursivelyAllObjects(IEntity entity, ApplyMethod method) {
        applyRecursively(entity, method, new IdentityHashSet<IEntity>(), new IdentityHashSet<Map<String, Serializable>>());
    }

    private static void applyRecursively(IEntity entity, ApplyMethod method, Set<IEntity> processed, Set<Map<String, Serializable>> processedValues) {
        if (processed.contains(entity) || processedValues.contains(entity.getValue())) {
            return;
        }
        boolean applyRecursively = method.apply(entity);
        processed.add(entity);
        if (entity.isNull()) {
            return;
        }
        processedValues.add(entity.getValue());
        if (!applyRecursively) {
            return;
        }

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if (member.getAttachLevel() == AttachLevel.Detached) {
                continue;
            }
            if (memberMeta.isEntity()) {
                applyRecursively((IEntity) member, method, processed, processedValues);
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (ISet<?>) member) {
                    applyRecursively(value, method, processed, processedValues);
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                for (IEntity value : (IList<?>) member) {
                    applyRecursively(value, method, processed, processedValues);
                }
            }
        }
    }

    private static final boolean traceFullyEqual = false;

    /**
     * Ignore changes in values of Not owned Objects.
     * e.g. compare only Owned members
     */
    public static boolean fullyEqual(IEntity ent1, IEntity ent2) {
        if (!EqualsHelper.equals(ent1, ent2)) {
            if (traceFullyEqual) {
                log.info("--changes\n{}\n!=\n{}", ent1, ent2);
            }
            return false;
        }
        return fullyEqualValues(ent1, ent2, true, new HashSet<IEntity>(), new IObject<?>[0]);
    }

    /**
     * Include changes in values of Not owned Objects
     */
    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2) {
        return fullyEqualValues(ent1, ent2, false, new HashSet<IEntity>(), new IObject<?>[0]);
    }

    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2, IObject<?>... ignoreValues) {
        return fullyEqualValues(ent1, ent2, false, new HashSet<IEntity>(), ignoreValues);
    }

    @SuppressWarnings("unchecked")
    private static boolean fullyEqualValues(IEntity ent1, IEntity ent2, boolean ownedValuesOnly, Set<IEntity> processed, IObject<?>... ignoreValues) {
        // Cast if required to concert instance
        ent1 = ent1.cast();
        ent2 = ent2.cast();

        if ((ent1 == ent2) || (processed.contains(ent1))) {
            return true;
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
            if ((member1.getAttachLevel() == AttachLevel.Detached) && (member2.getAttachLevel() == AttachLevel.Detached)) {
                continue;
            }

            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity ent1Member = (IEntity) member1;
                IEntity ent2Member = (IEntity) member2;
                if (ent2Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (memberMeta.isEmbedded()) {
                    if (!fullyEqualValues(ent1Member, ent2Member, ownedValuesOnly, processed, new IObject<?>[0])) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                } else if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!ent1Member.equals(ent2Member)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                } else if (!fullyEqualValues(ent1Member, ent2Member, ownedValuesOnly, processed, new IObject<?>[0])) {
                    if (traceFullyEqual) {
                        log.info("--changes in member {}", memberName);
                    }
                    return false;
                }
                break;
            case EntityList:
                if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                } else {
                    if (!fullyEqualValues((IList<IEntity>) member1, (IList<IEntity>) member2, ownedValuesOnly, processed)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                }
                break;
            case EntitySet:
                if (ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                } else {
                    if (!fullyEqualValues((ISet<IEntity>) member1, (ISet<IEntity>) member2, ownedValuesOnly, processed)) {
                        if (traceFullyEqual) {
                            log.info("--changes in member {}", memberName);
                        }
                        return false;
                    }
                }
                break;
            default:
                if (!EqualsHelper.equals(member1, member2)) {
                    if (traceFullyEqual) {
                        log.info("--changes in member {}", memberName);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean fullyEqualValues(ISet<IEntity> set1, ISet<IEntity> set2, boolean ownedValuesOnly, Set<IEntity> processed) {
        if (set1.size() != set2.size()) {
            return false;
        }
        Iterator<IEntity> iter1 = set1.iterator();
        List<IEntity> set2copy = new Vector<IEntity>(set2);
        set1Loop: while (iter1.hasNext()) {
            IEntity ent1 = iter1.next();
            // Find first entity with the same data
            Iterator<IEntity> iter2 = set2copy.iterator();
            while (iter2.hasNext()) {
                if (!fullyEqualValues(ent1, iter2.next(), ownedValuesOnly, processed, new IObject<?>[0])) {
                    // Do not compare the same objects twice
                    iter2.remove();
                    continue set1Loop;
                }
            }
            return true;
        }
        return true;
    }

    private static boolean fullyEqualValues(IList<IEntity> value1, IList<IEntity> value2, boolean ownedValuesOnly, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return false;
        }
        Iterator<IEntity> iter1 = value1.iterator();
        Iterator<IEntity> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            if (!fullyEqualValues(iter1.next(), iter2.next(), ownedValuesOnly, processed, new IObject<?>[0])) {
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
            IObject<?> member1 = ent1.getMember(memberName);
            IObject<?> member2 = ent2.getMember(memberName);
            if ((member1.getAttachLevel() == AttachLevel.Detached) || (member2.getAttachLevel() == AttachLevel.Detached)) {
                continue;
            }

            Path p;
            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity ent1Member = (IEntity) member1;
                IEntity ent2Member = (IEntity) member2;
                if (ent2Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (ent1Member.getMeta().isEmbedded()) {
                    if ((p = getChangedDataPath(ent1Member, ent2Member, processed)) != null) {
                        return p;
                    }
                } else if (!memberMeta.isOwnedRelationships()) {
                    if (!(processed.contains(ent1)) && (!ent1Member.equals(ent2Member))) {
                        return ent1Member.getPath();
                    }
                } else if ((p = getChangedDataPath(ent1Member, ent2Member, processed)) != null) {
                    return p;
                }
                break;
            case EntitySet:
                if ((p = getChangedDataPath((ISet<IEntity>) member1, (ISet<IEntity>) member2, processed)) != null) {
                    return p;
                }
                break;
            case EntityList:
                if ((p = getChangedDataPath((IList<IEntity>) member1, (IList<IEntity>) member2, processed)) != null) {
                    return p;
                }
                break;
            default:
                if (memberName.equals(IEntity.PRIMARY_KEY)) {
                    if ((ent1.getPrimaryKey() != null) && !EqualsHelper.classEquals(ent1.getPrimaryKey(), ent2.getPrimaryKey())) {
                        return ent1.getMember(memberName).getPath();
                    }
                } else if (!EqualsHelper.equals(member1, member2)) {
                    return ent1.getMember(memberName).getPath();
                }
            }
        }
        return null;
    }

    private static Path getChangedDataPath(ISet<IEntity> set1, ISet<IEntity> set2, Set<IEntity> processed) {
        if (set1.size() != set2.size()) {
            return set1.getPath();
        }
        Iterator<IEntity> iter1 = set1.iterator();
        List<IEntity> set2copy = new Vector<IEntity>(set2);
        set1Loop: while (iter1.hasNext()) {
            IEntity ent1 = iter1.next();
            // Find first entity with the same data
            Iterator<IEntity> iter2 = set2copy.iterator();
            while (iter2.hasNext()) {
                if (getChangedDataPath(ent1, iter2.next()) == null) {
                    // Do not compare the same objects twice
                    iter2.remove();
                    continue set1Loop;
                }
            }
            return ent1.getPath();
        }
        return null;
    }

    private static Path getChangedDataPath(IList<IEntity> value1, IList<IEntity> value2, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return value1.getPath();
        }
        Iterator<IEntity> iter1 = value1.iterator();
        Iterator<IEntity> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            Path p;
            if ((p = getChangedDataPath(iter1.next(), iter2.next(), processed)) != null) {
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

    public static boolean membersEquals(IEntity ent1, IEntity ent2, IPrimitive<?>... protoValues) {
        for (IPrimitive<?> member : protoValues) {
            if (!ent1.getMember(member.getFieldName()).equals(ent2.getMember(member.getFieldName()))) {
                return false;
            }
        }
        return true;
    }

    public static void membersCopy(IEntity src, IEntity dst, IPrimitive<?>... protoValues) {
        for (IPrimitive<?> member : protoValues) {
            String memberFieldName = member.getFieldName();
            Serializable v = (Serializable) src.getMember(memberFieldName).getValue();
            dst.setMemberValue(memberFieldName, v);
        }
    }

    public static <F extends IEntity, S extends F, D extends F> void copyFragment(Class<F> fragmentClass, S src, D dst) {
        for (String memberName : EntityFactory.getEntityMeta(fragmentClass).getMemberNames()) {
            dst.setMemberValue(memberName, src.getMemberValue(memberName));
        }
    }

    /**
     * Set value only if the value is different
     * 
     * @param valueMemeber
     * @param value
     * @return true if the value is changed
     */
    public static <S extends Serializable> boolean updateMember(IPrimitive<S> valueMember, S value) {
        if (!EqualsHelper.equals(valueMember.getValue(), value)) {
            valueMember.setValue(value);
            return true;
        } else {
            return false;
        }
    }

    public static <D extends IEntity, S extends D> boolean updateMember(D dst, S src) {
        if (src.getValue() != dst.getValue()) {
            dst.set(src);
            return true;
        } else {
            return false;
        }
    }

    public static <D extends IEntity, S extends D> boolean update(D dst, S src) {
        return update(dst, src, new HashSet<IEntity>());
    }

    public static boolean updateMembers(IEntity dst, IEntity src, IPrimitive<?>... protoValues) {
        boolean updated = false;
        for (IPrimitive<?> member : protoValues) {
            if (!src.getMember(member.getFieldName()).equals(dst.getMember(member.getFieldName()))) {
                dst.setMemberValue(member.getFieldName(), src.getMemberValue(member.getFieldName()));
                updated = true;
            }
        }
        return updated;
    }

    @SuppressWarnings("unchecked")
    public static boolean update(IEntity dst, IEntity src, Set<IEntity> processed) {
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
                    updated |= update(entDstMember, entSrcMember, processed);
                } else {
                    if (update(entDstMember, entSrcMember, processed)) {
                        entDstMember.setPrimaryKey(entSrcMember.getPrimaryKey());
                        updated = true;
                    }
                }
                break;
            case EntitySet:
                updated |= update((ISet<IEntity>) dst.getMember(memberName), (ISet<IEntity>) src.getMember(memberName), processed);
                break;
            case EntityList:
                updated |= update((IList<IEntity>) dst.getMember(memberName), (IList<IEntity>) src.getMember(memberName), processed);
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

    public static boolean update(ICollection<IEntity, ?> dst, ICollection<IEntity, ?> src, Set<IEntity> processed) {
        boolean updated = false;
        for (IEntity srcItem : (ICollection<IEntity, ?>) src) {
            //find
            boolean found = false;
            for (IEntity dstItem : dst) {
                if (srcItem.equals(dstItem) || srcItem.businessEquals(dstItem)) {
                    found = true;
                    updated |= update(dstItem, srcItem, processed);
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
     *            to be duplicated
     * @return a copy of the provided entity without <code>id</code>s
     */
    public static <E extends IEntity> E businessDuplicate(E entity) {
        final E copy = entity.duplicate();
        makeDuplicate(copy);
        return copy;
    }

    public static <E extends IEntity> void makeDuplicate(final E rootEntity) {
        rootEntity.setPrimaryKey(null);
        applyRecursivelyAllObjects(rootEntity, new ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if ((entity == rootEntity || entity.getMeta().isOwnedRelationships())) {
                    if (entity.getPrimaryKey() != null) {
                        entity.setPrimaryKey(null);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * ApplyMethod would be called for every 'owner' Entity in MetaData even if the Owner object is null.
     */
    public static void applyToOwners(IEntity entity, ApplyMethod method) {
        IEntity currentEntity = entity;
        do {
            IEntity castedEntity = currentEntity.cast();
            String ownerMember = castedEntity.getEntityMeta().getOwnerMemberName();
            if (ownerMember == null) {
                break;
            }
            currentEntity = (IEntity) castedEntity.getMember(ownerMember);
        } while (method.apply(currentEntity));
    }

}
