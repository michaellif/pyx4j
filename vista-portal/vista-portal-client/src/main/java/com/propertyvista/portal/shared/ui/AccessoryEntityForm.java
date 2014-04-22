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
 * Created on Apr 15, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public abstract class AccessoryEntityForm<E extends IEntity> extends CForm<E> {

    public AccessoryEntityForm(Class<E> clazz) {
        super(clazz);
    }

    public AccessoryEntityForm(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member) {
        return inject(member, new FieldDecoratorBuilder().build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, String labelWidth, String componentWidth, String contentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth, int componentWidth, int contentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth, int componentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth).build());
    }

    public final <T extends CField<?, ?>> T injectAndDecorate(IObject<?> member, T comp) {
        return inject(member, comp, new FieldDecoratorBuilder().build());
    }

    public final <T extends CField<?, ?>> T injectAndDecorate(IObject<?> member, T comp, String labelWidth, String componentWidth, String contentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final <T extends CField<?, ?>> T injectAndDecorate(IObject<?> member, T comp, int labelWidth, int componentWidth, int contentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final <T extends CField<?, ?>> T injectAndDecorate(IObject<?> member, T comp, int labelWidth, int componentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth).build());
    }

    public final <T extends CField<?, ?>> T injectAndDecorate(IObject<?> member, T comp, int labelWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth).build());
    }

}
