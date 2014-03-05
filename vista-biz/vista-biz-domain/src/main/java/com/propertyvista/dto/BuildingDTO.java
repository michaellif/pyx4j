/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileEmail;
import com.propertyvista.domain.marketing.ils.ILSSummaryBuilding;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
@ExtendsBO
public interface BuildingDTO extends Building {

    IList<ARCode> availableUtilities();

    @EmbeddedEntity
    GeoLocation geoLocation();

    MerchantAccount merchantAccount();

    IList<DashboardMetadata> dashboards();

    IPrimitive<Boolean> merchantAccountPresent();

    @Caption(name = "ILS Summary")
    IList<ILSSummaryBuilding> ilsSummary();

    IList<ILSProfileBuilding> ilsProfile();

    ILSProfileEmail ilsEmail();

    IPrimitive<Boolean> ilsEmailConfigured();
}
