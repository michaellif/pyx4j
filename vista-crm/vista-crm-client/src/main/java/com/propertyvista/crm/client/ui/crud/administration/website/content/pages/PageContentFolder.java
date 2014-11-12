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
package com.propertyvista.crm.client.ui.crud.administration.website.content.pages;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

class PageContentFolder extends VistaBoxFolder<PageContent> {

    private static final I18n i18n = I18n.get(PageContentFolder.class);

    private final CForm<PageDescriptor> parent;

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PageContentFolder(CForm<PageDescriptor> parent) {
        super(PageContent.class, parent.isEditable());
        this.parent = parent;
        this.addValueChangeHandler(new ValueChangeHandler<IList<PageContent>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PageContent>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PageContent item : getValue()) {
            usedLocales.add(item.locale());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateUsedLocales();
    }

    @Override
    protected void addItem() {
        new AvailableLocaleSelectorDialog(usedLocales) {
            @Override
            public boolean onClickOk() {
                AvailableLocale locale = getSelectedLocale();
                if (locale != null) {
                    PageContent item = EntityFactory.create(PageContent.class);
                    item.locale().set(locale);
                    PageContentFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<PageContent> createItemForm(IObject<?> member) {
        return new PageContentEditor();
    }

    @Override
    public void addValidations() {
        super.addValidations();
        this.addComponentValidator(new AbstractComponentValidator<IList<PageContent>>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() == null) {
                    return null;
                }
                return !getCComponent().getValue().isEmpty() ? null
                        : new BasicValidationError(getCComponent(), i18n.tr("At least one content item is necessary"));
            }
        });
    }

    @Override
    protected void createNewEntity(AsyncCallback<PageContent> callback) {
        PageContent newEntity = EntityFactory.create(PageContent.class);

        newEntity.descriptor().set(parent.getValue());

        callback.onSuccess(newEntity);
    }

    class PageContentEditor extends CForm<PageContent> {

        public PageContentEditor() {
            super(PageContent.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            formPanel.append(Location.Left, proto().locale(), locale).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto()._caption().caption()).decorate();
            formPanel.append(Location.Left, proto()._caption().secondaryCaption()).decorate();

            CRichTextArea editor = new CRichTextArea();
            editor.setImageProvider(new SiteImageResourceProvider());
            formPanel.append(Location.Dual, proto().content(), editor).decorate();

            // TODO
            // main.setWidget(++row, 0, inject(proto().image(), new CFileUploader(), new FormDecoratorBuilder( 60).build()));
            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            boolean staticPage = getValue().descriptor().type().getValue() == PageDescriptor.Type.staticContent;
            get(proto().content()).setVisible(staticPage);
            get(proto()._caption().secondaryCaption()).setVisible(staticPage);
        }
    }
}