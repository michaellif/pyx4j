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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.themes.VistaAdminTheme;
import com.propertyvista.admin.client.ui.components.AnchorButton;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;

public class AdminViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static I18n i18n = I18nFactory.getI18n(AdminViewerViewImplBase.class);

    protected final Class<? extends CrudAppPlace> placeClass;

    protected final AdminHeaderDecorator header;

    protected final String defaultCaption;

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this.placeClass = placeClass;
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();
        addNorth(header = new AdminHeaderDecorator(defaultCaption, createActionsPanel()), 3);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        header.setCaption(defaultCaption + " " + value.getStringView());
    }

    private Widget createActionsPanel() {
        HorizontalPanel buttons = new HorizontalPanel();
        AnchorButton btnEdit = new AnchorButton(i18n.tr("Edit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.edit(placeClass);
            }
        });
        btnEdit.addStyleName(btnEdit.getStylePrimaryName() + VistaAdminTheme.StyleSuffixEx.EditButton);
        buttons.add(btnEdit);
        return buttons;
    }
}
