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
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("Type: {0}, Name: {1}")
@DiscriminatorValue("service")
public interface Service extends Product {

    @I18n
    @XmlType(name = "ServiceType")
    public enum Type {

        residentialUnit,

        commercialUnit,

        residentialShortTermUnit,

        roof,

        sundry,

        garage,

        storage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "serviceType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<String> name();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    IPrimitive<DepositType> depositType();

    @Owned
    @Detached
    IList<ServiceFeature> features();

    @Owned
    @Detached
    IList<ServiceConcession> concessions();
}
