/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.marketing;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.lease.LeaseParticipant;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface LeaseParticipantMoveInAction extends IEntity {

    public enum MoveInActionType {

        autoPay,

        insurance;

    }

    public enum MoveInActionStatus {

        doItLater,

        completed;

    }

    @Owner
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @MemberColumn(notNull = true)
    LeaseParticipant<?> leaseParticipant();

    @MemberColumn(name = "tp", notNull = true)
    IPrimitive<MoveInActionType> type();

    IPrimitive<MoveInActionStatus> status();
}
