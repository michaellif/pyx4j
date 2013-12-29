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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.offering.Service.ServiceV;

@ToStringFormat("{1}, {0}")
@DiscriminatorValue("service")
public interface Service extends Product<ServiceV> {

    @Timestamp
    IPrimitive<Date> updated();

    @DiscriminatorValue("service")
    public interface ServiceV extends Product.ProductV<Service> {

        // eligibility matrix:

        @Detached
        IList<Feature> features();

        @Detached
        IList<Concession> concessions();
    }

}
