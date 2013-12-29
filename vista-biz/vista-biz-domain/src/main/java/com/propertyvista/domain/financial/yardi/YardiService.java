/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.financial.yardi;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@EmbeddedEntity
public interface YardiService extends IEntity {

    public enum Type {
        Rent, AirCon, Heat, Trash, BroadbandInternet, Cable, Electric, Gas, HotWater, Sewer, Water, Telephone, Fitness, Parking, Fees, Other,
    }

    IPrimitive<Type> type();
}
