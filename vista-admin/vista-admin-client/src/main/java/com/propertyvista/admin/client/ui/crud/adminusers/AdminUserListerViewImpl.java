/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.adminusers;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.admin.client.ui.crud.AdminListerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.AdminUserDTO;

public class AdminUserListerViewImpl extends AdminListerViewImplBase<AdminUserDTO> implements AdminUserListerView {

    public AdminUserListerViewImpl() {
        super(AdminSiteMap.Administration.AdminUsers.class);
        setLister(new AdminUserLister());
    }

    private static class AdminUserLister extends ListerBase<AdminUserDTO> {

        public AdminUserLister() {
            super(AdminUserDTO.class, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().email()).build(),
                    new MemberColumnDescriptor.Builder(proto().enabled()).build(),
                    new MemberColumnDescriptor.Builder(proto().created()).build()
            );//@formatter:on
        }
    }
}
