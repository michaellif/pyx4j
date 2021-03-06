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
 */
package com.pyx4j.entity.shared.utils;

import java.io.Serializable;
import java.util.HashMap;
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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;

public class EntityGraph {

    protected static final Logger log = LoggerFactory.getLogger(EntityGraph.class);

    public static interface ApplyMethod {

        /**
         * @return true if we need to go recursively inside this entity.
         */

        public boolean apply(IEntity entity);

    }

    public static interface ApplyMemberMethod {

        /**
         * @return true if we need to go recursively inside this entity or collection.
         */
        public boolean apply(IEntity memberEntity);

        public boolean apply(ICollection<IEntity, ?> memberCollection);

    }

    /**
     * Apply to Entity members
     */
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
        entity = entity.cast();
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if (member.getAttachLevel() == AttachLevel.Detached || member.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
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

    /**
     * Apply to Non primitive members Collection and Entity
     */
    public static void applyRecursively(IEntity entity, ApplyMemberMethod method) {
        applyRecursively(entity, method, new HashSet<IEntity>(), new IdentityHashSet<Map<String, Serializable>>());
    }

    private static void applyRecursively(IEntity entity, ApplyMemberMethod method, Set<IEntity> processed, Set<Map<String, Serializable>> processedValues) {
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
        entity = entity.cast();

        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if (memberMeta.isEntity()) {
                applyRecursively((IEntity) member, method, processed, processedValues);
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> memberCollection = (ISet<IEntity>) member;
                if (method.apply(memberCollection)) {
                    for (IEntity value : memberCollection) {
                        applyRecursively(value, method, processed, processedValues);
                    }
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> memberCollection = (IList<IEntity>) member;
                if (method.apply(memberCollection)) {
                    for (IEntity value : (IList<?>) member) {
                        applyRecursively(value, method, processed, processedValues);
                    }
                }
            }
        }
    }

    public static class EntityGraphEqualOptions {

        public boolean trace = false;

        public boolean ownedValuesOnly = false;

        public boolean ignoreTransient = true;

        public boolean ignoreRpcTransient = false;

        // Detached on one side members will be ignored.
        public boolean ignoreDetachedMembers = false;

        public EntityGraphEqualOptions() {

        }

        public EntityGraphEqualOptions(boolean ownedValuesOnly) {
            this.ownedValuesOnly = ownedValuesOnly;
        }
    }

    /**
     * Ignore changes in values of Not owned Objects.
     * e.g. compare only Owned members
     */
    public static boolean fullyEqual(IEntity ent1, IEntity ent2) {
        if (!EqualsHelper.equals(ent1, ent2)) {
            return false;
        }
        return fullyEqualValues(ent1, ent2, new EntityGraphEqualOptions(true), new HashSet<IEntity>(), new HashSet<Path>());
    }

    /**
     * Include changes in values of Not owned Objects
     */
    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2) {
        return fullyEqualValues(ent1, ent2, new EntityGraphEqualOptions(false), new HashSet<IEntity>(), new HashSet<Path>());
    }

    /**
     *
     * @param ent1
     *            entity1 to compare
     * @param ent2
     *            entity2 to compare
     * @param ignoreValues
     *            ignore values of selected members
     *
     *            Example: EntityGraph.fullyEqualValues(origSchedule, updateSchedule, origSchedule.timestamp());
     * @return
     */
    public static boolean fullyEqualValues(IEntity ent1, IEntity ent2, IObject<?>... ignoreValues) {
        return fullyEqualValues(ent1, ent2, new EntityGraphEqualOptions(false), new HashSet<IEntity>(), toIgnorePath(ignoreValues));
    }

    public static boolean fullyEqual(IEntity ent1, IEntity ent2, EntityGraphEqualOptions options, IObject<?>... ignoreValues) {
        return fullyEqualValues(ent1, ent2, options, new HashSet<IEntity>(), toIgnorePath(ignoreValues));
    }

    /**
     *
     * @param ent1
     *            entity1 to compare
     * @param ent2
     *            entity2 to compare
     * @param ignoreValues
     *            ignore values of selected members
     *
     *            Example: EntityGraph.ownedEqualValues(origSchedule, updateSchedule, origSchedule.timestamp());
     * @return
     */
    public static boolean ownedEqualValues(IEntity ent1, IEntity ent2, IObject<?>... ignoreValues) {
        return fullyEqualValues(ent1, ent2, new EntityGraphEqualOptions(true), new HashSet<IEntity>(), toIgnorePath(ignoreValues));
    }

    private static Set<Path> toIgnorePath(IObject<?>... ignoreValues) {
        Set<Path> ignorePath = new HashSet<Path>();
        if (ignoreValues != null) {
            for (IObject<?> ignore : ignoreValues) {
                ignorePath.add(ignore.getPath());
            }
        }
        return ignorePath;
    }

