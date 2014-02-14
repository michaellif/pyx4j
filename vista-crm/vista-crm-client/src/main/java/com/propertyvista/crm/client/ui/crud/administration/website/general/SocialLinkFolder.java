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
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IFormat;
import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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
    public CComponent<?> create(IObject<?> member) {
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
            selector.setFormat(new IFormat<SocialLink.SocialSite>() {
                @Override
                public SocialSite parse(String string) throws ParseException {
                    return null;
                }

                @Override
                public String format(SocialSite value) {
                    return value != null ? value.toString() : i18n.tr("Select Social Site");
                }
            });
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
                        hide(false);
                    }
                });
                panel.add(selector);
                selector.getWidget().getEditor().setVisibleItemCount(optSize + 1);
                selector.getWidget().getEditor().setHeight("100px");
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

    class SocialLinkEditor extends CEntityForm<SocialLink> {

        public SocialLinkEditor() {
            super(SocialLink.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CLabel<String> site = new CLabel<String>();
            site.setEditable(false);
            main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().socialSite(), site), 10, true).build());
            main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().siteUrl()), 35, true).build());
            get(proto().siteUrl()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public FieldValidationError isValid() {
                    if (getComponent().getValue() == null || getComponent().getValue().length() == 0) {
                        return new FieldValidationError(getComponent(), i18n.tr("URL should not be empty"));
                    } else if (!ValidationUtils.isCorrectUrl(getComponent().getValue())) {
                        return new FieldValidationError(getComponent(), i18n.tr("Please use proper URL format"));
                    }
                    return null;
                }
            });
            return main;
        }
    }
}