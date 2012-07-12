/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.interfaces.importer.model.UtilityIO;

public class UtilityConverter extends EntityDtoBinder<Utility, UtilityIO> {

    public UtilityConverter() {
        super(Utility.class, UtilityIO.class, false);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.name());
    }

}
