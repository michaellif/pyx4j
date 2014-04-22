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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.site.shared.domain.reports.PropertyCriterionEntity;

public class PropertyCriterionEditor extends CFolderRowEditor<PropertyCriterionEntity> {

    private static List<FolderColumnDescriptor> COLUMN_DESCRIPTORS;

    private static Map<Class<?>, Collection<PropertyCriterion.Restriction>> DATA_TYPE_RESTRICTIONS;

    static {
        COLUMN_DESCRIPTORS = new ArrayList<>();
        PropertyCriterionEntity proto = EntityFactory.getEntityPrototype(PropertyCriterionEntity.class);
        COLUMN_DESCRIPTORS.add(new FolderColumnDescriptor(proto.criterionName(), "15em", true));
        COLUMN_DESCRIPTORS.add(new FolderColumnDescriptor(proto.restriction(), "20em"));
        COLUMN_DESCRIPTORS.add(new FolderColumnDescriptor(proto.value(), "20em"));

        Collection<PropertyCriterion.Restriction> numberRestriction = Arrays.asList(//@formatter:off
                PropertyCriterion.Restriction.EQUAL,
                PropertyCriterion.Restriction.NOT_EQUAL,
                PropertyCriterion.Restriction.LESS_THAN,
                PropertyCriterion.Restriction.LESS_THAN_OR_EQUAL,
                PropertyCriterion.Restriction.GREATER_THAN,
                PropertyCriterion.Restriction.GREATER_THAN_OR_EQUAL
        );//@formatter:on 

        DATA_TYPE_RESTRICTIONS = new HashMap<>();
        DATA_TYPE_RESTRICTIONS.put(Integer.class, numberRestriction);
        DATA_TYPE_RESTRICTIONS.put(Double.class, numberRestriction);
        DATA_TYPE_RESTRICTIONS.put(BigDecimal.class, numberRestriction);

        DATA_TYPE_RESTRICTIONS.put(Date.class, numberRestriction);
        DATA_TYPE_RESTRICTIONS.put(LogicalDate.class, numberRestriction);

        Collection<PropertyCriterion.Restriction> stringRestriction = Arrays.asList(//@formatter:off
                PropertyCriterion.Restriction.EQUAL,
                PropertyCriterion.Restriction.NOT_EQUAL,
                PropertyCriterion.Restriction.RDB_LIKE
        );//@formatter:off
        DATA_TYPE_RESTRICTIONS.put(String.class, stringRestriction);
        
        Collection<PropertyCriterion.Restriction> entityRestriction = Arrays.asList(//@formatter:off
                PropertyCriterion.Restriction.EQUAL,
                PropertyCriterion.Restriction.NOT_EQUAL
        );//@formatter:off
        DATA_TYPE_RESTRICTIONS.put(IEntity.class, entityRestriction);
        
        Collection<PropertyCriterion.Restriction> enumRestriction = Arrays.asList(//@formatter:off
                PropertyCriterion.Restriction.EQUAL,
                PropertyCriterion.Restriction.NOT_EQUAL
        );//@formatter:off
        DATA_TYPE_RESTRICTIONS.put(Enum.class, enumRestriction);
    }

    private IEntity tableProto;

    public PropertyCriterionEditor() {
        super(PropertyCriterionEntity.class, COLUMN_DESCRIPTORS);
    }

    public PropertyCriterionEditor(IEntity tableProto) {
        this();
        this.tableProto = tableProto;
    }

    @Override
    protected CField<?, ?> createCell(FolderColumnDescriptor column) {

        if (column == proto().path()) {
            CField<?, ?> comp = super.createCell(column);
            comp.setViewable(true);
            return comp;
        } else if (column == proto().restriction()) {
            return super.createCell(column);
        } else if (column == proto().value()) {
            return super.createCell(column);
        } else {
            return super.createCell(column);
        }
        
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (tableProto != null) {
            CComponent<?, ?, ?> comp = get(proto().restriction());
            if (comp instanceof CComboBox) {
                CComboBox<PropertyCriterion.Restriction> comboBox = (CComboBox<PropertyCriterion.Restriction>) comp;
                IObject<?> member = tableProto.getMember(new Path(getValue().path().getValue()));
                Class<?> memberType = member.getValueClass(); 
                
                // a workaround for polymorphic stuff
                if (memberType.isEnum()) {
                    memberType = Enum.class;
                } else if (member instanceof IEntity) {
                    memberType = IEntity.class;
                }
                comboBox.setOptions(DATA_TYPE_RESTRICTIONS.get(memberType));               
            }
        }
    }

}
