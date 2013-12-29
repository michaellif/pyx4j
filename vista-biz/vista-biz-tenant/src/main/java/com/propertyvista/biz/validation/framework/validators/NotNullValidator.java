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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.validation.framework.MemberValidator;
import com.propertyvista.biz.validation.framework.SimpleValidationFailure;
import com.propertyvista.biz.validation.framework.ValidationFailure;

public class NotNullValidator implements MemberValidator {

    private static final I18n i18n = I18n.get(NotNullValidator.class);

    private final String validationFailedMessage;

    public NotNullValidator() {
        this(null);
    }

    public NotNullValidator(String validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    @Override
    public Set<ValidationFailure> validate(IObject<?> member) {
        if (member.isNull()) {
            String message = null;
            if (validationFailedMessage != null) {
                message = validationFailedMessage;
            } else {
                MemberMeta metadata = member.getMeta();
                String caption = metadata != null ? metadata.getCaption() : ((IEntity) member).getEntityMeta().getCaption();
                message = i18n.tr("{0} is mandatory", caption);
            }
            Set<ValidationFailure> result = new HashSet<ValidationFailure>();
            result.add(new SimpleValidationFailure(member, message));
            return result;
        } else {
            return Collections.emptySet();
        }
    }
}