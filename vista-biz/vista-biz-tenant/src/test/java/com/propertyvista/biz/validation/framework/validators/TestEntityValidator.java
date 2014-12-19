/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2012
 * @author Artyom
 */
package com.propertyvista.biz.validation.framework.validators;

import java.util.HashSet;
import java.util.Set;

import com.propertyvista.biz.validation.framework.EntityValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.domain.EntityB;

/** validates that value is zero */
class TestEntityValidator implements EntityValidator<EntityB> {

    @Override
    public Set<ValidationFailure> validate(EntityB entity) {
        Set<ValidationFailure> result = new HashSet<ValidationFailure>();
        if (entity.isNull() || !entity.value().getValue().equals(new Integer(0))) {
            result.add(new SimpleValidationFailure(entity, "value should be equal to zero"));
        }
        return result;
    }
}