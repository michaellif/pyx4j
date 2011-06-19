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
package com.pyx4j.site.client.ui.crud;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;

public class EditorViewImplBase<E extends IEntity> extends ViewImplBase<E> implements IEditorView<E> {

    private static I18n i18n = I18nFactory.getI18n(EditorViewImplBase.class);

    protected Presenter presenter;

    public EditorViewImplBase() {
        super();
    }

    public EditorViewImplBase(CEntityForm<E> form) {
        super(form);
    }

    public EditorViewImplBase(Widget header, double size) {
        super(header, size);
    }

    public EditorViewImplBase(Widget header, double size, CEntityForm<E> form) {
        super(header, size, form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEditMode(EditMode mode) {
    }

    @Override
    public E getValue() {
        return form.getValue();
    }

    @Override
    public void onApplySuccess() {
    }

    @Override
    public void onSaveSuccess() {
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        return false;
    }
}
