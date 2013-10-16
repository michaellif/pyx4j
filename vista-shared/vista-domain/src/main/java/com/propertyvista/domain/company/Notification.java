/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.company;

import java.io.Serializable;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;

public interface Notification extends IEntity {

    @I18n
    public static enum NotificationType implements Serializable {

        @Translate("Electronic Payment Rejected (NSF)")
        ElectronicPaymentRejectedNsf,

        AutoPayReviewRequired,

        MaintenanceRequest;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @ReadOnly
    @Indexed
    @MemberColumn(notNull = true)
    @JoinColumn
    Employee employee();

    @ReadOnly
    @MemberColumn(name = "tp")
    IPrimitive<NotificationType> type();

    @Detached
    IList<Building> buildings();

    @Detached
    IList<Portfolio> portfolios();
}
