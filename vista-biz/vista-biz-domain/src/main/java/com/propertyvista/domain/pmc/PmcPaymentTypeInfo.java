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
package com.propertyvista.domain.pmc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
@Caption(name = "PMC Payment Type Info")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcPaymentTypeInfo extends AbstractPaymentFees {

    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed(uniqueConstraint = true)
    @Detached
    Pmc pmc();

    //This not used for now.

    IPrimitive<Boolean> ccVisaPaymentAvailable();

    IPrimitive<Boolean> ccMasterCardPaymentAvailable();

    IPrimitive<Boolean> ccDiscoverPaymentAvailable();

    IPrimitive<Boolean> ccAmexPaymentAvailable();

    //--

    IPrimitive<Boolean> eCheckPaymentAvailable();

    IPrimitive<Boolean> eftPaymentAvailable();

    IPrimitive<Boolean> interacCaledonPaymentAvailable();

    IPrimitive<Boolean> interacPaymentPadPaymentAvailable();

    IPrimitive<Boolean> interacVisaPaymentAvailable();

}
