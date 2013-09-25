/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.server.AbstractListServiceImpl;

import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public class SelectPortfolioListServiceImpl extends AbstractListServiceImpl<Portfolio> implements SelectPortfolioListService {

    public SelectPortfolioListServiceImpl() {
        super(Portfolio.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }
}
