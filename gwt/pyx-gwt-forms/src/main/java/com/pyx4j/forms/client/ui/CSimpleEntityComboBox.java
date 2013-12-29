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
 * Created on 2012-10-24
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.core.IEntity;

public class CSimpleEntityComboBox<E extends IEntity> extends CComboBox<E> {

    public CSimpleEntityComboBox() {
        super();
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || getValue().isNull();
    }

    @Override
    public boolean isValuesEquals(E value1, E value2) {
        if (((value1 == null) || value1.isNull()) && ((value2 == null) || value2.isNull())) {
            return true;
        } else {
            return EqualsHelper.equals(value1, value2);
        }
    }

    @Override
    public String getItemName(E o) {
        if ((o == null) || (o.isNull())) {
            // Get super's NULL presentation
            return super.getItemName(null);
        } else {
            return o.getStringView();
        }
    }

}
