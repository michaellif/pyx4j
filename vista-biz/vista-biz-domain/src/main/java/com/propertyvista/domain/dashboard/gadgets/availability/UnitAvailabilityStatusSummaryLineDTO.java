/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.availability;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface UnitAvailabilityStatusSummaryLineDTO extends IEntity {

    @I18n
    public enum AvailabilityCategory {

        total, occupied, vacant, vacantRented, notice, noticeRented, netExposure;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @Caption(name = "")
    IPrimitive<AvailabilityCategory> category();

    IPrimitive<Integer> units();

    @Format("#0.00%")
    IPrimitive<Double> percentage();
}
