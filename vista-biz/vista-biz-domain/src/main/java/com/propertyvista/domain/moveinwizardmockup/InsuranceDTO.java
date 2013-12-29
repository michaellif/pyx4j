/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.moveinwizardmockup;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface InsuranceDTO extends IEntity {

    @Caption(name = "I (We) Already Have Tenant Insurance")
    IPrimitive<Boolean> alreadyHaveInsurance();

    @EmbeddedEntity
    PurchaseInsuranceDTO purchaseInsurance();

    @EmbeddedEntity
    ExistingInsurance existingInsurance();
}
