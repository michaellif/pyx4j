/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.EditorViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.admin.client.themes.VistaAdminTheme;
import com.propertyvista.admin.client.ui.components.AnchorButton;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;

public class AdminEditorViewImplBase<E extends IEntity> extends EditorViewImplBase<E> {

    protected String defaultCaption;

    protected Button btnApply;

    protected Button btnSave;

    protected EditMode mode;

    public AdminEditorViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(new AdminHeaderDecorator(), new Toolbar(), VistaAdminTheme.defaultHeaderHeight);

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");

        ((AdminHeaderDecorator) getHeader()).setCaption(defaultCaption);

        Toolbar footer = ((Toolbar) getFooter());

        btnApply = new Button(i18n.tr("Apply"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.apply();
            }
        });
        footer.addItem(btnApply);

        btnSave = new Button(i18n.tr("Save"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.save();
            }
        });
        footer.addItem(btnSave);

        AnchorButton btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        });
        footer.addItem(btnCancel);

        enableButtons(false);

    }

    public AdminEditorViewImplBase(Class<? extends CrudAppPlace> placeClass, CrudEntityForm<E> form) {
        this(placeClass);
        form.initContent();
        setForm(form);
    }

    @Override
    protected void setForm(CrudEntityForm<? extends E> form) {
        super.setForm(form);

        this.form.addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                enableButtons(true);
            }
        });
    }

    @Override
    public void populate(E value) {
        enableButtons(false);
        ((AdminHeaderDecorator) getHeader()).setCaption(defaultCaption + " " + value.getStringView());
        if (EditMode.newItem.equals(mode)) {
            form.setActiveTab(0);
        }

        super.populate(value);
    }

    @Override
    public void setEditMode(EditMode mode) {
        this.mode = mode;
    }

    @Override
    public void onSaveSuccess() {
        enableButtons(false);
    }

    @Override
    public void onApplySuccess() {
        enableButtons(false);
    }

    protected void enableButtons(boolean enable) {
//        
// TODO Currently buttons are enabled always - more precise form dirty-state mechanics should be implemented!..
//        
//        btnApply.setEnabled(enable);
//        btnSave.setEnabled(enable);
    }
}