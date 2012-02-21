/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.Currency;

public interface BuildingFinancial extends IEntity {

    @Format("MM/dd/yyyy")
    @NotNull
    IPrimitive<LogicalDate> dateAcquired();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> purchasePrice();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> marketPrice();

    @Format("MM/dd/yyyy")
    @NotNull
    IPrimitive<LogicalDate> lastAppraisalDate();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> lastAppraisalValue();

    /*
     * Type of currency used for this particular building
     * in all money-related fields.
     */
    Currency currency();
}