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
 * Created on May 25, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.pyx4j.entity.client.ui.crud.CEntityEditorForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CFormFolder;
import com.pyx4j.forms.client.ui.FormFactory;

public class CEntityFormFolder<E extends IEntity> extends CFormFolder<E> {

    private IObject<?>[][] members;

    private final CEntityEditorForm<E> metaModel;

    public CEntityFormFolder(Class<E> clazz, EntityFormFactory<E> factory) {
        super(factory);
        this.metaModel = CEntityEditorForm.create(clazz);
    }

    public E meta() {
        return metaModel.meta();
    }

    public CEntityEditorForm<E> getForm() {
        return metaModel;
    }

    public CForm OLDcreateForm() {
        CComponent<?>[][] components = new CComponent<?>[members.length][members[0].length];

        CEntityEditorForm<E> model = CEntityEditorForm.create((Class<E>) metaModel.meta().getObjectClass());

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[0].length; j++) {
                IObject<?> member = members[i][j];
                if (member == null) {
                    components[i][j] = null;
                } else if (model.contains(member)) {
                    components[i][j] = model.get(member);
                } else {
                    components[i][j] = model.create(member);
                }
                // Hack
                if (components[i][j] instanceof CEntityFormFolder) {
                    ((CEntityFormFolder) components[i][j]).setFormMembers(((CEntityFormFolder) metaModel.get(member)).members);
                }
            }
        }

        CForm form = new CForm(this);

        form.setComponents(components);

        return form;
    }

    public void setFormMembers(IObject<?>[][] members) {
        this.members = members;

    }
}
