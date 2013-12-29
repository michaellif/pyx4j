/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework.validators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;

import com.propertyvista.biz.validation.framework.CollectionValidator;
import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public class CollectionContentValidator<E extends IEntity> implements CollectionValidator<E> {

    private final EntityValidator<E> itemValidator;

    public CollectionContentValidator(EntityValidator<E> itemValidator) {
        this.itemValidator = itemValidator;
    }

    @Override
    public Set<ValidationFailure> validate(ICollection<E, ?> obj) {
        Set<ValidationFailure> result = new HashSet<ValidationFailure>();
        Iterator<E> i = obj.iterator();
        while (i.hasNext()) {
            result.addAll(itemValidator.validate(i.next()));
        }
        return result;
    }

}
