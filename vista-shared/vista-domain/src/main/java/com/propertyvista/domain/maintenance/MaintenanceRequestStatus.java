/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("{0}")
public interface MaintenanceRequestStatus extends IEntity {

    @I18n(context = "Maintenance Request")
    public enum StatusPhase {

        Submitted, Scheduled, Resolved, Cancelled;

        public static Set<StatusPhase> open() {
            return EnumSet.of(Submitted, Scheduled);
        }

        public static Set<StatusPhase> closed() {
            return EnumSet.of(Resolved, Cancelled);
        }

        public Set<StatusPhase> transitions() {
            switch (this) {
            case Submitted:
            case Scheduled:
                return EnumSet.of(Scheduled, Resolved, Cancelled);
            default:
                return EnumSet.noneOf(StatusPhase.class);
            }
        }

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
    MaintenanceRequestMetadata meta();

    @ToString(index = 0)
    IPrimitive<StatusPhase> phase();

    @ToString
    IPrimitive<String> name();
}