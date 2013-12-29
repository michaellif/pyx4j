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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.CollectionValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public class NotEmptyValidator<E extends IEntity> implements CollectionValidator<E> {

    private static final I18n i18n = I18n.get(NotEmptyValidator.class);

    @Override
    public Set<ValidationFailure> validate(ICollection<E, ?> obj) {
        if (obj.isEmpty()) {
            Set<ValidationFailure> result = new HashSet<ValidationFailure>();
            result.add(new SimpleValidationFailure(obj, i18n.tr("{0} must have at least one element", obj.getMeta().getCaption())));
            return result;
        } else {
            return Collections.emptySet();
        }
    }

}
