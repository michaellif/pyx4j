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
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class AbstractEditorView<E extends IEntity> extends AbstractFormView<E> implements IEditorView<E> {

    protected static final I18n i18n = I18n.get(AbstractEditorView.class);

    public AbstractEditorView() {
        super();
    }

    @Override
    public void setEditable(boolean flag) {
        getForm().setViewable(!flag);
    }

    @Override
    public IEditorPresenter<E> getPresenter() {
        return (IEditorPresenter<E>) super.getPresenter();
    }

    @Override
    public void reset() {
        super.reset();
        setEditable(false);
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            MessageDialog.error(i18n.tr("Error"), caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return getForm().isDirty();
    }

    @Override
    public E getValue() {
        return getForm().getValue();
    }
}
