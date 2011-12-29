/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 21, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;

/**
 * Marker interface for policies that are available at unit/floorplan level.
 */
@AbstractEntity
@Inheritance
public interface UnitPolicy extends BuildingPolicy {

}
