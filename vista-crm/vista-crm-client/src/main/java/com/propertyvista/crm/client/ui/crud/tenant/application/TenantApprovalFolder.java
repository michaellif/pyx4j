/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.application;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.misc.EquifaxResult;

public class TenantApprovalFolder extends VistaTableFolder<TenantFinancialDTO> {

    public TenantApprovalFolder(boolean modifyable) {
        super(TenantFinancialDTO.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().person(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().equifaxApproval().percenrtageApproved(), "5em"));
        columns.add(new EntityFolderColumnDescriptor(proto().equifaxApproval().suggestedDecision(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().equifaxApproval().checkResultDetails(), "10em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(EquifaxResult.class)) {
            return new CEntityCrudHyperlink<EquifaxResult>(i18n.tr("View"), MainActivityMapper.getCrudAppPlace(EquifaxResult.class));
        }
        return super.create(member);
    }
}