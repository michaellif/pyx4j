/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.profile.companyinfo;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.domain.settings.PmcCompanyInfoContact;

public class PmcCompanyInfoForm extends CrmEntityForm<PmcCompanyInfoDTO> {

    public PmcCompanyInfoForm(IForm<PmcCompanyInfoDTO> view) {
        super(PmcCompanyInfoDTO.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().companyName()), 20).build());

        content.setH1(++row, 0, 2, proto().contacts().getMeta().getCaption());
        content.setWidget(++row, 0, 2, inject(proto().contacts(), new PmcCompanyInfoContactFolder()));

        selectTab(addTab(content));
        setTabBarVisible(false);
    }

    private class PmcCompanyInfoContactFolder extends VistaTableFolder<PmcCompanyInfoContact> {

        public PmcCompanyInfoContactFolder() {
            super(PmcCompanyInfoContact.class, PmcCompanyInfoForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().name(), "20em"),
                new EntityFolderColumnDescriptor(proto().phone(), "12em"),
                new EntityFolderColumnDescriptor(proto().email(), "15em"),
                new EntityFolderColumnDescriptor(proto().type(), "10em"));
            //@formatter:on
        }
    }
}
