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
 */
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.site.SocialLink;
import com.propertyvista.domain.site.SocialLink.SocialSite;

class SocialLinkFolder extends VistaBoxFolder<SocialLink> {
    private static final I18n i18n = I18n.get(SocialLinkFolder.class);

    private final Set<SocialSite> usedSites = new HashSet<SocialSite>();

    public SocialLinkFolder(boolean modifyable) {
        super(SocialLink.class, modifyable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<SocialLink>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<SocialLink>> event) {
                updateUsedSites();
            }
        });
    }

    private void updateUsedSites() {
        usedSites.clear();
        for (SocialLink link : getValue()) {
            usedSites.add(link.socialSite().getValue());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateUsedSites();
    }

    @Override
    protected void addItem() {
        new SocialSiteSelector() {
            @Override
            public boolean onClickOk() {
                SocialSite socialSite = getSelectedSocialSite();
                if (socialSite != null) {
                    SocialLink link = EntityFactory.create(SocialLink.class);
                    link.socialSite().setValue(socialSite);
                    SocialLinkFolder.super.addItem(link);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<SocialLink> createItemForm(IObject<?> member) {
        return new SocialLinkEditor();
    }

    private abstract class SocialSiteSelector extends Dialog implements OkCancelOption {

        private SocialSite selectedSocialSite;

        public SocialSiteSelector() {
            super(i18n.tr("Social Site Selection"));
            setDialogPixelWidth(350);
            setDialogOptions(this);

            SimplePanel panel = new SimplePanel();
            CComboBox<SocialSite> selector = new CComboBox<SocialSite>();
            selector.setMandatory(true);

            ArrayList<SocialSite> options = new ArrayList<SocialSite>(Arrays.asList(SocialSite.values()));
            options.removeAll(usedSites);

            selector.populate(null);
            selector.setOptions(options);
            int optSize = options.size();
            if (optSize > 0) {
                selector.addValueChangeHandler(new ValueChangeHandler<SocialSite>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<SocialSite> event) {
                        selectedSocialSite = event.getValue();
                        getOkButton().setEnabled(true);
                    }
                });
                getOkButton().setEnabled(false);
                panel.setWidget(selector);
            } else {
                panel.setWidget(new Label(i18n.tr("Sorry, no more items to choose from.")));
            }

            panel.getElement().getStyle().setPadding(1, Unit.EM);
            setBody(panel);
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }

        public SocialSite getSelectedSocialSite() {
            return selectedSocialSite;
        }
    }

    private class SocialLinkEditor extends CForm<SocialLink> {

        public SocialLinkEditor() {
            super(SocialLink.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CLabel<String> site = new CLabel<String>();
            site.setEditable(false);
            formPanel.append(Location.Left, proto().socialSite(), site).decorate();
            formPanel.append(Location.Dual, proto().siteUrl()).decorate();
            get(proto().siteUrl()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public BasicValidationError isValid() {
                    if (getCComponent().getValue() == null || getCComponent().getValue().length() == 0) {
                        return new BasicValidationError(getCComponent(), i18n.tr("URL should not be empty"));
                    } else if (!ValidationUtils.isCorrectUrl(getCComponent().getValue())) {
                        return new BasicValidationError(getCComponent(), i18n.tr("Please use proper URL format"));
                    }
                    return null;
                }
            });
            return formPanel;
        }
    }
}