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
 * Created on Aug 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.reports.PropertyCriterionEntity;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

public class PropertyCriterionEditor extends CEntityForm<PropertyCriterionEntity> {

    public PropertyCriterionEditor() {
        super(PropertyCriterionEntity.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        panel.setCellPadding(5);
        panel.getColumnFormatter().setWidth(0, "20%");
        panel.setWidget(0, 0, inject(proto().path()));
        get(proto().path()).setViewable(true);
        panel.setWidget(0, 1, inject(proto().restriction()));
        panel.setWidget(0, 2, inject(proto().value()));

        return panel;
    }

}
