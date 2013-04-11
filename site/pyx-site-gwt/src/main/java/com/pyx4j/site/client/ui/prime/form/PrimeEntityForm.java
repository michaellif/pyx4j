/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-07-29
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime.form;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CTabbedEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

public abstract class PrimeEntityForm<E extends IEntity> extends CTabbedEntityForm<E> {

    private IForm<? extends IEntity> view;

    public PrimeEntityForm(Class<E> rootClass, IForm<? extends IEntity> view) {
        this(rootClass, null, view);
    }

    public PrimeEntityForm(Class<E> rootClass, IEditableComponentFactory factory, IForm<? extends IEntity> view) {
        super(rootClass, factory);
        this.view = view;

        if (view instanceof IViewer) {
            setEditable(false);
            setViewable(true);
        }

    }

    public IForm<? extends IEntity> getParentView() {
        assert (view != null);
        return view;
    }

}
