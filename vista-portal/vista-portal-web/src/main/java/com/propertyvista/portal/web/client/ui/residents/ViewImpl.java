/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-24
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;

public class ViewImpl<E extends IEntity> extends ViewBaseImpl<E> implements View<E> {

    private final Anchor back;

    private final Button edit;

    public ViewImpl() {
        this(null);

    }

    public ViewImpl(CEntityForm<E> form) {
        this(form, false, false);
    }

    public ViewImpl(boolean noEdit, boolean noBack) {
        this(null, noEdit, noBack);
    }

    public ViewImpl(CEntityForm<E> form, boolean noEdit, boolean noBack) {
        super(form);

        edit = new Button(i18n.tr("Edit"));
        edit.getElement().getStyle().setMargin(10, Unit.PX);
        edit.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        edit.setCommand(new Command() {
            @Override
            public void execute() {
                ((View.Presenter<E>) getPresenter()).edit(getForm().getValue().id().getValue());
            }
        });
        if (!noEdit) {
            addToFooter(DecorationUtils.inline(edit));
        }

        back = new Anchor(i18n.tr("Back"), new Command() {
            @Override
            public void execute() {
                ((View.Presenter<E>) getPresenter()).back();
            }
        });
        back.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        back.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        if (!noBack) {
            addToFooter(back);
        }
    }

    protected Anchor getBack() {
        return back;
    }

    protected Button getEdit() {
        return edit;
    }
}
