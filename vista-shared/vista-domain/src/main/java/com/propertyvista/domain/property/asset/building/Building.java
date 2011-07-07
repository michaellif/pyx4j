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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.domain.media.Media;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.Complex;

//TODO rename to Property?!
public interface Building extends IEntity {

    @ToString
    @EmbeddedEntity
    BuildingInfo info();

    @EmbeddedEntity
    BuildingFinancial financial();

    @EmbeddedEntity
    BuildingContactInfo contacts();

    @EmbeddedEntity
    Marketing marketing();

    // there is a drop-down box with create new complex  
    Complex complex();

    @Detached
    IList<Media> media();

    IList<Feature> featureCatalog();
}
