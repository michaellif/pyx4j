/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@ToStringFormat("Type: {0}, Name: {1}")
public interface Service extends IEntity {

    @Translatable
    @XmlType(name = "ServiceType")
    public enum Type {

        residentialUnit,

        commercialUnit,

        residentialShortTermUnit,

        roof,

        sundry,

        garage;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

// ----------------------------------------------

    @Owner
    @Detached
    @ReadOnly
    ServiceCatalog catalog();

// ----------------------------------------------

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "serviceType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<String> name();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Owned
    @Detached
    IList<ServiceItem> items();

    IPrimitive<DepositType> depositType();

    @Owned
    @Detached
    IList<ServiceFeature> features();

    @Owned
    @Detached
    IList<ServiceConcession> concessions();
}
