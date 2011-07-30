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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.EditorViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public class CrmEditorViewImplBase<E extends IEntity> extends EditorViewImplBase<E> {

    protected final CrmHeaderDecorator header;

    protected String defaultCaption;

    protected Button btnApply;

    protected Button btnSave;

    protected EditMode mode;

    public CrmEditorViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();
        addNorth(header = new CrmHeaderDecorator(defaultCaption), VistaCrmTheme.defaultHeaderHeight);
        addSouth(createButtons(), VistaCrmTheme.defaultFooterHeight);
    }

    public CrmEditorViewImplBase(Class<? extends CrudAppPlace> placeClass, CrudEntityForm<E> form) {
        this(placeClass);
        form.initialize();
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
        header.setCaption(defaultCaption + " " + value.getStringView());
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

    private Widget createButtons() {
        HorizontalPanel buttons = new HorizontalPanel();

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

        AnchorButton btnCancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        });

        enableButtons(false);

        btnApply.addStyleName(btnSave.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.SaveButton);
        btnApply.setWidth("7em");

        btnSave.addStyleName(btnSave.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.SaveButton);
        btnSave.setWidth("7em");
        btnCancel.setWidth("5em");

        buttons.add(btnApply);
        buttons.add(btnSave);
        buttons.add(btnCancel);

        buttons.setCellHorizontalAlignment(btnCancel, HasHorizontalAlignment.ALIGN_CENTER);
        buttons.setCellVerticalAlignment(btnCancel, HasVerticalAlignment.ALIGN_MIDDLE);
        buttons.setSpacing(10);

        SimplePanel wrap = new SimplePanel();
        wrap.getElement().getStyle().setProperty("borderTop", "1px solid #bbb");
        buttons.getElement().getStyle().setPosition(Position.ABSOLUTE);
        buttons.getElement().getStyle().setRight(0, Unit.EM);
        wrap.setWidget(buttons);
        wrap.setWidth("100%");
        return wrap;
    }

    protected void enableButtons(boolean enable) {
//        btnApply.setEnabled(enable);
        btnSave.setEnabled(enable);
    }

}
