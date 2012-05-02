/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 27, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.LayoutModuleCrudService;
import com.propertyvista.domain.site.HomePageGadget;

public class LayoutModuleCrudServiceImpl extends AbstractCrudServiceImpl<HomePageGadget> implements LayoutModuleCrudService {

    public LayoutModuleCrudServiceImpl() {
        super(HomePageGadget.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

}
