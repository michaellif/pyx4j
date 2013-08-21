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
import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractPaymentFees extends IEntity {

    /**
     * this fee is percent of a transaction
     */
    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> ccVisaFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> ccMasterCardFee();

    //Not implemented
    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> ccDiscoverFee();

    //Not implemented
    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> ccAmexFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> visaDebitFee();

    //--
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> eChequeFee();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> directBankingFee();

    //Not implemented
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> interacCaledonFee();

    //Not implemented
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> interacPaymentPadFee();

    //Not implemented
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> interacVisaFee();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();
}
