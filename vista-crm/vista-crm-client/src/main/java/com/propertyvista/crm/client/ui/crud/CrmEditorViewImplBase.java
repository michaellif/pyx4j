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
package com.propertyvista.crm.client.ui.crud;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.form.EditorViewImplBase;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;

public class CrmEditorViewImplBase<E extends IEntity> extends EditorViewImplBase<E> {

    protected String defaultCaption;

    protected Button btnApply;

    protected Button btnSave;

    protected EditMode mode;

    public CrmEditorViewImplBase(Class<? extends AppPlace> placeClass) {
        super(new CrmTitleBar(), new Toolbar(), VistaCrmTheme.defaultHeaderHeight);

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        ((CrmTitleBar) getHeader()).setCaption(defaultCaption);

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

    public CrmEditorViewImplBase(Class<? extends AppPlace> placeClass, CrmEntityForm<E> form) {
        this(placeClass);
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
        if (EditMode.newItem.equals(mode)) {
            ((CrmTitleBar) getHeader()).setCaption(defaultCaption + " " + i18n.tr("New Item..."));
            form.setActiveTab(0);
        } else {
            ((CrmTitleBar) getHeader()).setCaption(defaultCaption + " " + value.getStringView());
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

    private class YesNoBox extends OkCancelBox {

        private boolean yes;

        public YesNoBox() {
            super(i18n.tr("Please confirm"));

            okButton.setText(i18n.tr("Yes"));
            cancelButton.setText(i18n.tr("No"));

            setContent(createContent());
        }

        protected Widget createContent() {
            return new HTML(i18n.tr("Do you really want to cancel?"));
        }

        @Override
        protected boolean onOk() {
            return (yes = true);
        }

        public boolean getYes() {
            return yes;
        }
    }
}
