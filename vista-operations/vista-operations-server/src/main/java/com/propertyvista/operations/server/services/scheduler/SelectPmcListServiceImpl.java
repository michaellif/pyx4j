/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.scheduler;

import com.pyx4j.entity.server.AbstractListServiceImpl;

import com.propertyvista.operations.rpc.services.scheduler.SelectPmcListService;
import com.propertyvista.domain.pmc.Pmc;

public class SelectPmcListServiceImpl extends AbstractListServiceImpl<Pmc> implements SelectPmcListService {

    public SelectPmcListServiceImpl() {
        super(Pmc.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}
