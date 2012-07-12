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
 * Created on Jun 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.ui.decorators.IDecorator;

public class EntityContainerScrollPanel<E extends CEntityContainer<?>> extends ScrollPanel implements IDecorator<E> {

    public EntityContainerScrollPanel() {
        setHeight("100%");
        setWidth("100%");
    }

    @Override
    public void setComponent(E component) {
        setWidget(component.createContent());
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

}