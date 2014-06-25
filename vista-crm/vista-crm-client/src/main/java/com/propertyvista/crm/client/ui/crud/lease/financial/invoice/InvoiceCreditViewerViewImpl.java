/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial.invoice;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceCreditDTO;

public class InvoiceCreditViewerViewImpl extends CrmViewerViewImplBase<InvoiceCreditDTO> implements InvoiceCreditViewerView {

    public InvoiceCreditViewerViewImpl() {
        super(true);

        setForm(new InvoiceCreditForm(this));
        setNotesVisible(false);
    }

    @Override
    protected void populateBreadcrumbs(InvoiceCreditDTO value) {
        // We don't need breadcrumbs for this view
    }

}
