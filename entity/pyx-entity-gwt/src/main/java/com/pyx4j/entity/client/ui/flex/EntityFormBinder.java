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
 * Created on Feb 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

//TODO we need Cleanup unused / invisible data in generic way
public class EntityFormBinder<E extends IEntity> extends EntityBinder<E> {

    private static final Logger log = LoggerFactory.getLogger(EntityFormBinder.class);

    private E origEntity;

    public EntityFormBinder(Class<E> clazz) {
        super(clazz);
    }

    public E getOrigValue() {
        return origEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void populate(E entity) {

        this.origEntity = entity;
        if (entity != null) {
            super.populate((E) entity.cloneEntity());
        } else {
            super.populate((E) EntityFactory.create(proto().getValueClass()));
        }
    }

    public boolean isChanged() {
        return !equalRecursive(getOrigValue(), getValue());
    }

    public static boolean equalRecursive(IEntity entity1, IEntity entity2) {
        return equalRecursive(entity1, entity2, new HashSet<IEntity>());
    }

    private static boolean equalRecursive(IEntity entity1, IEntity entity2, Set<IEntity> processed) {
        if (((entity2 == null) || entity2.isNull())) {
            return isEmptyEntity(entity1);
        } else if ((entity1 == null) || entity1.isNull()) {
            return isEmptyEntity(entity2);
        }
        if (processed != null) {
            if (processed.contains(entity1)) {
                return true;
            }
            processed.add(entity1);
        }
        EntityMeta em = entity1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            if (memberMeta.isEntity()) {
                if (memberMeta.isEmbedded()) {
                    if (!equalRecursive((IEntity) entity1.getMember(memberName), (IEntity) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (((IEntity) entity1.getMember(memberName)).isNull()) {
                    if (!((IEntity) entity2.getMember(memberName)).isNull()) {
                        log.debug("changed [null] -> [{}]", entity2.getMember(memberName));
                        return false;
                    }
                } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                    log.debug("changed [{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                    return false;
                }
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                //TODO OwnedRelationships
                if (!EqualsHelper.equals((ISet<?>) entity1.getMember(memberName), (ISet<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!listValuesEquals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (!EqualsHelper.equals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                log.debug("changed {}", memberName);
                log.debug("[{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                return false;
            }
        }
        return true;
    }

    private static boolean listValuesEquals(IList<?> value1, IList<?> value2, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return false;
        }
        Iterator<?> iter1 = value1.iterator();
        Iterator<?> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            if (!equalRecursive((IEntity) iter1.next(), (IEntity) iter2.next(), processed)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyEntity(IEntity entity) {
        if ((entity == null) || entity.isNull()) {
            return true;
        }
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            IObject<?> member = entity.getMember(memberName);
            if (member.isNull()) {
                continue;
            } else if (memberMeta.isEntity()) {
                if (!isEmptyEntity((IEntity) member)) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if ((ISet.class.equals(memberMeta.getObjectClass())) || (IList.class.equals(memberMeta.getObjectClass()))) {
                if (!((ICollection<?, ?>) member).isEmpty()) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if (Boolean.class.equals(memberMeta.getValueClass())) {
                // Special case for values presented by CheckBox
                if (member.getValue() == Boolean.TRUE) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else {
                log.debug("member {} not empty; {}", memberName, member);
                return false;
            }
        }
        return true;
    }
}
