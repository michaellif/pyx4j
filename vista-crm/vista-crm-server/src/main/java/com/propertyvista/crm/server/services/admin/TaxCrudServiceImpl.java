/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.admin.TaxCrudService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxCrudServiceImpl extends AbstractCrudServiceImpl<Tax> implements TaxCrudService {

    public TaxCrudServiceImpl() {
        super(Tax.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}
