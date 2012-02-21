/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * General required information for all Income types.
 */
@AbstractEntity
@Inheritance
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IIncomeInfo extends IEntity {

    @Caption(name = "Description")
    IPrimitive<String> name();

    @NotNull
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> monthlyAmount();

    /**
     * Start of income period. For employment that would be employment start date.
     */
    @Caption(name = "Start Date")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> starts();

    @Caption(name = "End Date")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> ends();
}
