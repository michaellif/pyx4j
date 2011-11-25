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
 * Created on May 31, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.i18n.shared.I18n;

public abstract class CEntityContainer<E extends IObject<?>> extends CContainer<E, NativeEntityPanel<E>> implements IEditableComponentFactory {

    private static final I18n i18n = I18n.get(CEntityContainer.class);

    private IDecorator decorator;

    private ImageResource icon;

    public abstract IsWidget createContent();

    protected IDecorator<?> createDecorator() {
        return null;
    }

    public Panel getContainer() {
        return asWidget();
    }

    @Override
    protected NativeEntityPanel<E> createWidget() {
        return new NativeEntityPanel<E>();
    }

    public void initContent() {
        decorator = createDecorator();
        if (decorator == null) {
            asWidget().setWidget(createContent());
        } else {
            asWidget().setWidget(decorator);
            decorator.setComponent(this);
        }

        addValidations();
    }

    public IDecorator<?> getDecorator() {
        return decorator;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

    @Override
    public boolean isVisited() {
        return true;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        assert (getParent() != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return ((CEntityContainer<?>) getParent()).create(member);
    }

    @Override
    public void onAdopt(CContainer<?, ?> parent) {
        super.onAdopt(parent);
        initContent();
        addValidations();
    }

    public void addValidations() {

    }
}
