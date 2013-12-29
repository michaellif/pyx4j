/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.dto.payment;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ConvenienceFeeCalculationResponseTO extends IEntity {

    @NotNull
    IPrimitive<String> transactionNumber();

    @NotNull
    @Format("$#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @NotNull
    @Format("$#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> feeAmount();

    @NotNull
    @Format("#.00%")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> feePercentage();

    @ToString(index = 3)
    @Format("$#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> total();
}
