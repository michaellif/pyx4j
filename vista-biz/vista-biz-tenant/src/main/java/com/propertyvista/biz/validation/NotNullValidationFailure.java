/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.i18n.shared.I18n;

public class NotNullValidationFailure implements ValidationFailure {

    private static final I18n i18n = I18n.get(NotNullValidationFailure.class);

    private final IObject<?> member;

    public NotNullValidationFailure(IObject<?> member, String message) {
        this.member = member;
    }

    @Override
    public String getMessage() {
        return i18n.tr("{0} is mandatory", member.getMeta().getCaption());
    }
}
