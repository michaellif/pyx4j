/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceDebitDTO;

public class InvoiceDebitViewerViewImpl extends CrmViewerViewImplBase<InvoiceDebitDTO> {

    public InvoiceDebitViewerViewImpl() {
        super(true);
        setForm(new InvoiceDebitForm(this));
        setNotesVisible(false);
    }

    @Override
    protected void populateBreadcrumbs(InvoiceDebitDTO value) {
        // This is not required here (or is it?)
    }
}
