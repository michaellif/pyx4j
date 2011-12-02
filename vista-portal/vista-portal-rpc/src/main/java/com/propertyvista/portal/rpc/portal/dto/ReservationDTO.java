/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface ReservationDTO extends IEntity {

    @I18n(context = "Reservation")
    enum Status {

        Submitted,

        Approved,

        Completed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<String> description();

    @Format("MMM dd, yyyy")
    IPrimitive<LogicalDate> date();

    @Format("h:mm a")
    IPrimitive<Time> time();

    IPrimitive<Status> status();
}
