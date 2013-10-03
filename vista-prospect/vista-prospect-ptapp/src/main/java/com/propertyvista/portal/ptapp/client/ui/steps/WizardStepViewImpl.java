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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.events.NotificationEvent;

import com.propertyvista.misc.VistaTODO;

public class WizardStepViewImpl<E extends IEntity, T extends WizardStepPresenter<E>> extends FlowPanel implements WizardStepView<E, T> {

    protected static final I18n i18n = I18n.get(WizardStepViewImpl.class);

    private T presenter;

    private final CEntityForm<E> form;

    private final Button actionButton;

    public WizardStepViewImpl(final CEntityForm<E> form) {
        this.form = form;
        form.initContent();
        add(form);

        actionButton = new Button(actionName());
        actionButton.ensureDebugId(CrudDebugId.Crud_Save.debugId());
        actionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onAction();
            }
        });

        HTML separator = new HTML();
        separator.getElement().getStyle().setProperty("borderTop", "1px dotted black");
        separator.getElement().getStyle().setProperty("margin", "1em 0em 0em 0em");
        add(separator);

        actionButton.getElement().getStyle().setProperty("margin", "1em 1em 1em 0em");
        add(actionButton);

        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);
    }

    protected void onAction() {
        AppSite.getEventBus().fireEvent(new NotificationEvent(null, null, null));
        if (!form.isValid()) {
            form.setUnconditionalValidationErrorRendering(true);
            Window.scrollTo(0, 0);
            throw new UserRuntimeException(form.getValidationResults().getValidationMessage(true, false));
        }

        doAction();
        Window.scrollTo(0, 0);
    }

    protected String actionName() {
        return i18n.tr("Save and Continue");
    }

    protected void doAction() {
        saveAction();
    }

    protected void saveAction() {
        presenter.save(getValue());
    }

    protected void nextAction() {
        presenter.next(getValue());
    }

    @Override
    public void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    @Override
    public T getPresenter() {
        return presenter;
    }

    @Override
    public void populate(E entity) {
        form.populate(entity);
    }

    @Override
    public E getValue() {
        return form.getValue();
    }

    protected CEntityForm<E> getForm() {
        return form;
    }

    protected void setActionButtonVisible(boolean visible) {
        // this was created to create wizard step with buttons inside the form itself.
        if (VistaTODO.enableWelcomeWizardDemoMode) {

            actionButton.setVisible(visible);
        }
    }
}
