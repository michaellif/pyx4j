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
package com.pyx4j.entity.client.ui;

import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CRichTextAreaPopup;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTimeField;

public class BaseEditableComponentFactory implements EditableComponentFactory {

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
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
                return new CRichTextAreaPopup();
            case combo:
                if (mm.isEntity()) {
                    CEntityComboBox<?> comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
                    if (mm.isEmbedded()) {
                        comp.setUseNamesComparison(true);
                    }
                    return comp;
                } else {
                    CComboBox<?> comp = new CComboBox();
                    if (mm.getValueClass().isEnum()) {
                        comp.setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
                    }
                    return comp;
                }
            case suggest:
                if (mm.isEntity()) {
                    return new CEntitySuggestBox(mm.getCaption(), mm.getObjectClass());
                } else {
                    return new CSuggestBox();
                }
            case captcha:
                return new CCaptcha();
            case monthyearpicker:
                return new CMonthYearPicker(false);
            case yearpicker:
                return new CMonthYearPicker(true);
            case timepicker:
                return new CTimeField();
            case email:
                return new CEmailField();
            case phone:
                return new CPhoneField();
            default:
                throw new Error("Unknown ");
            }
        } else if (mm.getObjectClassType() == ObjectClassType.EntityList) {
            return new CEntityFormFolder(mm.getValueClass(), createEntityFormFactory(member));
        } else if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.Entity) {
            return new CEntityFormGroup(mm.getValueClass(), createEntityFormFactory(member));
        } else if (mm.getValueClass().equals(String.class)) {
            return new CTextField();
        } else if (mm.isEntity()) {
            CEntityComboBox comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
            if (mm.isEmbedded()) {
                (comp).setUseNamesComparison(true);
            }
            return comp;
        } else if (mm.getValueClass().isEnum()) {
            CComboBox<?> comp = new CComboBox();
            comp.setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
            return comp;
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            return new CDatePicker();
        } else if (mm.getValueClass().equals(Boolean.class)) {
            return new CCheckBox();
        } else if (mm.getValueClass().equals(Integer.class)) {
            return new CIntegerField();
        } else if (mm.getValueClass().equals(Long.class)) {
            return new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            CDoubleField comp = new CDoubleField();
            if (mm.getFormat() != null) {
                (comp).setNumberFormat(mm.getFormat());
            }
            return comp;
        } else {
            return new CTextField();
        }
    }

    protected EntityFormFactory<? extends IEntity> createEntityFormFactory(IObject<?> member) {
        throw new Error("No factory for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }
}