    @SuppressWarnings("unchecked")
    private static boolean fullyEqualValues(IEntity ent1, IEntity ent2, EntityGraphEqualOptions options, Set<IEntity> processed, Set<Path> ignorePaths) {
        if (ent1 == ent2) {
            return true;
        } else if ((ent1 == null) || (ent2 == null)) {
            return false;
        }

        // Cast if required to concert instance
        ent1 = ent1.cast();
        ent2 = ent2.cast();

        if (processed.contains(ent1)) {
            return true;
        }
        if ((!ent1.getInstanceValueClass().equals(ent2.getInstanceValueClass()))) {
            return false;
        }

        processed.add(ent1);

        EntityMeta em = ent1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (options.ignoreTransient && memberMeta.isTransient()) {
                continue;
            }
            if (options.ignoreRpcTransient && memberMeta.isRpcTransient()) {
                continue;
            }
            IObject<?> member1 = ent1.getMember(memberName);
            if (ignorePaths.contains(member1.getPath())) {
                continue;
            }

            IObject<?> member2 = ent2.getMember(memberName);
            if ((options.ignoreDetachedMembers) && ((member1.getAttachLevel() == AttachLevel.Detached) || (member2.getAttachLevel() == AttachLevel.Detached))) {
                continue;
            } else if ((member1.getAttachLevel() == AttachLevel.Detached) && (member2.getAttachLevel() == AttachLevel.Detached)) {
                continue;
            }

            switch (memberMeta.getObjectClassType()) {
            case Entity:
                IEntity ent1Member = (IEntity) member1;
                IEntity ent2Member = (IEntity) member2;
                if (ent1Member.isNull() && ent2Member.isNull()) {
                    continue;
                } else if (memberMeta.isEmbedded()) {
                    if (!fullyEqualValues(ent1Member, ent2Member, options, processed, ignorePaths)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                } else if (!memberMeta.isOwnedRelationships()
                        && ((member1.getAttachLevel() != AttachLevel.Attached) || (member2.getAttachLevel() != AttachLevel.Attached))) {
                    // Reference to other entity
                    if (!ent1Member.equals(ent2Member)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                            log.debug("changed [{}] -> [{}]", ent1Member.getDebugExceptionInfoString(), ent2Member.getDebugExceptionInfoString());
                        }
                        return false;
                    }
                } else if (options.ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!ent1Member.equals(ent2Member)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                            log.debug("changed [{}] -> [{}]", ent1Member.getDebugExceptionInfoString(), ent2Member.getDebugExceptionInfoString());
                        }
                        return false;
                    }
                } else if (!fullyEqualValues(ent1Member, ent2Member, options, processed, ignorePaths)) {
                    if (!ent1.hasValues() && !ent2.hasValues()) {
                        if (options.trace) {
                            log.debug("ignore owner {} changes {}", memberName, member1.getPath());
                        }
                    } else {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                }
                break;
            case EntityList:
                if (options.ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                } else {
                    if (!fullyEqualValues((IList<IEntity>) member1, (IList<IEntity>) member2, options, processed)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                }
                break;
            case EntitySet:
                if (options.ownedValuesOnly && !memberMeta.isOwnedRelationships()) {
                    if (!EqualsHelper.equals(member1, member2)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                } else {
                    if (!fullyEqualValues((ISet<IEntity>) member1, (ISet<IEntity>) member2, options, processed)) {
                        if (options.trace) {
                            log.info("changes in member {} {}", memberName, member1.getPath());
                        }
                        return false;
                    }
                }
                break;
            default:
                if (!EqualsHelper.equals(member1, member2)) {
                    if (options.trace) {
                        log.info("changes in member {} {}", memberName, member1.getPath());
                        log.debug("[{}] -> [{}]", member1, member2);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean fullyEqualValues(ISet<IEntity> set1, ISet<IEntity> set2, EntityGraphEqualOptions options, Set<IEntity> processed) {
        if (set1.size() != set2.size()) {
            return false;
        }
        if (set1.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
            return true;
        }
        Iterator<IEntity> iter1 = set1.iterator();
        List<IEntity> set2copy = new Vector<IEntity>(set2);
        set1Loop: while (iter1.hasNext()) {
            IEntity ent1 = iter1.next();
            // Find first entity with the same data
            Iterator<IEntity> iter2 = set2copy.iterator();
            while (iter2.hasNext()) {
                if (!fullyEqualValues(ent1, iter2.next(), options, processed, new HashSet<Path>())) {
                    // Do not compare the same objects twice
                    iter2.remove();
                    continue set1Loop;
                }
            }
            return true;
        }
        return true;
    }

    private static boolean fullyEqualValues(IList<IEntity> value1, IList<IEntity> value2, EntityGraphEqualOptions options, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return false;
        }
        if (value1.getAttachLevel() == AttachLevel.CollectionSizeOnly) {
            return true;
        }
        Iterator<IEntity> iter1 = value1.iterator();
        Iterator<IEntity> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            if (!fullyEqualValues(iter1.next(), iter2.next(), options, processed, new HashSet<Path>())) {
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
     * Set value only if the value is null
     *
     * @param valueMemeber
     * @param value
     * @return true if the value is changed
     */
    public static <S extends Serializable> boolean setDefault(IPrimitive<S> valueMember, S value) {
        if (valueMember.isNull()) {
            valueMember.setValue(value);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static void setDefaults(IEntity src, IEntity dst, IPrimitive<?>... protoValues) {
        for (IPrimitive<?> member : protoValues) {
            String memberFieldName = member.getFieldName();
            IPrimitive<Serializable> dstMember = (IPrimitive<Serializable>) dst.getMember(memberFieldName);
            if (dstMember.isNull()) {
                dstMember.setValue(src.getMemberValue(memberFieldName));
            }
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
        enshureSingleEntityValue(rootEntity);
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
     * Use entity.equals() by PK to make object present only once.
     *
     * NOTE: As of now persistence may retrieve cross references multiple times. (excluding root entity)
     * Different references of the same entity may have different attach level... So represent using different shell instances and value map.
     */
    public static <E extends IEntity> void enshureSingleEntityValue(final E rootEntity) {
        final Map<IEntity, IEntity> instances = new HashMap<>();
        applyRecursivelyAllObjects(rootEntity, new ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if (entity == rootEntity) {
                    instances.put(entity, entity);
                } else {
                    if (instances.containsKey(entity)) {
                        IEntity valueInstance = instances.get(entity);
                        if (valueInstance.getValue() != entity.getValue()) {
                            entity.setValue(valueInstance.getValue());
                        }
                    } else {
                        instances.put(entity, entity);
                    }
                }
                return true;
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
