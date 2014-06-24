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

import java.util.Set;

import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public interface BillingCycleBillListerView extends ILister<BillDataDTO> {

    interface Presenter extends ILister.Presenter<BillDataDTO> {

        void confirm(Set<BillDataDTO> bills);

        void reject(Set<BillDataDTO> bills, String reason);

        void print(Set<BillDataDTO> bills);
    }

    void setActionButtonsVisible(boolean visible);
}
