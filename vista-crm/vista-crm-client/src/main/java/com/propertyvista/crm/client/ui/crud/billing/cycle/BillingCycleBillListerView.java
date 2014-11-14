/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import java.util.Collection;

import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public interface BillingCycleBillListerView extends IPrimeListerView<BillDataDTO> {

    interface Presenter extends IPrimeListerView.IPrimeListerPresenter<BillDataDTO> {

        void confirm(Collection<BillDataDTO> bills);

        void reject(Collection<BillDataDTO> bills, String reason);

        void print(Collection<BillDataDTO> bills);
    }

    void setActionButtonsVisible(boolean visible);
}
