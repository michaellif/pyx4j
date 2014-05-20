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
 * Created on Apr 24, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;

public class BasicCFormPanel extends DualColumnFormPanel {

    private final CForm<?> parent;

    public BasicCFormPanel(CForm<?> parent) {
        super();
        this.parent = parent;
    }

    public CompOptions append(Location location, IObject<?> member) {
        CField<?, ?> comp = parent.inject(member);
        return append(location, comp);
    }

    public CompOptions append(Location location, IObject<?> member, CField<?, ?> comp) {
        comp = parent.inject(member, comp);
        return append(location, comp);
    }

    public CompOptions append(Location location, CField<?, ?> comp) {
        super.append(location, comp);
        return new CompOptions(comp);
    }

    public void append(Location location, IObject<?> member, CComponent<?, ?, ?> comp) {
        comp = parent.inject(member, comp);
        super.append(location, comp);
    }

    public class CompOptions {

        private final CField<?, ?> comp;

        public CompOptions(CField<?, ?> comp) {
            this.comp = comp;
        }

        public FormFieldDecoratorOptions decorate() {
            final FormFieldDecoratorOptions options = createFieldDecoratorOptions();
            // Until init() method called, FieldDecoratorOptions can be updated.
            comp.setDecorator(createFieldDecorator(options));
            return options;
        }
    }

    protected FormFieldDecoratorOptions createFieldDecoratorOptions() {
        return new FormFieldDecoratorOptions();
    }

    protected FieldDecorator createFieldDecorator(final FormFieldDecoratorOptions options) {
        return new FormFieldDecorator(options);
    }

}
