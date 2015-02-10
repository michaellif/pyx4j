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
 */
package com.propertyvista.crm.client.ui.crud.administration.profile.companyinfo;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.domain.settings.PmcCompanyInfoContact;

public class PmcCompanyInfoForm extends CrmEntityForm<PmcCompanyInfoDTO> {

    private static final I18n i18n = I18n.get(PmcCompanyInfoForm.class);

    public PmcCompanyInfoForm(IPrimeFormView<PmcCompanyInfoDTO, ?> view) {
        super(PmcCompanyInfoDTO.class, view);

        selectTab(addTab(createGeneralTabContent(), i18n.tr("General")));
        selectTab(addTab(createWebsiteDnsNameTabContent(), i18n.tr("Website DNS Name")));
        selectTab(addTab(createPortalDnsNameTabContent(), i18n.tr("Portal DNS Name")));
    }

    FormPanel createGeneralTabContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().companyName()).decorate();

        formPanel.h1(proto().contacts().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().contacts(), new PmcCompanyInfoContactFolder());

        return formPanel;
    }

    private FormPanel createWebsiteDnsNameTabContent() {
        FormPanel formPanel = new FormPanel(this);

        if (!isViewable()) {
            HTML websiteHeader = new HTML(CrmResources.INSTANCE.dnsNameSetupWebsiteHeader().getText());
            websiteHeader.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Dual, websiteHeader);

            formPanel.br();
        }

        formPanel.append(Location.Left, proto().websiteDnsName()).decorate();

        if (!isViewable()) {
            formPanel.br();
            formPanel.br();

            HTML websiteFooter = new HTML(CrmResources.INSTANCE.dnsNameSetupFooter().getText());
            websiteFooter.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Dual, websiteFooter);
        }

        return formPanel;
    }

    private FormPanel createPortalDnsNameTabContent() {
        FormPanel formPanel = new FormPanel(this);

        if (!isViewable()) {
            HTML portalHeader = new HTML(CrmResources.INSTANCE.dnsNameSetupPortalHeader().getText());
            portalHeader.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Dual, portalHeader);

            formPanel.br();
        }

        formPanel.append(Location.Left, proto().portalDnsName()).decorate();

        if (!isViewable()) {
            formPanel.br();
            formPanel.br();

            HTML portalFooter = new HTML(CrmResources.INSTANCE.dnsNameSetupFooter().getText());
            portalFooter.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Dual, portalFooter);
        }

        return formPanel;
    }

    private class PmcCompanyInfoContactFolder extends VistaTableFolder<PmcCompanyInfoContact> {

        public PmcCompanyInfoContactFolder() {
            super(PmcCompanyInfoContact.class, PmcCompanyInfoForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().name(), "20em"),
                new FolderColumnDescriptor(proto().phone(), "12em"),
                new FolderColumnDescriptor(proto().email(), "15em"),
                new FolderColumnDescriptor(proto().type(), "10em"));
            //@formatter:on
        }
    }
}
