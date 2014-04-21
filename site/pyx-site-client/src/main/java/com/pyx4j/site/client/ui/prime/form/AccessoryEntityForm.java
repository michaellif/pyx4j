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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

public abstract class AccessoryEntityForm<E extends IEntity> extends CEntityForm<E> {

    public AccessoryEntityForm(Class<E> clazz) {
        super(clazz);
    }

    public AccessoryEntityForm(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member) {
        CField<?, ?> comp = (CField<?, ?>) inject(member);
        comp.setDecorator(new FieldDecoratorBuilder().build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, double labelWidth, double componentWidth, double contentWidth) {
        CField<?, ?> comp = (CField<?, ?>) inject(member);
        comp.setDecorator(new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, double componentWidth, boolean dual) {
        CField<?, ?> comp = (CField<?, ?>) inject(member);
        comp.setDecorator(new FieldDecoratorBuilder(componentWidth, dual).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, double componentWidth) {
        CField<?, ?> comp = (CField<?, ?>) inject(member);
        comp.setDecorator(new FieldDecoratorBuilder(componentWidth).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, boolean dual) {
        CField<?, ?> comp = (CField<?, ?>) inject(member);
        comp.setDecorator(new FieldDecoratorBuilder(dual).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp) {
        inject(member, comp);
        comp.setDecorator(new FieldDecoratorBuilder().build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, double labelWidth, double componentWidth, double contentWidth) {
        inject(member, comp);
        comp.setDecorator(new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, double componentWidth, boolean dual) {
        inject(member, comp);
        comp.setDecorator(new FieldDecoratorBuilder(componentWidth, dual).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, double componentWidth) {
        inject(member, comp);
        comp.setDecorator(new FieldDecoratorBuilder(componentWidth).build());
        return comp;
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, boolean dual) {
        inject(member, comp);
        comp.setDecorator(new FieldDecoratorBuilder(dual).build());
        return comp;
    }
}
