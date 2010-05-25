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
 * Created on May 25, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CFormFolder;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.FormCreator;

public class CEntityFormFolder extends CFormFolder implements FormCreator {

    private IObject<?>[][] members;

    private final EntityFormModel model;

    public CEntityFormFolder(EntityFormModel model, IObject<?>[][] members) {
        super(null);
        this.model = model;
        this.members = members;
    }

    @Override
    public CForm createForm() {
        CComponent<?>[][] components;
        if (model != null) {
            components = new CComponent<?>[members.length][members[0].length];
            for (int i = 0; i < components.length; i++) {
                for (int j = 0; j < components[0].length; j++) {
                    IObject<?> member = members[i][j];
                    if (member == null) {
                        components[i][j] = null;
                    } else if (model.contains(member)) {
                        components[i][j] = model.get(member);
                    } else {
                        components[i][j] = model.create(member);
                    }
                }
            }
        } else {

            //TODO remove after integration

            components = new CComponent[][] {

            { new CTextField("Field 1"), new CTextField("Field 2") },

            { new CTextField("Field 3"), new CTextField("Field 4") }

            };
        }
        CForm form = new CForm(true);

        form.setComponents(components);

        return form;
    }

    public void setComponets(IObject<?>[][] components2) {
        members = components2;

    }
}
