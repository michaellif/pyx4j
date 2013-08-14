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
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.dto.TenantDTO;

public class TenantLister extends AbstractLister<TenantDTO> {

    protected static final I18n i18n = I18n.get(TenantLister.class);

    private Button padFileUpload;

    public TenantLister() {
        this(false);
    }

    public TenantLister(boolean addDownloadListOfTenantsWithouPortalAccess) {
        super(TenantDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().participantId()).build(),
            new Builder(proto().role()).sortable(false).searchable(false).build(),
            
            new Builder(proto().customer().person().name()).searchable(false).build(),
            new Builder(proto().customer().person().name().firstName(), false).build(),
            new Builder(proto().customer().person().name().lastName(), false).build(),
            new Builder(proto().customer().person().sex(), false).build(),
            new Builder(proto().customer().person().birthDate()).build(),
            
            new Builder(proto().customer().person().homePhone()).build(),
            new Builder(proto().customer().person().mobilePhone(), false).build(),
            new Builder(proto().customer().person().workPhone(), false).build(),
            new Builder(proto().customer().person().email()).build(),
            new Builder(proto().customer().registeredInPortal()).visible(false).build(),
            
            new Builder(proto().lease()).searchable(false).build(),
            new Builder(proto().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build(),
            
            new Builder(proto().lease().unit().info().number()).columnTitle(i18n.tr("Unit #")).build(),
            new Builder(proto().lease().unit().building().propertyCode()).visible(false).build(),
            new Builder(proto().lease().unit().building().info().name()).visible(false).columnTitle(i18n.tr("Building Name")).build()
            
        ); // @formatter:on

        addActionItem(padFileUpload = new Button(i18n.tr("Upload PAD File"), new Command() {
            @Override
            public void execute() {
                ((TenantListerView.Presenter) getPresenter()).uploadPadFile();
            }
        }));

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().lease().leaseId(), false), new Sort(proto().customer().person().name(), false));
    }

    public void setTenantPadFileUploadEnable(boolean isEnabled) {
        padFileUpload.setVisible(isEnabled);
    }
}
