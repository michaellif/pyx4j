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
package com.propertyvista.domain.property.asset.unit;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface AptUnit extends IEntity {

	@EmbeddedEntity
	AptUnitInfo info();

	/**
	 * Keeps current and future occupancy data
	 */
	IList<AptUnitOccupancy> occupancies();

	@Transient
	IPrimitive<Double> numberOfOccupants();

	/**
	 * Denormalized field used for search, derived from @see AptUnitOccupancy
	 * TODO should be calculated during Entity save
	 * 
	 * @deprecated remove deprecated once it is calculated and filled properly.
	 */
	@Deprecated
	@Indexed
	@Format("MM/dd/yyyy")
	@Caption(name = "Available")
	IPrimitive<java.sql.Date> avalableForRent();

	@EmbeddedEntity
	AptUnitFinancial financial();

	@EmbeddedEntity
	AptUnitMarketing marketing();

	IList<AptUnitAmenity> amenities();

	IList<Concession> concessions();

	IList<AddOn> addOns();

	@Detached
	IList<Medium> media();
}
