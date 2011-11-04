/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.property.asset.building.Building;

/**
 * Q: Why Equipment is not a BuildingElement?
 * 
 * Actually, all that equipment not a building elements in terms of... like, let say unit, locker, parking etc...
 * But, most importantly, this term used mostly for business-money purpose : connect service items in catalog and items which they sell/rent...
 * end we do not rent equipment (but may be?)
 * Theoretically, nothing of this prohibit equipment to be a building element too...
 * 
 */
@Inheritance
@AbstractEntity
public interface BuildingElement extends IEntity {

    @Owner
    @Detached
    @ReadOnly
    @Caption(name = "Building")
    @XmlTransient
    Building belongsTo();
}
