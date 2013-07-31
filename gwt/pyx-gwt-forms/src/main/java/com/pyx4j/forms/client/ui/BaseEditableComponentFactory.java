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
 * Created on May 26, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.widgets.client.RadioGroup;

public class BaseEditableComponentFactory implements IEditableComponentFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        EditorType editorType = mm.getEditorType();
        if (editorType != null) {
            switch (editorType) {
            case text:
                return new CTextField();
            case password:
                return new CPasswordTextField();
            case textarea:
                return new CTextArea();
            case richtextarea:
                return new CRichTextArea();
            case combo:
                if (mm.isEntity()) {
                    CEntityComboBox comp = new CEntityComboBox(mm.getObjectClass());
                    if (mm.isEmbedded()) {
                        comp.setUseNamesComparison(true);
                    }
                    return comp;
                } else {
                    CComboBox comp = new CComboBox();
                    if (mm.getValueClass().isEnum()) {
                        comp.setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
                    }
                    return comp;
                }
            case suggest:
                if (mm.isEntity()) {
                    return new CEntitySuggestBox(mm.getObjectClass());
                } else {
                    return new CSuggestStringBox();
                }
            case captcha:
                return new CCaptcha();
            case monthyearpicker:
                return new CMonthYearPicker(false);
            case yearpicker:
                return new CMonthYearPicker(true);
            case timepicker: {
                CTimeField comp = new CTimeField();
                if (mm.getFormat() != null) {
                    comp.setTimeFormat(mm.getFormat());
                }
                return comp;
            }
            case percentage: {
                CPercentageField comp = new CPercentageField();
                if (mm.getFormat() != null) {
                    comp.setPercentageFormat(mm.getFormat());
                }
                return comp;
            }
            case money:
                return new CMoneyField();
            case email:
                return new CEmailField();
            case phone:
                return new CPhoneField();
            case radiogroup:
                if (mm.getValueClass() == Boolean.class) {
                    return new CRadioGroupBoolean(RadioGroup.Layout.HORISONTAL);
                } else if (mm.getValueClass().isEnum()) {
                    return new CRadioGroupEnum(mm.getValueClass(), RadioGroup.Layout.HORISONTAL);
                } else {
                    throw new Error("Unknown");
                }
            case label:
                if (mm.getValueClass().equals(String.class)) {
                    return new CLabel();
                } else if (mm.getValueClass().isEnum()) {
                    return new CEnumLabel();
                } else if (mm.isEntity()) {
                    return new CEntityLabel();
                } else if (mm.isNumberValueClass()) {
                    return new CNumberLabel();
                } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))
                        || (mm.getValueClass().equals(LogicalDate.class))) {
                    CDateLabel comp = new CDateLabel();
                    if (mm.getFormat() != null) {
                        (comp).setDateFormat(mm.getFormat());
                    }
                    return comp;
                } else if (mm.getValueClass() == Boolean.class) {
                    return new CBooleanLabel();
                }
            case color:
                return new CColorPicker();
            case hue:
                return new CColorPicker(true);
            default:
                throw new Error("Unknown");
            }
        } else if (mm.getValueClass().equals(String.class)) {
            return new CTextField();
        } else if (mm.isEntity()) {
            CEntityComboBox comp = new CEntityComboBox(mm.getObjectClass());
            if (mm.isEmbedded()) {
                (comp).setUseNamesComparison(true);
            }
            return comp;
        } else if (mm.getValueClass().isEnum()) {
            CComboBox comp = new CComboBox();
            comp.setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
            return comp;
        } else if (mm.getValueClass().equals(LogicalDate.class)) {
            CDatePicker comp = new CDatePicker();
            if (mm.getFormat() != null) {
                comp.setDateFormat(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            CDateLabel comp = new CDateLabel();
            if (mm.getFormat() != null) {
                comp.setDateFormat(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Time.class)) {
            CTimeField comp = new CTimeField();
            if (mm.getFormat() != null) {
                (comp).setTimeFormat(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(Boolean.class)) {
            return new CCheckBox();
        } else if (mm.getValueClass().equals(Integer.class)) {
            return new CIntegerField();
        } else if (mm.getValueClass().equals(BigDecimal.class)) {
            return new CBigDecimalField();
        } else if (mm.getValueClass().equals(Long.class)) {
            return new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            CDoubleField comp = new CDoubleField();
            if (mm.getFormat() != null) {
                (comp).setNumberPattern(mm.getFormat());
            }
            return comp;
        } else if (mm.getValueClass().equals(String.class)) {
            return new CTextField();
        } else {
            throw new Error("No Component factory for member '" + member.getMeta().getFieldName() + "' of class " + member.getValueClass());
        }
    }

}
