/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.kijiji.util;

import com.kijiji.pint.rs.ILSUnit;

import com.propertyvista.domain.property.asset.unit.AptUnit;

public class KijijiListingProcessor {
    public ILSUnit convert(AptUnit aptUnit) {
        ILSUnit unit = new ILSUnit();
        //unit.setClientUnitId(aptUnit.info().number().getValue());
        return unit;
    }
}
