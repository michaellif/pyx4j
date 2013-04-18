/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import com.propertyvista.portal.client.ui.residents.View;
import com.propertyvista.portal.client.ui.residents.ViewImpl;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;

public class BillSummaryViewImpl extends ViewImpl<PvBillingFinancialSummaryDTO> implements BillSummaryView {

    public BillSummaryViewImpl() {
        super(new BillSummaryForm());
    }

    @Override
    public void setPresenter(View.Presenter<PvBillingFinancialSummaryDTO> presenter) {
        super.setPresenter(presenter);
        ((BillSummaryForm) getForm()).setPresenter((BillSummaryView.Presenter) presenter);
    }
}
