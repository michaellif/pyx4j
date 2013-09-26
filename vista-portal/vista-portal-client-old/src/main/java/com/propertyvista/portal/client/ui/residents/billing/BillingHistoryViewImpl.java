/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import com.propertyvista.portal.client.ui.residents.ViewBase;
import com.propertyvista.portal.client.ui.residents.ViewImpl;
import com.propertyvista.portal.domain.dto.BillListDTO;

public class BillingHistoryViewImpl extends ViewImpl<BillListDTO> implements BillingHistoryView {

    public BillingHistoryViewImpl() {
        super(new BillingHistoryForm(), true, true);
    }

    @Override
    public void setPresenter(ViewBase.Presenter<BillListDTO> presenter) {
        super.setPresenter(presenter);
        ((BillingHistoryForm) getForm()).setPresenter((BillingHistoryView.Presenter) presenter);
    }
}
