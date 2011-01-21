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
 * Created on Jan 21, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.geo;

import com.pyx4j.entity.client.ui.crud.CriteriaEditableComponentFactory;
import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class GeoCriteriaEditableComponentFactory extends CriteriaEditableComponentFactory {

    @Override
    public CEditableComponent<?> create(IObject<?> member) {
        if (member.getObjectClass().equals(GeoCriteria.class)) {
            return new CLocationCriteriaTextField();
        } else {
            return super.create(member);
        }
    }
}
