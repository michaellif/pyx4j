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
package com.pyx4j.site.client.ui.prime.form;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

public abstract class AccessoryEntityForm<E extends IEntity> extends CEntityForm<E> {

    public AccessoryEntityForm(Class<E> clazz) {
        super(clazz);
    }

    public AccessoryEntityForm(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

    public final CComponent<?, ?> injectAndDecorate(IObject<?> member) {
        return inject(member, new FieldDecoratorBuilder().build());
    }

    public final CComponent<?, ?> injectAndDecorate(IObject<?> member, double labelWidth, double componentWidth, double contentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CComponent<?, ?> injectAndDecorate(IObject<?> member, double componentWidth, boolean dual) {
        return inject(member, new FieldDecoratorBuilder(componentWidth, dual).build());
    }

    public final CComponent<?, ?> injectAndDecorate(IObject<?> member, double componentWidth) {
        return inject(member, new FieldDecoratorBuilder(componentWidth).build());
    }

    public final CComponent<?, ?> injectAndDecorate(IObject<?> member, boolean dual) {
        return inject(member, new FieldDecoratorBuilder(dual).build());
    }

    public final <T extends CComponent<?, ?>> T injectAndDecorate(IObject<?> member, T comp) {
        return inject(member, comp, new FieldDecoratorBuilder().build());
    }

    public final <T extends CComponent<?, ?>> T injectAndDecorate(IObject<?> member, T comp, double labelWidth, double componentWidth, double contentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final <T extends CComponent<?, ?>> T injectAndDecorate(IObject<?> member, T comp, double componentWidth, boolean dual) {
        return inject(member, comp, new FieldDecoratorBuilder(componentWidth, dual).build());
    }

    public final <T extends CComponent<?, ?>> T injectAndDecorate(IObject<?> member, T comp, double componentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(componentWidth).build());
    }

    public final <T extends CComponent<?, ?>> T injectAndDecorate(IObject<?> member, T comp, boolean dual) {
        return inject(member, comp, new FieldDecoratorBuilder(dual).build());
    }
}
