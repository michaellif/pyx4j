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
package com.pyx4j.site.client.ui.crud;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

public abstract class CrudEntityForm<E extends IEntity> extends CEntityEditor<E> {

    private static final I18n i18n = I18n.get(CrudEntityForm.class);

    private IFormView<? extends IEntity> parentView;

    public CrudEntityForm(Class<E> rootClass) {
        super(rootClass);
    }

    public CrudEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
    }

    public void setParentView(IFormView<? extends IEntity> parentView) {
        this.parentView = parentView;
    }

    public IFormView<? extends IEntity> getParentView() {
        assert (parentView != null);
        return parentView;
    }

    // default active tab mechanics:
    public void setActiveTab(int index) {
    }

    public int getActiveTab() {
        return -1;
    }
}
