/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.building.catalog;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.building.catalog.UtilityCrudService;
import com.propertyvista.domain.property.asset.Utility;

public class UtilityCrudServiceImpl extends AbstractCrudServiceImpl<Utility> implements UtilityCrudService {

    public UtilityCrudServiceImpl() {
        super(Utility.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }
}
