/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Feb 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.crud;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public abstract class AbstractEntityEditorPanel<E extends IEntity> extends SimplePanel {

    private final EntityEditorForm<E> form;

    public AbstractEntityEditorPanel(Class<E> clazz) {
        super();
        form = EntityEditorForm.create(clazz);

        setStyleName(EntityCSSClass.pyx4j_Entity_EntityEditor.name());
    }

    public E meta() {
        return form.meta();
    }

    @Override
    public void setWidget(Widget w) {
        super.setWidget(w);
    }

    public EntityEditorForm<E> getForm() {
        return form;
    }

    public void populateForm(E entity) {
        form.populate(entity);
    }

    public E getEntity() {
        return form.getValue();
    }

    public <T> CEditableComponent<T> get(IObject<T> member) {
        return form.get(member);
    }

    public Widget createFormWidget(LabelAlignment allignment, IObject<?>[][] members) {
        CComponent<?>[][] components = new CComponent<?>[members.length][members[0].length];
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[0].length; j++) {
                IObject<?> member = members[i][j];
                if (member == null) {
                    components[i][j] = null;
                } else if (form.contains(member)) {
                    components[i][j] = get(member);
                } else {
                    components[i][j] = form.create(member);
                }
            }
        }
        return CForm.createFormWidget(allignment, components);
    }
}
