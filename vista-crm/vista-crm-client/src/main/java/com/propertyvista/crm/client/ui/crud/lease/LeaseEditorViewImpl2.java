/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseEditorViewImplBase2;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseEditorViewImpl2 extends LeaseEditorViewImplBase2<LeaseDTO2> implements LeaseEditorView2 {

    public LeaseEditorViewImpl2() {
        super(CrmSiteMap.Tenants.Lease2.class);
        setForm(new LeaseNewForm());
    }
}
