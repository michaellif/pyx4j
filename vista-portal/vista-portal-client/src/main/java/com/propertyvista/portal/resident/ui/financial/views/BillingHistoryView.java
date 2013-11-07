/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.views;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.shared.ui.IFormView;

public interface BillingHistoryView extends IFormView<BillingHistoryDTO> {

    interface Presenter extends IFormViewPresenter<BillingHistoryDTO> {

        void viewBill(Bill itemId);
    }
}
