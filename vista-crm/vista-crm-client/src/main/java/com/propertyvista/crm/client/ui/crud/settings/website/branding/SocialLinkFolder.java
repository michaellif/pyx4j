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
package com.propertyvista.crm.client.ui.crud.settings.website.branding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
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
        new SocialSiteSelector().show();
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof SocialLink) {
            return new SocialLinkEditor();
        }
        return super.create(member);
    }

    class SocialSiteSelector extends Dialog implements CancelOption {
        public SocialSiteSelector() {
            super("Select Social Site");
            setDialogOptions(this);

            VerticalPanel panel = new VerticalPanel();
            CComboBox<SocialSite> selector = new CComboBox<SocialSite>();
            selector.setNoSelectionText("Select Social Site");
            ArrayList<SocialSite> options = new ArrayList<SocialSite>(Arrays.asList(SocialSite.values()));
            options.removeAll(usedSites);
            selector.setOptions(options);
            int optSize = options.size();
            if (optSize > 0) {
                selector.addValueChangeHandler(new ValueChangeHandler<SocialSite>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<SocialSite> event) {
                        SocialLink link = EntityFactory.create(SocialLink.class);
                        link.socialSite().setValue(event.getValue());
                        SocialLinkFolder.super.addItem(link);
                        hide();
                    }
                });
                panel.add(selector);
                selector.getWidget().getEditor().setVisibleItemCount(optSize + 1);
            } else {
                panel.add(new Label(i18n.tr("Sorry, no more items to choose from.")));
            }
            setBody(panel);
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }
    }

    class SocialLinkEditor extends CEntityDecoratableForm<SocialLink> {

        public SocialLinkEditor() {
            super(SocialLink.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            CLabel site = new CLabel();
            site.setEditable(false);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().socialSite(), site), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().siteUrl()), 35).build());
            get(proto().siteUrl()).addValueValidator(new EditableValueValidator<String>() {
                @Override
                public ValidationError isValid(CComponent<String, ?> component, String url) {
                    if (url == null || url.length() == 0) {
                        return new ValidationError(component, i18n.tr("URL should not be empty"));
                    } else if (!ValidationUtils.isCorrectUrl(url)) {
                        return new ValidationError(component, i18n.tr("Please use proper URL format"));
                    }
                    return null;
                }
            });
            return main;
        }
    }
}