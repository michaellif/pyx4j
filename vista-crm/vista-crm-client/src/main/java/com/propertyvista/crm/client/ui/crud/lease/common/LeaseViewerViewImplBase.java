/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositLifecycleLister;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImplBase<DTO extends LeaseDTO> extends CrmViewerViewImplBase<DTO> implements LeaseViewerViewBase<DTO> {

    protected final IListerView<DepositLifecycleDTO> depositLister;

    public LeaseViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(placeClass);

        depositLister = new ListerInternalViewImplBase<DepositLifecycleDTO>(new DepositLifecycleLister());
    }

    @Override
    public IListerView<DepositLifecycleDTO> getDepositListerView() {
        return depositLister;
    }
}
