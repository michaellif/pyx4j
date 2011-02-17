/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.CrudDebugId;

public class WizardStepViewImpl<E extends IEntity, T extends WizardStepPresenter<E>> extends FlowPanel implements WizardStepView<E, T> {

    private static I18n i18n = I18nFactory.getI18n(WizardStepViewImpl.class);

    private T presenter;

    private final CEntityForm<E> form;

    public WizardStepViewImpl(final CEntityForm<E> form) {
        this.form = form;
        add(form);

        Button saveButton = new Button(i18n.tr("Save and Continue"));
        saveButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(form.getValue());
            }

        });

        HTML separator = new HTML();
        separator.getElement().getStyle().setProperty("border", "1px dotted black");
        separator.getElement().getStyle().setProperty("margin", "1em 0em 0em 0em");
        add(separator);

        saveButton.getElement().getStyle().setProperty("margin", "1em 1em 1em 0em");
        //        saveButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(saveButton);

        getElement().getStyle().setMarginLeft(5, Unit.PCT);
        getElement().getStyle().setMarginRight(5, Unit.PCT);
        //TODO setWidth("60%");
    }

    @Override
    public void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E entity) {
        form.populate(entity);
    }

}
