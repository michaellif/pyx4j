/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("{0}, {1}")
public interface BuildingUtility extends IEntity {

    @I18n
    @I18nComment("Building Utiltiy Type")
    @XmlType(name = "BuildingUtiltiyType")
    public enum Type {

        airConditioning,

        electricity,

        internet,

        cable,

        garbage,

        gas,

        heating,

        hydro,

        sewage,

        television,

        telephone,

        water,

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
    @Indexed
    @JoinColumn
    Building building();

    @Length(2048)
    IPrimitive<String> description();

    @Length(128)
    @ToString(index = 1)
    IPrimitive<String> name();

    @OrderColumn
    IPrimitive<Integer> orderInBuilding();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "buildingUtilityType")
    IPrimitive<Type> type();

    @NotNull
    IPrimitive<Boolean> isDeleted();
}
