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
package com.propertyvista.portal.ptapp.client.ui.steps;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.events.UserMessageEvent;

public class WizardStepViewImpl<E extends IEntity, T extends WizardStepPresenter<E>> extends FlowPanel implements WizardStepView<E, T> {

    private static I18n i18n = I18n.get(WizardStepViewImpl.class);

    private T presenter;

    private final CEntityEditor<E> form;

    public WizardStepViewImpl(final CEntityEditor<E> form) {
        this.form = form;
        form.initContent();
        add(form);

        Button saveButton = new Button(actionName());
        saveButton.ensureDebugId(CrudDebugId.Crud_Save.debugId());
        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new UserMessageEvent(null, null, null));
                form.setVisited(true);
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.save(getValue());
                Window.scrollTo(0, 0);
            }
        });

        HTML separator = new HTML();
        separator.getElement().getStyle().setProperty("borderTop", "1px dotted black");
        separator.getElement().getStyle().setProperty("margin", "1em 0em 0em 0em");
        add(separator);

        saveButton.getElement().getStyle().setProperty("margin", "1em 1em 1em 0em");
        add(saveButton);

        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);
    }

    protected String actionName() {
        return i18n.tr("Save and Continue");
    }

    @Override
    public void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    protected T getPresenter() {
        return presenter;
    }

    @Override
    public void populate(E entity) {
        form.populate(entity);
    }

    protected E getValue() {
        return form.getValue();
    }

    protected CEntityEditor<E> getForm() {
        return form;
    }
}
