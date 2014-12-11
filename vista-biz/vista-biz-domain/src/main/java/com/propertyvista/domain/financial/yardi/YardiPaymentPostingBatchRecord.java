/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial.yardi;

import java.util.Date;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.PaymentRecord;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface YardiPaymentPostingBatchRecord extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    YardiPaymentPostingBatch batch();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @NotNull
    IPrimitive<Boolean> added();

    @NotNull
    IPrimitive<Boolean> reversal();

    interface PaymentRecordColumnId extends ColumnId {
    }

    // TODO PostgreSQL 9.3 FOR NO KEY UPDATE
    @JoinColumn(PaymentRecordColumnId.class)
    @MemberColumn(notNull = true, createForeignKey = false)
    PaymentRecord paymentRecord();
}
