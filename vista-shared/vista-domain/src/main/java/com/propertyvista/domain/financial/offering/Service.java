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

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Service.ServiceV;

@DiscriminatorValue("service")
public interface Service extends Product, IVersionedEntity<ServiceV> {

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

    @Timestamp
    IPrimitive<Date> updated();

    @ToStringFormat("{0}, {1}")
    @DiscriminatorValue("service")
    public interface ServiceV extends Product.ProductV, IVersionData<Service> {

        @NotNull
        @ToString(index = 0)
        @MemberColumn(name = "serviceType")
        IPrimitive<Type> type();

        // ----------------------------------------------------
        // eligibility matrix:

        @Versioned
        @Detached
        IList<Feature> features();

        @Versioned
        @Detached
        IList<Concession> concessions();
    }
}
