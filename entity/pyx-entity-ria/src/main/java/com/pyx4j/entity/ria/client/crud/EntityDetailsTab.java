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
 * Created on Nov 19, 2010
 * @author Misha
 * @version $Id: code-templates.xml 4670 2010-01-10 07:33:42Z vlads $
 */
package com.pyx4j.entity.ria.client.crud;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.shared.IEntity;

public class EntityDetailsTab<E extends IEntity> {

    private final String title;

    private final CEntityForm<E> form;

    EntityDetailsTab(String title, CEntityForm<E> form) {
        this.title = title;
        this.form = form;
    }

    public String getTitle() {
        return title;
    }

    public CEntityForm<E> getForm() {
        return form;
    }

    public Widget initNativeComponent() {
        return (Widget) form.initNativeComponent();
    }

}