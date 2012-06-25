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
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcForm extends AdminEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcForm.class);

    public PmcForm() {
        this(false);
    }

    public PmcForm(boolean viewMode) {
        super(PmcDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("General"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingAccountId()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().namespace()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().vistaCrmUrl(), new CHyperlink(new Command() {
            @Override
            public void execute() {
                String url = getValue().vistaCrmUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, i18n.tr("CRM"), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }

        })), 50).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().residentPortalUrl(), new CHyperlink(new Command() {
            @Override
            public void execute() {
                String url = getValue().residentPortalUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, i18n.tr("Resident Portal"), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }

        })), 50).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().prospectPortalUrl(), new CHyperlink(new Command() {
            @Override
            public void execute() {
                String url = getValue().prospectPortalUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, i18n.tr("Prospect Portal"), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }

        })), 50).build());

        content.setH1(++row, 0, 2, proto().dnsNameAliases().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        selectTab(addTab(content, i18n.tr("General")));
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().status()).setViewable(true);

        boolean isVisible = get(proto().status()).getValue() != PmcStatus.Created;
        get(proto().vistaCrmUrl()).setVisible(isVisible);
        get(proto().residentPortalUrl()).setVisible(isVisible);
        get(proto().prospectPortalUrl()).setVisible(isVisible);
    }
}