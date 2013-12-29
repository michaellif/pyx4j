/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.PublicVisibilityType;

@ToStringFormat("{0}{1,choice,null#|!null# ex.{1}}")
public interface PropertyContact extends IEntity {

    @I18n
    public enum PropertyContactType {

        mainOffice,

        administrator,

        superintendent,

        pool,

        poolEmergency,

        elevator,

        intercom,

        laundry,

        pointOfSale,

        fireMonitoring;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @MemberColumn(name = "phoneType")
    IPrimitive<PropertyContactType> type();

    IPrimitive<String> name();

    @NotNull
    IPrimitive<PublicVisibilityType> visibility();

    @ToString(index = 0)
    @MemberColumn(name = "phoneNumber")
    @Editor(type = EditorType.phone)
    @BusinessEqualValue
    IPrimitive<String> phone();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    IPrimitive<String> description();

}
