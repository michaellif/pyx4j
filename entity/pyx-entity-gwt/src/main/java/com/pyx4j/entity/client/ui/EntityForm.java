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
 * Created on Jan 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.google.gwt.dev.util.collect.HashMap;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CPanel;

public class EntityForm extends CPanel {

    private IEntity<IEntity<?>> origEntity;

    private IEntity<IEntity<?>> editableEntity;

    private final HashMap<CEditableComponent<?>, Path> binding = new HashMap<CEditableComponent<?>, Path>();

    public EntityForm(Layout layout) {
        super(layout);
    }

    public void bind(CEditableComponent<?> component, Path path) {
        binding.put(component, path);
    }

    @SuppressWarnings("unchecked")
    public void populate(IEntity<IEntity<?>> entity) {
        this.origEntity = entity;
        editableEntity = (IEntity<IEntity<?>>) entity.cloneEntity();

        for (CEditableComponent component : binding.keySet()) {
            Path path = binding.get(component);
            component.setValue(editableEntity.getMember(path).getValue());
        }
    }

}
