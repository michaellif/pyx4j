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
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.widgets.client.RadioGroup;

public class BaseEditableComponentFactory implements IEditableComponentFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CField<?, ?> create(IObject<?> member) {
        CField<?, ?> comp;
        MemberMeta mm = member.getMeta();
        EditorType editorType = mm.getEditorType();
        if (editorType != null) {
            switch (editorType) {
            case text:
                comp = new CTextField();
            case password:
                comp = new CPasswordTextField();
            case textarea:
                comp = new CTextArea();
            case richtextarea:
                comp = new CRichTextArea();
            case combo:
                if (mm.isEntity()) {
                    comp = new CEntityComboBox(mm.getObjectClass());
                    if (mm.isEmbedded()) {
                        ((CEntityComboBox) comp).setUseNamesComparison(true);
                    }
                } else {
                    comp = new CComboBox();
                    if (mm.getValueClass().isEnum()) {
                        ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
                    }
                }
            case suggest:
                if (mm.isEntity()) {
                    comp = new CEntitySuggestBox(mm.getObjectClass());
                } else {
                    comp = new CSuggestStringBox();
                }
            case captcha:
                comp = new CCaptcha();
            case monthyearpicker:
                comp = new CMonthYearPicker(false);
            case yearpicker:
                comp = new CMonthYearPicker(true);
            case timepicker: {
                comp = new CTimeField();
                if (mm.getFormat() != null) {
                    ((CTimeField) comp).setTimeFormat(mm.getFormat());
                }
            }
            case percentage: {
                comp = new CPercentageField();
                if (mm.getFormat() != null) {
                    ((CPercentageField) comp).setPercentageFormat(mm.getFormat());
                }
            }
            case percentagelabel: {
                comp = new CPercentageLabel();
                if (mm.getFormat() != null) {
                    ((CPercentageLabel) comp).setPercentageFormat(mm.getFormat());
                }
            }
            case money:
                comp = new CMoneyField();
            case moneylabel:
                comp = new CMoneyLabel();
            case email:
                comp = new CEmailField();
            case phone:
                comp = new CPhoneField();
            case radiogroup:
                if (mm.getValueClass() == Boolean.class) {
                    comp = new CRadioGroupBoolean(RadioGroup.Layout.HORISONTAL);
                } else if (mm.getValueClass().isEnum()) {
                    comp = new CRadioGroupEnum(mm.getValueClass(), RadioGroup.Layout.HORISONTAL);
                } else {
                    throw new Error("Unknown");
                }
            case label:
                if (mm.getValueClass().equals(String.class)) {
                    comp = new CLabel();
                } else if (mm.getValueClass().isEnum()) {
                    comp = new CEnumLabel();
                } else if (mm.isEntity()) {
                    comp = new CEntityLabel();
                } else if (mm.isNumberValueClass()) {
                    comp = new CNumberLabel();
                } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))
                        || (mm.getValueClass().equals(LogicalDate.class))) {
                    comp = new CDateLabel();
                    if (mm.getFormat() != null) {
                        ((CDateLabel) comp).setDateFormat(mm.getFormat());
                    }
                } else if (mm.getValueClass() == Boolean.class) {
                    comp = new CBooleanLabel();
                }
            case color:
                comp = new CColorPicker();
            case hue:
                comp = new CColorPicker(true);
            default:
                throw new Error("Unknown");
            }
        } else if (mm.getValueClass().equals(String.class)) {
            comp = new CTextField();
        } else if (mm.isEntity()) {
            if (mm.isOwnedRelationships()) {
                comp = new CEntityLabel();
            } else {
                comp = new CEntityComboBox(mm.getObjectClass());
            }
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox();
            ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
        } else if (mm.getValueClass().equals(LogicalDate.class)) {
            comp = new CDatePicker();
            if (mm.getFormat() != null) {
                ((CDatePicker) comp).setDateFormat(mm.getFormat());
            }
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            comp = new CDateLabel();
            if (mm.getFormat() != null) {
                ((CDateLabel) comp).setDateFormat(mm.getFormat());
            }
        } else if (mm.getValueClass().equals(Time.class)) {
            comp = new CTimeField();
            if (mm.getFormat() != null) {
                ((CTimeField) comp).setTimeFormat(mm.getFormat());
            }
        } else if (mm.getValueClass().equals(Boolean.class)) {
            comp = new CCheckBox();
        } else if (mm.getValueClass().equals(Integer.class)) {
            comp = new CIntegerField();
        } else if (mm.getValueClass().equals(BigDecimal.class)) {
            comp = new CBigDecimalField();
        } else if (mm.getValueClass().equals(Long.class)) {
            comp = new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            comp = new CDoubleField();
            if (mm.getFormat() != null) {
                ((CDoubleField) comp).setNumberPattern(mm.getFormat());
            }
        } else {
            throw new Error("No Component factory for member '" + member.getMeta().getFieldName() + "' of class " + member.getValueClass());
        }
        return comp;
    }

}
