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
package com.pyx4j.forms.client.ui.datatable.filter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityComparatorFactory;
import com.pyx4j.forms.client.ui.CBigDecimalField;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CKeyField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

public class CriteriaEditableComponentFactory implements IEditableComponentFactory {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CComponent<?> comp;
        if (mm.isEntity()) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
            ((CEntityComboBox) comp).setOptionsComparator(EntityComparatorFactory.createStringViewComparator());
        } else if ((mm.getObjectClassType() == ObjectClassType.EntityList) || (mm.getObjectClassType() == ObjectClassType.EntitySet)) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getValueClass());
            ((CEntityComboBox) comp).setOptionsComparator(EntityComparatorFactory.createStringViewComparator());
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox();
            ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
        } else if (mm.getValueClass().equals(LogicalDate.class) || mm.getValueClass().equals(Date.class) || mm.getValueClass().equals(java.sql.Date.class)) {
            comp = new CDatePicker();
        } else if (mm.getValueClass().equals(Integer.class)) {
            comp = new CIntegerField();
        } else if (mm.getValueClass().equals(Key.class)) {
            comp = new CKeyField();
        } else if (mm.getValueClass().equals(BigDecimal.class)) {
            comp = new CBigDecimalField();
        } else if (mm.getValueClass().equals(Long.class)) {
            comp = new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            comp = new CDoubleField();
        } else if (mm.getValueClass().equals(Boolean.class)) {
            comp = new CComboBoxBoolean();
        } else {
            if (EditorType.phone.equals(mm.getEditorType())) {
                comp = new CPhoneField();
                ((CPhoneField) comp).setFormat(new CPhoneField.PhoneSearchFormat());
            } else {
                comp = new CTextField();
            }
        }
        return comp;
    }

}
