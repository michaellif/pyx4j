/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface PapChargeDTO extends IEntity {

    public enum ChangeType {

        Changed, Removed, New

    }

    IPrimitive<String> chargeName();

    IPrimitive<ChangeType> changeType();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> suspendedPrice();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> suspendedPreAuthorizedPaymentAmount();

    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> suspendedPreAuthorizedPaymentPercent();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> newPrice();

    // user defined: if charge is changed or new add it to preauthorized payments
    IPrimitive<Boolean> discardCharge();

    // user defined: (this field is supposed to be in sync with percent)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> newPreAuthorizedPaymentAmount();

    // user defined: (this field is supposed to be in sync with amount)
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> newPreAuthorizedPaymentPercent();

    // this is to be used for 'Cancel' operation
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> suggestedNewPreAuthorizedPaymentAmount();

    PapDTO _parentPap();

    IPrimitive<Boolean> isPivot();

}
