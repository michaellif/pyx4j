/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework.validators;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;

import com.propertyvista.biz.validation.framework.CollectionValidator;
import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.MemberValidator;
import com.propertyvista.biz.validation.framework.PrimitiveValidator;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public abstract class CompositeEntityValidator<E extends IEntity> implements EntityValidator<E> {

    private final E proto;

    private final Map<Path, Collection<MemberValidator>> memberValidators;

    @SuppressWarnings("rawtypes")
    private final Map<Path, Collection<PrimitiveValidator>> primitiveValidators;

    @SuppressWarnings("rawtypes")
    private final Map<Path, Collection<EntityValidator>> entityValidators;

    @SuppressWarnings("rawtypes")
    private final Map<Path, Collection<CollectionValidator>> collectionValidators;

    @SuppressWarnings("rawtypes")
    public CompositeEntityValidator(Class<E> entityClassLiteral) {
        this.memberValidators = new HashMap<Path, Collection<MemberValidator>>();
        this.primitiveValidators = new HashMap<Path, Collection<PrimitiveValidator>>();
        this.entityValidators = new HashMap<Path, Collection<EntityValidator>>();
        this.collectionValidators = new HashMap<Path, Collection<CollectionValidator>>();
        this.proto = EntityFactory.create(entityClassLiteral);
        init();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final Set<ValidationFailure> validate(E obj) {
        Set<ValidationFailure> failures = new HashSet<ValidationFailure>();

        prepare(obj); // allow custom validator to somehow prepare object...

        for (Entry<Path, Collection<MemberValidator>> entry : memberValidators.entrySet()) {
            for (MemberValidator validator : entry.getValue()) {
                Set<ValidationFailure> subFailures = validator.validate(obj.getMember(entry.getKey()));
                failures.addAll(subFailures);
            }
        }
        for (Entry<Path, Collection<PrimitiveValidator>> entry : primitiveValidators.entrySet()) {
            for (PrimitiveValidator validator : entry.getValue()) {
                Set<ValidationFailure> subFailures = validator.validate((IPrimitive) obj.getMember(entry.getKey()));
                failures.addAll(subFailures);
            }
        }
        for (Entry<Path, Collection<EntityValidator>> entry : entityValidators.entrySet()) {
            for (EntityValidator validator : entry.getValue()) {
                Set<ValidationFailure> subFailures = validator.validate((IEntity) obj.getMember(entry.getKey()));
                failures.addAll(subFailures);
            }
        }
        for (Entry<Path, Collection<CollectionValidator>> entry : collectionValidators.entrySet()) {
            for (CollectionValidator validator : entry.getValue()) {
                Set<ValidationFailure> subFailures = validator.validate((ICollection) obj.getMember(entry.getKey()));
                failures.addAll(subFailures);
            }
        }

        return failures;
    }

    public final E proto() {
        return proto;
    }

    /**
     * Override this method to create validator bindings using <code>bind()</code>
     */
    protected abstract void init();

    /**
     * Override this method to prepare Entity for validation (load detached members, etc.)
     */
    protected void prepare(E obj) {
    }

    protected final void bind(IObject<?> member, MemberValidator validator) {
        bind(memberValidators, member, validator);
    }

    protected final <T extends Serializable> void bind(IPrimitive<T> primitiveMember, PrimitiveValidator<T> validator) {
        bind(primitiveValidators, primitiveMember, validator);
    }

    protected final <T extends IEntity> void bind(T member, EntityValidator<T> validator) {
        bind(entityValidators, member, validator);
    }

    protected final <T extends IEntity> void bind(ICollection<T, ?> member, CollectionValidator<T> validator) {
        bind(collectionValidators, member, validator);
    }

    /**
     * Bind validator for the entities contained in the collection
     */
    protected final <T extends IEntity> void bind(ICollection<T, ?> member, EntityValidator<T> itemValidator) {
        bind(collectionValidators, member, new CollectionContentValidator<T>(itemValidator));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final static void bind(Map map, IObject<?> member, Object validator) {
        if (!map.containsKey(member.getPath())) {
            map.put(member.getPath(), new LinkedList());
        }
        ((LinkedList) map.get(member.getPath())).add(validator);
    }
}
