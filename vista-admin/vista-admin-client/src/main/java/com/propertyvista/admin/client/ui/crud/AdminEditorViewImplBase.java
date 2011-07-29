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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import com.propertyvista.admin.client.themes.VistaAdminTheme;
import com.propertyvista.admin.client.ui.components.AnchorButton;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;

public class AdminEditorViewImplBase<E extends IEntity> extends EditorViewImplBase<E> {

    private static I18n i18n = I18nFactory.getI18n(AdminEditorViewImplBase.class);

    protected final AdminHeaderDecorator header;

    protected final String defaultCaption;

    public AdminEditorViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();
        addNorth(header = new AdminHeaderDecorator(defaultCaption), 3);
        addSouth(createButtons(), 4);
    }

    public AdminEditorViewImplBase(Class<? extends CrudAppPlace> placeClass, CrudEntityForm<E> form) {
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();
        addNorth(header = new AdminHeaderDecorator(defaultCaption), 3);
        addSouth(createButtons(), 4);
        form.initialize();
        setForm(form);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        header.setCaption(defaultCaption + " " + value.getStringView());
    }

    private Widget createButtons() {
        HorizontalPanel buttons = new HorizontalPanel();

        Button btnSave = new Button(i18n.tr("Save"), new ClickHandler() {
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

        btnSave.addStyleName(btnSave.getStylePrimaryName() + VistaAdminTheme.StyleSuffixEx.SaveButton);

        btnSave.setWidth("7em");
        btnCancel.setWidth("5em");

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
}
