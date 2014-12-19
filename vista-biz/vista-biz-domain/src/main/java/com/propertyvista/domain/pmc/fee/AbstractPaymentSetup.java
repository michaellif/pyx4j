/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2014
 * @author vlads
 */
package com.propertyvista.domain.pmc.fee;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractPaymentSetup extends IEntity {

    @Caption(name = "Accepted eCheck (ACH)")
    IPrimitive<Boolean> acceptedEcheck();

    IPrimitive<Boolean> acceptedDirectBanking();

    IPrimitive<Boolean> acceptedVisa();

    IPrimitive<Boolean> acceptedVisaConvenienceFee();

    IPrimitive<Boolean> acceptedVisaDebit();

    IPrimitive<Boolean> acceptedVisaDebitConvenienceFee();

    IPrimitive<Boolean> acceptedMasterCard();

    IPrimitive<Boolean> acceptedMasterCardConvenienceFee();

}
