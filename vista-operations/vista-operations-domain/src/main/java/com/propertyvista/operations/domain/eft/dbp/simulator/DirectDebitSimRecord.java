/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.dbp.simulator;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface DirectDebitSimRecord extends IEntity {

    @Owner
    @JoinColumn
    @Indexed
    DirectDebitSimFile file();

    @NotNull
    @Length(14)
    @Indexed
    IPrimitive<String> accountNumber();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @Length(30)
    IPrimitive<String> paymentReferenceNumber();

    @Length(35)
    IPrimitive<String> customerName();

    @Timestamp(Timestamp.Update.Created)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> receivedDate();

}
