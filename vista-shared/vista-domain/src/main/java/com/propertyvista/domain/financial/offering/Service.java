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
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Service.ServiceV;
import com.propertyvista.misc.VistaTODO;

@ToStringFormat("{1}, {0}")
@DiscriminatorValue("service")
public interface Service extends Product<ServiceV> {

    @I18n
    @XmlType(name = "ServiceType")
    public enum ServiceType {

        residentialUnit,

        residentialShortTermUnit,

        commercialUnit;

        /*
         * VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
         * 
         * roof,
         * 
         * sundry,
         * 
         * garage,
         * 
         * storage;
         */

        public static EnumSet<ServiceType> unitRelated() {
            if (VistaTODO.removedForProduction) {
                return EnumSet.of(residentialUnit);
            } else {
                return EnumSet.of(residentialUnit, residentialShortTermUnit, commercialUnit);
            }
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Timestamp
    IPrimitive<Date> updated();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(notNull = true)
    IPrimitive<ServiceType> serviceType();

    @DiscriminatorValue("service")
    public interface ServiceV extends Product.ProductV<Service> {

        // eligibility matrix:

        @Detached
        IList<Feature> features();

        @Detached
        IList<Concession> concessions();
    }

}
