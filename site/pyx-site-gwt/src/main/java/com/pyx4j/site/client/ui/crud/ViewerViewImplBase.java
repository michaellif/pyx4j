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
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;

public class ViewerViewImplBase<E extends IEntity> extends ViewImplBase<E> implements IViewerView<E> {

    protected Presenter presenter;

    public ViewerViewImplBase() {
        super();
    }

    public ViewerViewImplBase(CrudEntityForm<E> form) {
        super(form);
    }

    public ViewerViewImplBase(Widget header, double size) {
        super(header, size);
    }

    public ViewerViewImplBase(Widget header, double size, CrudEntityForm<E> form) {
        super(header, size, form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }
}
