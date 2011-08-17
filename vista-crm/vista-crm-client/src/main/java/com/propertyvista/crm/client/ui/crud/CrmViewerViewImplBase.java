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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmActionsBarDecorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public class CrmViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    protected final CrmHeaderDecorator header;

    protected final String defaultCaption;

    protected final HorizontalPanel actionsPanel;

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();

        actionsPanel = new HorizontalPanel();
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...
        actionsPanel.setSpacing(4);

        addNorth(header = new CrmHeaderDecorator(defaultCaption), VistaCrmTheme.defaultHeaderHeight);
        addNorth(new CrmActionsBarDecorator(null, fillActionsPanel()), VistaCrmTheme.defaultActionBarHeight);

        header.setHeight("100%"); // fill all that defaultHeaderHeight!..
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form) {
        this(placeClass);
        form.initialize();
        setForm(form);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        header.setCaption(defaultCaption + " " + value.getStringView());
    }

    private Widget fillActionsPanel() {
        Button btnEdit = new Button(i18n.tr("Edit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.edit();
            }
        });
        btnEdit.addStyleName(btnEdit.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.EditButton);
        addActionButton(btnEdit);
        return actionsPanel;
    }

    protected void addActionButton(Widget action) {
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
//        action.getElement().getStyle().setMarginRight(5, Unit.PX);
    }

    protected CrmEntityForm<E> getForm() {
        return (CrmEntityForm<E>) form;
    }
}
