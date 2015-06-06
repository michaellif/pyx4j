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
 * Created on Jun 6, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.forms.client.ui.query.QueryComposer;
import com.pyx4j.widgets.client.Label;

public class NQuery<E extends IQuery<?>> extends NFocusField<E, QueryComposer<E>, CQuery<E>, Label> {

    public NQuery(final CQuery<E> cQuery) {
        super(cQuery);

    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @Override
    protected QueryComposer<E> createEditor() {
        return new QueryComposer<E>();
    }

    @Override
    public void setNativeValue(E value) {
        if (isViewable()) {
            getViewer().setText(value == null ? "" : value.getStringView());
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public E getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

}
