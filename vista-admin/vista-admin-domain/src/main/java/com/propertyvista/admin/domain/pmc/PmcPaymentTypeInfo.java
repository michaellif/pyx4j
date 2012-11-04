/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.pmc;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(prefix = "admin", namespace = VistaNamespace.adminNamespace)
@Caption(name = "PMC Payment Type Info")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcPaymentTypeInfo extends IEntity {

    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed(uniqueConstraint = true)
    Pmc pmc();

    IPrimitive<Boolean> ccVisaPaymentAvailable();

    /**
     * this fee is percent of a transaction
     */
    IPrimitive<BigDecimal> ccVisaFee();

    IPrimitive<Boolean> ccMasterCardPaymentAvailable();

    IPrimitive<BigDecimal> ccMasterCardFee();

    IPrimitive<Boolean> ccDiscoverPaymentAvailable();

    IPrimitive<BigDecimal> ccDiscoverFee();

    IPrimitive<Boolean> ccAmexPaymentAvailable();

    IPrimitive<BigDecimal> ccAmexFee();

    //--

    IPrimitive<Boolean> eCheckPaymentAvailable();

    IPrimitive<BigDecimal> eChequeFee();

    IPrimitive<Boolean> eftPaymentAvailable();

    IPrimitive<BigDecimal> eftFee();

    IPrimitive<Boolean> interacCaledonPaymentAvailable();

    IPrimitive<BigDecimal> interacCaledonFee();

    IPrimitive<Boolean> interacPaymentPadPaymentAvailable();

    IPrimitive<BigDecimal> interacPaymentPadFee();

    IPrimitive<Boolean> interacVisaPaymentAvailable();

    IPrimitive<BigDecimal> interacVisaFee();

}
