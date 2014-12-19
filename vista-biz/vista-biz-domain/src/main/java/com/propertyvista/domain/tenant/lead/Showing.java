/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 8, 2011
 * @author Vlad
 */
package com.propertyvista.domain.tenant.lead;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface Showing extends IEntity {

    @I18n
    public enum Status {

        planned,

        seen;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum Result {

        interested,

        notInterested;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum Reason {

        tooExpensive,

        tooSmall,

        tooDark,

        other;

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
    @JoinColumn
    Appointment appointment();

    @NotNull
    @ToString(index = 0)
    AptUnit unit();

    @NotNull
    @ToString(index = 1)
    IPrimitive<Status> status();

    @ToString(index = 2)
    IPrimitive<Result> result();

    IPrimitive<Reason> reason();
}
