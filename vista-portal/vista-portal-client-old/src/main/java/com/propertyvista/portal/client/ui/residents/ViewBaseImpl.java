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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.site.Notification.NotificationType;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;

public class ViewBaseImpl<E extends IEntity> extends FlowPanel implements ViewBase<E> {

    protected static final I18n i18n = I18n.get(ViewBaseImpl.class);

    private final UserMessagePanel messagePanel;

    private final SimplePanel formHolder;

    private final FlowPanel footer;

    private CEntityForm<E> form;

    private Presenter<E> presenter;

    public ViewBaseImpl() {
        this(null);
    }

    public ViewBaseImpl(CEntityForm<E> form) {
        add(messagePanel = new UserMessagePanel());
        add(formHolder = new SimplePanel());
        add(footer = new FlowPanel());

        if (form != null) {
            setForm(form);
        }
    }

    protected void setForm(CEntityForm<E> theForm) {
        form = theForm;

        // set form role-behavior:
        if (this instanceof Edit) {
            form.setEditable(true);
            form.setViewable(false);
        } else {
            form.setEditable(false);
            form.setViewable(true);
        }

        form.initContent();
        formHolder.setWidget(form.asWidget());
    }

    protected void addToFooter(IsWidget widget) {
        footer.add(widget);
    }

    protected void removeFromFooter(IsWidget widget) {
        footer.remove(widget);
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
        messagePanel.setMessage(msg, NotificationType.ERROR);
    }

    @Override
    public void showNote(String msg) {
        messagePanel.setMessage(msg, NotificationType.INFO);
    }

    public Presenter<E> getPresenter() {
        return presenter;
    }

    public CEntityForm<E> getForm() {
        return form;
    }
}
