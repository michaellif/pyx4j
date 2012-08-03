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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermViewerViewImpl extends CrmViewerViewImplBase<LeaseTermDTO> implements LeaseTermViewerView {

    public LeaseTermViewerViewImpl() {
        super(CrmSiteMap.Tenants.LeaseTerm.class, new LeaseTermForm(true));
    }
}
