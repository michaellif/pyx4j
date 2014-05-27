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
 * Created on May 20, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.decorators.IDecorator;

public class NContainer<DATA_TYPE extends IObject<?>> extends NComponent<DATA_TYPE, CContainer<?, DATA_TYPE, ?>> implements RequiresResize, ProvidesResize {

    private IsWidget content;

    public NContainer(CContainer<?, DATA_TYPE, ?> cComponent) {
        super(cComponent);
    }

    @Override
    public IsWidget getContent() {
        return content;
    }

    @SuppressWarnings("rawtypes")
    public void setContent(IsWidget content) {
        this.content = content;
        if (getWidget() instanceof IDecorator) {
            ((IDecorator) getWidget()).setContent(content);
        } else {
            setWidget(content);
        }
    }

    @Override
    public SimplePanel getContentHolder() {
        return this;
    }

    @Override
    public void onResize() {
        if (content instanceof RequiresResize) {
            ((RequiresResize) content).onResize();
        }
    }

    @Override
    public void showErrors(boolean show) {
    }

}