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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

public class AdminViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static final I18n i18n = I18n.get(AdminViewerViewImplBase.class);

    protected final String defaultCaption;

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this(placeClass, false);
    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, boolean viewOnly) {
        super();

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        setCaption(defaultCaption);

        if (!viewOnly) {
            Button btnEdit = new Button(i18n.tr("Edit"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    getPresenter().edit();
                }
            });

            addHeaderToolbarItem(btnEdit);
        }

    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, AdminEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, AdminEntityForm<E> form, boolean viewOnly) {
        this(placeClass, viewOnly);
        setForm(form);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        setCaption(defaultCaption + " " + value.getStringView());
    }

}
