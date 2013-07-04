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
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.web.client.ui.EntityViewImpl;

public class EditImpl<E extends IEntity> extends EntityViewImpl<E> implements Edit<E> {

    private final Anchor cancel;

    private final Button submit;

    public EditImpl() {
        this(null);
    }

    public EditImpl(CEntityForm<E> form) {
        super(form);

        submit = new Button(i18n.tr("Submit"));
        submit.getElement().getStyle().setMargin(10, Unit.PX);
        submit.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        submit.setCommand(new Command() {
            @Override
            public void execute() {
                onSubmit();
            }
        });
        addToFooter(DecorationUtils.inline(submit));

        cancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                ((Edit.Presenter<E>) getPresenter()).cancel();
            }
        });
        cancel.asWidget().getElement().getStyle().setMargin(10, Unit.PX);
        cancel.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        addToFooter(cancel);
    }

    protected Anchor getCancel() {
        return cancel;
    }

    protected Button getSubmit() {
        return submit;
    }

    protected void onSubmit() {
        if (!getForm().isValid()) {
            Window.scrollTo(0, 0);
            // TODO Show validation error in dialog
        } else {
            ((Edit.Presenter<E>) getPresenter()).save(getForm().getValue());
        }
    }
}
