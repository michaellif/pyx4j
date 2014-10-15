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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface YardiPaymentPostingBatch extends IEntity {

    @I18n
    public enum YardiPostingStatus {

        Open,

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

    @ToString(index = 0)
    @Editor(type = EditorType.label)
    @Caption(name = "Yardi Batch #")
    IPrimitive<String> externalBatchNumber();

    @ToString(index = 1)
    IPrimitive<YardiPostingStatus> status();

    @ReadOnly
    @Timestamp(Update.Created)
    IPrimitive<Date> creationDate();

    IPrimitive<Date> finalizeDate();

    @NotNull
    IPrimitive<Boolean> postFailed();

    IPrimitive<String> postFailedErrorMessage();

    @NotNull
    IPrimitive<Boolean> cancelFailed();

    IPrimitive<String> cancelFailedErrorMessage();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<YardiPaymentPostingBatchRecord> records();
}
