/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public interface Complex extends IEntity {

    /**
     * Legal name of the property (max 120 char)
     */
    @ToString
    @Caption(name = "Legal Name")
    @NotNull
    IPrimitive<String> name();

    IPrimitive<String> website();

    @Detached
    DashboardMetadata dashboard();
// Let's leave just one-directional reference in Building?! 
//    ISet<Building> buildings();
}
