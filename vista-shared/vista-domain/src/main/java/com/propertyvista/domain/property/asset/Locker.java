/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

//TODO - replace inheritance on IEntity!!
public interface Locker extends Rentable {

    @Translatable
    public enum Type {

        regular,

        large,

        small;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 1)
    @MemberColumn(name = "spotType")
    IPrimitive<Type> type();

    @Override
    @ToString(index = 0)
    IPrimitive<String> name();

    @Owner
    @Detached
    LockerArea belongsTo();
}
