/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type.base;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;

@Transient
@Inheritance
@AbstractEntity
public interface GadgetMetadata extends IEntity {

    public enum RefreshInterval {

        @Translate("Never")
        Never(-1),

        @Translate("15 min")
        min15L(15),

        @Translate("30 min")
        min30(30),

        @Translate("1 hour")
        hour1(60),

        @Translate("2 hours")
        hour2(120);

        /**
         * @param value
         *            refresh time in minutes
         */
        RefreshInterval(int value) {
            this.value = value * 60 * 1000;
        }

        private final int value;

        /** Interval in milliseconds */
        public int value() {
            return value;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    /** Refresh Period of the gadget in milliseconds (non positive value means the gadget never has to refresh itself) */
    @NotNull
    IPrimitive<RefreshInterval> refreshInterval();

    IPrimitive<String> gadgetId();

    @Detached
    @ReadOnly(allowOverrideNull = true)
    DashboardMetadata dashboard();

    @Detached
    @ReadOnly
    CrmUser ownerUser();

}
