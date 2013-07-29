/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc.fee;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractPaymentFees extends IEntity {

    /**
     * this fee is percent of a transaction
     */
    IPrimitive<BigDecimal> ccVisaFee();

    IPrimitive<BigDecimal> ccMasterCardFee();

    //Not implemented
    IPrimitive<BigDecimal> ccDiscoverFee();

    //Not implemented
    IPrimitive<BigDecimal> ccAmexFee();

    IPrimitive<BigDecimal> visaDebitFee();

    //--

    IPrimitive<BigDecimal> eChequeFee();

    IPrimitive<BigDecimal> directBankingFee();

    //Not implemented
    IPrimitive<BigDecimal> interacCaledonFee();

    //Not implemented
    IPrimitive<BigDecimal> interacPaymentPadFee();

    //Not implemented
    IPrimitive<BigDecimal> interacVisaFee();
}
