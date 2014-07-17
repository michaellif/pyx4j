/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tenantsure;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.TenantSureDTO;

public class TenantSureLister extends AbstractLister<TenantSureDTO> {

    public TenantSureLister() {
        super(TenantSureDTO.class, false, false);

        setDataTableModel(new DataTableModel<TenantSureDTO>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().pmc()).build(),
                    new MemberColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().propertyCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().certificateNumber()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().policy().status()).build(),
                    new MemberColumnDescriptor.Builder(proto().policy().cancellation()).build(),
                    new MemberColumnDescriptor.Builder(proto().policy().cancellationDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().policy().certificate().inceptionDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().policy().tenant().customer().person().name()).build()
            ));//@formatter:on
    }
}
