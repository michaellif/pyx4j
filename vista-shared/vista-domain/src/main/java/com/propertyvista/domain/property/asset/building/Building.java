/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.property.asset.BuildingAmenity;
import com.propertyvista.domain.property.asset.Complex;

@ToStringFormat("{0} {1}")
//TODO rename to Property
public interface Building extends IEntity {

    @EmbeddedEntity
    BuildingInfo info();

    IList<BuildingAmenity> amenities();

    @EmbeddedEntity
    BuildingFinancial financial();

    @EmbeddedEntity
    BuildingContactInfo contacts();

    @EmbeddedEntity
    BuildingMarketing marketing();

    // there is a drop-down box with create new complex  
    Complex complex();
}
