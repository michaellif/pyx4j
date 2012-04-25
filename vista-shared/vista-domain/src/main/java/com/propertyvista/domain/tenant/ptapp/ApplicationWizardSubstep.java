/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.ptapp;

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep.Status;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ApplicationWizardSubstep extends IEntity {

    @NotNull
    @ReadOnly
    @Owner
    @JoinColumn
    ApplicationWizardStep step();

    @OrderColumn
    IPrimitive<Integer> substepOrder();

    IPrimitive<String> name();

    IPrimitive<String> placeArgument();

    IPrimitive<Status> status();
}
