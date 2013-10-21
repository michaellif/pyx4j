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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

class PageContentFolder extends VistaBoxFolder<PageContent> {

    private static final I18n i18n = I18n.get(PageContentFolder.class);

    private final CEntityForm<PageDescriptor> parent;

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PageContentFolder(CEntityForm<PageDescriptor> parent) {
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
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PageContent) {
            return new PageContentEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        this.addValueValidator(new EditableValueValidator<IList<PageContent>>() {
            @Override
            public ValidationError isValid(CComponent<IList<PageContent>> component, IList<PageContent> value) {
                if (value == null) {
                    return null;
                }
                return !value.isEmpty() ? null : new ValidationError(component, i18n.tr("At least one content item is necessary"));
            }
        });
    }

    @Override
    protected void createNewEntity(AsyncCallback<PageContent> callback) {
        PageContent newEntity = EntityFactory.create(PageContent.class);

        newEntity.descriptor().set(parent.getValue());

        callback.onSuccess(newEntity);
    }

    class PageContentEditor extends CEntityDecoratableForm<PageContent> {

        public PageContentEditor() {
            super(PageContent.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 10).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto()._caption().caption()), 20).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto()._caption().secondaryCaption()), 20).build());

            CRichTextArea editor = new CRichTextArea();
            editor.setImageProvider(new SiteImageResourceProvider());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().content(), editor), 60).build());

            // TODO
            // main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().image(), new CFileUploader()), 60).build());
            return main;
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