/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;

public class BasicViewImpl<E extends IEntity> extends FlowPanel implements View<E> {

    protected static final I18n i18n = I18n.get(BasicViewImpl.class);

    protected final UserMessagePanel messagePanel;

    protected final SimplePanel formHolder;

    protected CEntityForm<E> form;

    protected Presenter<E> presenter;

    protected final Button submitButton;

    protected final CHyperlink cancel;

    public BasicViewImpl() {
        add(messagePanel = new UserMessagePanel());

        add(formHolder = new SimplePanel());

        submitButton = new Button(i18n.tr("Save"));
        submitButton.getElement().getStyle().setMargin(10, Unit.PX);
        submitButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true, false));
                } else {
                    presenter.save(form.getValue());
                }
            }
        });
        add(DecorationUtils.inline(submitButton));

        cancel = new CHyperlink(new Command() {
            @Override
            public void execute() {
                presenter.cancel();
            }
        });
        cancel.setValue(i18n.tr("Cancel"));
        cancel.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        cancel.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(cancel);
    }

    public BasicViewImpl(CEntityForm<E> viewForm) {
        this();
        setForm(viewForm);
    }

    protected void setForm(CEntityForm<E> viewForm) {
        form = viewForm;
        form.initContent();
        formHolder.setWidget(form.asWidget());
    }

    @Override
    public void setPresenter(Presenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E value) {
        messagePanel.clearMessage();

        form.reset();
        form.populate(value);
    }

    @Override
    public void showError(String msg) {
        messagePanel.setMessage(msg, UserMessageType.ERROR);
    }

    @Override
    public void showNote(String msg) {
        messagePanel.setMessage(msg, UserMessageType.INFO);
    }
}
