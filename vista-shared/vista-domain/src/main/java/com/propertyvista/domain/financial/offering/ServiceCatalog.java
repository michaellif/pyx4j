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

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

/**
 * Defines Services on a given Building
 */
public interface ServiceCatalog extends IEntity {

    /**
     * This is small hack for no column table SQL update. Do not use.
     */
    @Deprecated
    IPrimitive<String> x();

    /**
     * Double links - main dependency in appropriate entity:\
     * 
     * Note: Is not maintained! Should be synchronised if necessary in service!!!
     * (see @link LeaseCrudServiceImpl.syncBuildingServiceCatalog(Building building)).
     */
    @Transient
    IList<Service> services();

    @Transient
    IList<Feature> features();

    @Transient
    IList<Concession> concessions();

    // ----------------------------------------------------

    // Utilities included in price and should be EXCLUDED from Lease Service Agreement 
    IList<ServiceItemType> includedUtilities();
}
