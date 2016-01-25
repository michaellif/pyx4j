/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 17, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datagrid;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;

public class CDataGrid<E extends IEntity> extends CContainer<CDataGrid<E>, IList<E>, IDataGridDecorator<E>> {

    public CDataGrid(Class<E> rowClass) {
    }

    @Override
    public Collection<? extends CComponent<?>> getComponents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setComponentsValue(IList<E> value, boolean fireEvent, boolean populate) {
        // TODO Auto-generated method stub

    }

    @Override
    protected IsWidget createContent() {
        // TODO Auto-generated method stub
        return null;
    }

}
