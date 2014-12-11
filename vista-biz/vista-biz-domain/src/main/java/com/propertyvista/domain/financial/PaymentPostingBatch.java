/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.AbstractPmcUser;

@DiscriminatorValue("PaymentPostingBatch")
public interface PaymentPostingBatch extends IEntity, HasNotesAndAttachments {

    @I18n
    enum PostingStatus {

        Created,

        Posted,

        Canceled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    Building building();

    /** This is external batch number i.e. in Yardi */
    @ToString(index = 0)
    @Editor(type = EditorType.label)
    @Caption(name = "Yardi Batch #")
    IPrimitive<String> externalBatchNumber();

    @ToString(index = 1)
    IPrimitive<PostingStatus> status();

    @ReadOnly
    @Format("yyyy-MM-dd, HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Update.Created)
    IPrimitive<Date> created();

    @Format("yyyy-MM-dd, HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @ReadOnly
    @Detached(level = AttachLevel.ToStringMembers)
    AbstractPmcUser createdBy();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(PaymentRecord.class)
    ISet<PaymentRecord> payments();

    @EmbeddedEntity
    PaymentBatchBankDepositDetails depositDetails();
}
