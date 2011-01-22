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
package com.pyx4j.entity.ria.client.crud;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.CriteriaFormFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public abstract class EntitySearchCriteriaPart<E extends IEntity> {

    private CEntityForm<E> form;

    public EntitySearchCriteriaPart(Class<E> clazz) {

    }

    protected void createForm(CriteriaFormFactory<E> formFactory) {
        form = formFactory.createForm();
        form.populate(null);
        form.setAllignment(LabelAlignment.TOP);
    }

    public Widget initNativeComponent() {
        return form.asWidget();
    }

}
