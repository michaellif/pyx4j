/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.boxes.VersionSelectorDialog;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;

public class CrmViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static final I18n i18n = I18n.get(CrmViewerViewImplBase.class);

    protected final String defaultCaption;

    private final Button editButton;

    private Button selectVersion;

    private Button finalizeButton;

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this(placeClass, false);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, boolean viewOnly) {
        super(new CrmTitleBar(), null, CrmTheme.defaultHeaderHeight);

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        ((CrmTitleBar) getHeader()).setCaption(defaultCaption);

        if (!viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.edit();
                }
            });
            editButton.addStyleName(editButton.getStylePrimaryName() + CrmTheme.StyleSuffixEx.EditButton);

            addToolbarItem(editButton);
        } else {
            editButton = null;
        }
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form, boolean viewOnly) {
        this(placeClass, viewOnly);
        setForm(form);
    }

    protected <V extends IVersionData<?>> void enableVersioning(final Class<V> entityVersionClass,
            final Class<? extends AbstractListService<V>> entityVersionServiceClass) {

        selectVersion = new Button(i18n.tr("Select Version"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new VersionSelectorDialog<V>(entityVersionClass, form.getValue().getPrimaryKey()) {
                    @Override
                    public boolean onClickOk() {
                        presenter.view(getSelectedVersionId());
                        return true;
                    }

                    @Override
                    protected AbstractListService<V> getSelectService() {
                        return GWT.<AbstractListService<V>> create(entityVersionServiceClass);
                    }
                }.show();
            }
        });
        addToolbarItem(selectVersion.asWidget());

        finalizeButton = new Button(i18n.tr("Finalize"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.finalize();
            }
        });
        addToolbarItem(finalizeButton.asWidget());

        if (editButton != null) {
            editButton.setCaption(i18n.tr("Edit Draft"));
        }
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        ((CrmTitleBar) getHeader()).setCaption(defaultCaption + " " + value.getStringView());
        if (editButton != null) {
            editButton.setEnabled(super.getPresenter().canEdit());
        }
    }
}
