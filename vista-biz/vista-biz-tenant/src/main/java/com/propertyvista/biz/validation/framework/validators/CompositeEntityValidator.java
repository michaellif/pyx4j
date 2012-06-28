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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

import com.propertyvista.biz.validation.framework.CollectionValidator;
import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.MemberValidator;
import com.propertyvista.biz.validation.framework.PrimitiveValidator;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public abstract class CompositeEntityValidator<E extends IEntity> implements EntityValidator<E> {

    private final E proto;

    private final Map<Path, MemberValidator> memberValidators;

    private final Map<Path, PrimitiveValidator> primitiveValidators;

    private final Map<Path, EntityValidator> entityValidators;

    private final Map<Path, CollectionValidator> collectionValidators;

    public CompositeEntityValidator(Class<E> entityClassLiteral) {
        this.memberValidators = new HashMap<Path, MemberValidator>();
        this.primitiveValidators = new HashMap<Path, PrimitiveValidator>();
        this.entityValidators = new HashMap<Path, EntityValidator>();
        this.collectionValidators = new HashMap<Path, CollectionValidator>();
        this.proto = EntityFactory.create(entityClassLiteral);
        init();
    }

    @Override
    public final Set<ValidationFailure> validate(E obj) {
        Set<ValidationFailure> failures = new HashSet<ValidationFailure>();

        for (Entry<Path, MemberValidator> entry : memberValidators.entrySet()) {
            Set<ValidationFailure> subFailures = entry.getValue().validate(obj.getMember(entry.getKey()));
            failures.addAll(subFailures);
        }
        for (Entry<Path, PrimitiveValidator> entry : primitiveValidators.entrySet()) {
            Set<ValidationFailure> subFailures = entry.getValue().validate((IPrimitive) obj.getMember(entry.getKey()));
            failures.addAll(subFailures);
        }
        for (Entry<Path, EntityValidator> entry : entityValidators.entrySet()) {
            Set<ValidationFailure> subFailures = entry.getValue().validate((IEntity) obj.getMember(entry.getKey()));
            failures.addAll(subFailures);
        }
        for (Entry<Path, CollectionValidator> entry : collectionValidators.entrySet()) {
            Set<ValidationFailure> subFailures = entry.getValue().validate((ICollection) obj.getMember(entry.getKey()));
            failures.addAll(subFailures);
        }

        return failures;
    }

    public final E proto() {
        return proto;
    }

    protected abstract void init();

    protected final void bind(IObject<?> member, MemberValidator validator) {
        memberValidators.put(member.getPath(), validator);
    }

    protected final <T extends Object> void bind(IPrimitive<T> primitiveMember, PrimitiveValidator<T> validator) {
        primitiveValidators.put(primitiveMember.getPath(), validator);
    }

    protected final <T extends IEntity> void bind(T member, EntityValidator<T> validator) {
        entityValidators.put(member.getPath(), validator);
    }

    protected final <T extends IEntity> void bind(ICollection<T, ?> member, CollectionValidator<T> validator) {
        collectionValidators.put(member.getPath(), validator);
    }

}
