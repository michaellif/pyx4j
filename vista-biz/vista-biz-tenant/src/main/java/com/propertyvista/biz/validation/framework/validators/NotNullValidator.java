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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.Validator;

public class NotNullValidator implements Validator {

    @Override
    public Set validate(IObject obj) {
        if (obj.isNull()) {
            Set<ValidationFailure> vaildationFailure = new HashSet<ValidationFailure>();
            vaildationFailure.add(new NotNullValidatoinFailure(obj));
            return vaildationFailure;
        } else {
            return Collections.EMPTY_SET;
        }
    }

    public static class NotNullValidatoinFailure implements ValidationFailure<Object> {

        private static final I18n i18n = I18n.get(NotNullValidator.NotNullValidatoinFailure.class);

        private final IObject<Object> property;

        public NotNullValidatoinFailure(IObject<Object> obj) {
            this.property = obj;
        }

        @Override
        public String getMessage() {
            return i18n.tr("{0} is a mandatory field", property.getMeta().getCaption());
        }

        @Override
        public IObject<Object> getProperty() {
            return property;
        }

    }
}
