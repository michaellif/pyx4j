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
package com.propertyvista.portal.rpc.ptapp.dto.welcomewizard;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface InsuranceDTO extends IEntity {

    enum InsuranceOptions {

        @Translate(value = "I want to purchace new insurance policy")
        wantToBuyInsurance,

        @Translate(value = "I already have insurance")
        alreadyHaveInsurance

//        @Translate(value = "I was born lucky (don't need insurance)")
//        dontNeedInsurance
        ;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    IPrimitive<InsuranceOptions> insuranceType();

    @EmbeddedEntity
    PurchaseInsuranceDTO purchaseInsurance();

    @EmbeddedEntity
    ExistingInsurance existingInsurance();
}
