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
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityComparatorFactory;
import com.pyx4j.forms.client.ui.BaseEditableComponentFactory;
import com.pyx4j.forms.client.ui.CBigDecimalField;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CKeyField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

public class CriteriaEditableComponentFactory implements IEditableComponentFactory {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CField<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        EditorType editorType = mm.getEditorType();
        if (editorType != null) {
            switch (editorType) {
            case money:
            case moneylabel:
                return new CMoneyField();
            case phone:
                return new CPhoneField(PhoneType.search);
            case percentage:
            case percentagelabel:
                CPercentageField comp = new CPercentageField();
                if (mm.getFormat() != null) {
                    comp.setPercentageFormat(mm.getFormat());
                }
                return comp;
            default:
                break;
            }
        }
        if (mm.isEntity()) {
            CEntityComboBox comp = new CEntityComboBox(mm.getObjectClass());
            comp.setOptionsComparator(EntityComparatorFactory.createStringViewComparator());
            return comp;
        } else if ((mm.getObjectClassType() == ObjectClassType.EntityList) || (mm.getObjectClassType() == ObjectClassType.EntitySet)) {
            CEntityComboBox comp = new CEntityComboBox(mm.getValueClass());
            comp.setOptionsComparator(EntityComparatorFactory.createStringViewComparator());
            return comp;
        } else if (mm.getValueClass().isEnum()) {
            CComboBox comp = new CComboBox();
            comp.setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
            return comp;
        } else if (mm.getValueClass().equals(LogicalDate.class) || mm.getValueClass().equals(java.sql.Date.class)) {
            CDatePicker comp = new CDatePicker();
            String format = BaseEditableComponentFactory.getPreferedLogicalDateFormat(mm);
            if (format != null) {
                comp.setDateFormat(format);
            }
            return comp;
        } else if (mm.getValueClass().equals(Date.class)) {
            CDatePicker comp = new CDatePicker();
            String format = BaseEditableComponentFactory.getPreferedLogicalDateFormat(mm);
            if (format != null) {
                comp.setDateFormat(format);
            }
            return comp;
        } else if (mm.getValueClass().equals(Integer.class)) {
            CIntegerField comp = new CIntegerField();
            if ((mm.getFormat() != null) && (!mm.useMessageFormat())) {
                comp.setNumberPattern(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Key.class)) {
            return new CKeyField();
        } else if (mm.getValueClass().equals(BigDecimal.class)) {
            CBigDecimalField comp = new CBigDecimalField();
            if ((mm.getFormat() != null) && (!mm.useMessageFormat())) {
                comp.setNumberPattern(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Long.class)) {
            CLongField comp = new CLongField();
            if ((mm.getFormat() != null) && (!mm.useMessageFormat())) {
                comp.setNumberPattern(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Double.class)) {
            CDoubleField comp = new CDoubleField();
            if ((mm.getFormat() != null) && (!mm.useMessageFormat())) {
                comp.setNumberPattern(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Boolean.class)) {
            return new CComboBoxBoolean();
        } else {
            return new CTextField();
        }
    }

}
