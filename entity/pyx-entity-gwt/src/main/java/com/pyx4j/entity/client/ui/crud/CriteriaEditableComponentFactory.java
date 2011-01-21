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
package com.pyx4j.entity.client.ui.crud;

import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CTextField;

public class CriteriaEditableComponentFactory implements EditableComponentFactory {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CEditableComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?> comp;
        if (mm.isEntity()) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox();
            ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
        } else if (mm.getValueClass().equals(Date.class) || mm.getValueClass().equals(java.sql.Date.class)) {
            comp = new CDatePicker();
        } else if (mm.getValueClass().equals(Integer.class)) {
            comp = new CIntegerField();
        } else if (mm.getValueClass().equals(Long.class)) {
            comp = new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            comp = new CDoubleField();
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
