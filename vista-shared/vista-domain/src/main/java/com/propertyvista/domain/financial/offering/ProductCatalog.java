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
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

/**
 * Defines Services on a given Building
 */
public interface ProductCatalog extends IEntity {

    @Timestamp
    IPrimitive<Date> updated();

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    Building building();

    // ----------------------------------------------------
    // parent <-> child relationship:
    // note, that declaration order is important!

    @Owned
    @Detached
    IList<Concession> concessions();

    @Owned
    @Detached
    IList<Feature> features();

    @Owned
    @Detached
    IList<Service> services();

    // ----------------------------------------------------

    // Utilities included in price and should be EXCLUDED from Lease Service Agreement 
    IList<ProductItemType> includedUtilities();

    // Utilities provided by 3-d party and should be EXCLUDED from Lease Service Agreement 
    IList<ProductItemType> externalUtilities();

}
