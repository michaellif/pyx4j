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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;

public class ViewImpl<E extends IEntity> extends FlowPanel implements View<E> {

    protected static final I18n i18n = I18n.get(ViewImpl.class);

    private final UserMessagePanel messagePanel;

    private final SimplePanel formHolder;

    private CEntityForm<E> form;

    private final Button submit;

    private final Anchor cancel;

    private Presenter<E> presenter;

    public ViewImpl() {
        this(null);
    }

    public ViewImpl(CEntityForm<E> viewForm) {
        this(viewForm, false);
    }

    public ViewImpl(CEntityForm<E> viewForm, boolean readOnly) {
        add(messagePanel = new UserMessagePanel());

        add(formHolder = new SimplePanel());

        submit = new Button(i18n.tr("Submit"));
        submit.getElement().getStyle().setMargin(10, Unit.PX);
        submit.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        submit.setCommand(new Command() {
            @Override
            public void execute() {
                onSubmit();
            }
        });
        if (!readOnly) {
            add(DecorationUtils.inline(submit));
        }

        cancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                presenter.cancel();
            }
        });
        cancel.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        cancel.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(cancel);

        if (viewForm != null) {
            setForm(viewForm);
        }
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

    protected void onSubmit() {
        if (!form.isValid()) {
            Window.scrollTo(0, 0);
            showError(form.getValidationResults().getValidationMessage(true, false));
        } else {
            presenter.save(form.getValue());
        }
    }

    public Presenter<E> getPresenter() {
        return presenter;
    }

    public CEntityForm<E> getForm() {
        return form;
    }

    protected Button getSubmit() {
        return submit;
    }

    protected Anchor getCancel() {
        return cancel;
    }

}
