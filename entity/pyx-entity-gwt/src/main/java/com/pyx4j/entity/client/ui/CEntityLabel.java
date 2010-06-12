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
 * Created on Jun 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.gwt.NativeLabel;
import com.pyx4j.forms.client.ui.CAbstractLabel;

public class CEntityLabel extends CAbstractLabel<IEntity> {

    public CEntityLabel() {
        this(null);
    }

    public CEntityLabel(String title) {
        super(title);
    }

    @Override
    protected NativeLabel<IEntity> createNativeLabel() {
        return new NativeLabel<IEntity>(this) {
            @Override
            public void setNativeValue(IEntity value) {
                if (value == null) {
                    setText("");
                } else {
                    setText(value.getStringView());
                }
            }
        };
    }

}
