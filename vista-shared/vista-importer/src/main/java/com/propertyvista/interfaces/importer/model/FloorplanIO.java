/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FloorplanIO extends IEntity {

    IPrimitive<String> name();

    IPrimitive<String> marketingName();

    IPrimitive<String> description();

    IPrimitive<Integer> floorCount();

    IPrimitive<Integer> bedrooms();

    IPrimitive<Integer> dens();

    IPrimitive<Integer> bathrooms();

    IPrimitive<Integer> halfBath();

    IList<AmenityIO> amenities();

    IList<MediaIO> medias();

    IList<AptUnitIO> units();
}
