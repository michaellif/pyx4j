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
import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.IFormat;

public class CEntityLabel<E extends IEntity> extends CAbstractLabel<E> {

    public CEntityLabel() {
        this(null);
    }

    public CEntityLabel(String title) {
        super(title);
        this.setFormat(new IFormat<E>() {

            @Override
            public String format(E value) {
                if (value != null) {
                    return value.getStringView();
                } else {
                    return null;
                }
            }

            @Override
            public E parse(String string) {
                return null;
            }
        });
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }
}
