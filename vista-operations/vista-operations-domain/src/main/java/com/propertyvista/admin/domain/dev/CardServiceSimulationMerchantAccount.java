/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.dev;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.adminNamespace)
public interface CardServiceSimulationMerchantAccount extends IEntity {

    @ToString
    @Indexed(uniqueConstraint = true)
    @NotNull
    IPrimitive<String> terminalID();

    @Editor(type = EditorType.money)
    @Format("#0.00")
    IPrimitive<BigDecimal> balance();

    @Caption(description = "Force rejection code on all transactions to this Account")
    IPrimitive<String> responseCode();

    @Timestamp(Update.Created)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> created();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<CardServiceSimulationCard> cards();
}
