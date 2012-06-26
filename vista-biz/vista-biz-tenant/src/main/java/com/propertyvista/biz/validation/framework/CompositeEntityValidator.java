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
package com.propertyvista.biz.validation.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

public abstract class CompositeEntityValidator<E extends IEntity> implements EntityValidator<E> {

    private final E proto;

    private final Map<Path, Validator> validators;

    public CompositeEntityValidator(Class<E> entityClassLiteral) {
        this.validators = new HashMap<Path, Validator>();
        this.proto = EntityFactory.create(entityClassLiteral);
        init();
    }

    @Override
    public final java.util.Set<ValidationFailure<?>> validate(E obj) {
        Set<ValidationFailure<?>> failures = new HashSet<ValidationFailure<?>>();

        for (Entry<Path, Validator> entry : validators.entrySet()) {
            Set<ValidationFailure<?>> subFailures = entry.getValue().validate(obj.getMember(entry.getKey()));
            failures.addAll(subFailures);
        }

        return failures;
    }

    public final E proto() {
        return proto;
    }

    protected abstract void init();

    protected final <T> void bind(IObject<T> member, Validator<T, IObject<T>> validator) {
        validators.put(member.getPath(), validator);
    };
}
