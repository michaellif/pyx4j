/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.role;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleListerViewImpl extends CrmListerViewImplBase<CrmRole> implements CrmRoleListerView {

    public CrmRoleListerViewImpl() {
        super(CrmSiteMap.Settings.UserRole.class);
        setLister(new CrmRoleLister());
    }

    private static class CrmRoleLister extends ListerBase<CrmRole> {

        public CrmRoleLister() {
            super(CrmRole.class, CrmSiteMap.Settings.UserRole.class, false, true);
            setColumnDescriptors(

            new MemberColumnDescriptor.Builder(proto().name()).build(),

            new MemberColumnDescriptor.Builder(proto().description()).build());
        }
    }
}
