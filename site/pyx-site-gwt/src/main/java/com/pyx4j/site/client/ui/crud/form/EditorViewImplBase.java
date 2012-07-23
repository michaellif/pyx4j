/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.form;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class EditorViewImplBase<E extends IEntity> extends FormViewImplBase<E> implements IEditorView<E> {

    private static final I18n i18n = I18n.get(EditorViewImplBase.class);

    private IEditorView.Presenter presenter;

    public EditorViewImplBase() {
        super();
    }

    @Override
    public void setPresenter(IEditorView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IEditorView.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setEditMode(EditMode mode) {
    }

    @Override
    public E getValue() {
        return getForm().getValue();
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            showErrorDialog(caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return getForm().isDirty();
    }

    protected void showErrorDialog(String message) {
        MessageDialog.error(i18n.tr("Error"), message);
    }

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), getForm().getValidationResults().getMessagesText(true, true));
    }
}