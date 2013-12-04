/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.ILocalizedEntity;
import com.propertyvista.domain.site.AvailableLocale;

public abstract class LocalizedContentFolderBase<E extends ILocalizedEntity> extends VistaBoxFolder<E> {
    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    private final Class<E> entityClass;

    public LocalizedContentFolderBase(Class<E> entityClass, boolean editable) {
        super(entityClass, editable);
        this.entityClass = entityClass;
        this.addValueChangeHandler(new ValueChangeHandler<IList<E>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (E content : getValue()) {
            usedLocales.add(content.locale());
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
                    E content = EntityFactory.create(entityClass);
                    content.locale().set(locale);
                    LocalizedContentFolderBase.super.addItem(content);
                }
                return true;
            }
        }.show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ILocalizedEntity) {
            return new LocalizedContentEditor();
        }
        return super.create(member);
    }

    public abstract IsWidget createEditorContent(CEntityForm<E> editor);

    class LocalizedContentEditor extends CEntityForm<E> {

        public LocalizedContentEditor() {
            super(entityClass);
        }

        @Override
        public IsWidget createContent() {
            return createEditorContent(this);
        }
    }
}