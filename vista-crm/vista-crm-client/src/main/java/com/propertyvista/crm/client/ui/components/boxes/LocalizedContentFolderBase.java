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
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.ILocalizedEntity;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.i18n.CompiledLocale;

public abstract class LocalizedContentFolderBase<E extends ILocalizedEntity> extends VistaBoxFolder<E> {

    private final Set<CompiledLocale> usedLocales = new HashSet<>();

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
            usedLocales.add(content.locale().getValue());
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
                    content.locale().set(locale.lang());
                    LocalizedContentFolderBase.super.addItem(content);
                }
                return true;
            }
        }.show();
    }
}