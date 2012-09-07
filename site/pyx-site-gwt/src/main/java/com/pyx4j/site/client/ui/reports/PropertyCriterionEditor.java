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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.shared.domain.reports.PropertyCriterionEntity;

public class PropertyCriterionEditor extends CEntityFolderRowEditor<PropertyCriterionEntity> {

    private static List<EntityFolderColumnDescriptor> columnDesciptors;
    static {
        columnDesciptors = new ArrayList<EntityFolderColumnDescriptor>();
        PropertyCriterionEntity proto = EntityFactory.getEntityPrototype(PropertyCriterionEntity.class);
        columnDesciptors.add(new EntityFolderColumnDescriptor(proto.criterionName(), "15em", true));
        columnDesciptors.add(new EntityFolderColumnDescriptor(proto.restriction(), "20em"));
        columnDesciptors.add(new EntityFolderColumnDescriptor(proto.value(), "20em"));
    }

    public PropertyCriterionEditor() {
        super(PropertyCriterionEntity.class, columnDesciptors);
    }

    @Override
    protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {

        if (column == proto().path()) {
            CComponent<?, ?> comp = super.createCell(column);
            comp.setViewable(true);
            return comp;
        } else {
            return super.createCell(column);
        }
    }

}
