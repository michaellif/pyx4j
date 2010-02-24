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

import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.client.EntityCSSClass;

public abstract class AbstractEntitySearchCriteriaPanel<E extends IEntity> extends SimplePanel {

    public AbstractEntitySearchCriteriaPanel() {
        super();
        setStyleName(EntityCSSClass.pyx4j_Entity_EntitySearchCriteria.name());
    }

    public abstract EntityCriteria<E> getEntityCriteria();

    public abstract void populateEntityCriteria(EntityCriteria<E> criteria);

    @Override
    public void setWidget(Widget w) {
        super.setWidget(w);
        w.setWidth("100%");
    }

}
