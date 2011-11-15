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
package com.propertyvista.admin.client.ui.crud;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.admin.client.themes.VistaAdminTheme;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;

public class AdminViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    protected final String defaultCaption;

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(new AdminHeaderDecorator(), new Toolbar(), VistaAdminTheme.defaultHeaderHeight);

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        ((AdminHeaderDecorator) getHeader()).setCaption(defaultCaption);

        Button btnEdit = new Button(i18n.tr("Edit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.edit();
            }
        });
        btnEdit.addStyleName(btnEdit.getStylePrimaryName() + VistaAdminTheme.StyleSuffixEx.EditButton);

        addToolbarItem(btnEdit);

    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, AdminEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        ((AdminHeaderDecorator) getHeader()).setCaption(defaultCaption + " " + value.getStringView());
    }

}
