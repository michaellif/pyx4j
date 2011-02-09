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

public class BaseEditableComponentFactory implements EditableComponentFactory {

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?, ?> comp;
        EditorType editorType = mm.getEditorType();

        if (editorType != null) {
            switch (editorType) {
            case text:
                comp = new CTextField();
                break;
            case password:
                comp = new CPasswordTextField();
                break;
            case textarea:
                comp = new CTextArea();
                break;
            case richtextarea:
                comp = new CRichTextAreaPopup();
                break;
            case combo:
                if (mm.isEntity()) {
                    comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
                    if (mm.isEmbedded()) {
                        ((CEntityComboBox) comp).setUseNamesComparison(true);
                    }
                } else {
                    comp = new CComboBox();
                    if (mm.getValueClass().isEnum()) {
                        ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
                    }
                }
                break;
            case suggest:
                if (mm.isEntity()) {
                    comp = new CEntitySuggestBox(mm.getCaption(), mm.getObjectClass());
                } else {
                    comp = new CSuggestBox();
                }
                break;
            case captcha:
                comp = new CCaptcha();
                break;
            case monthyearpicker:
                comp = new CMonthYearPicker(false);
                break;
            case yearpicker:
                comp = new CMonthYearPicker(true);
                break;
            case email:
                comp = new CEmailField();
                break;
            case phone:
                comp = new CPhoneField();
                break;
            default:
                throw new Error("Unknown ");
            }
        } else if (mm.getObjectClassType() == ObjectClassType.EntityList) {
            comp = new CEntityFormFolder(mm.getValueClass(), createEntityFormFactory(member));
        } else if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.Entity) {
            comp = new CEntityFormGroup(mm.getValueClass(), createEntityFormFactory(member));
        } else if (mm.getValueClass().equals(String.class)) {
            comp = new CTextField();
        } else if (mm.isEntity()) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
            if (mm.isEmbedded()) {
                ((CEntityComboBox) comp).setUseNamesComparison(true);
            }
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox();
            ((CComboBox) comp).setOptions(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            comp = new CDatePicker();
        } else if (mm.getValueClass().equals(Boolean.class)) {
            comp = new CCheckBox();
        } else if (mm.getValueClass().equals(Integer.class)) {
            comp = new CIntegerField();
        } else if (mm.getValueClass().equals(Long.class)) {
            comp = new CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            comp = new CDoubleField();
            if (mm.getFormat() != null) {
                ((CDoubleField) comp).setNumberFormat(mm.getFormat());
            }
        } else {
            comp = new CTextField();
        }
        return comp;
    }

    protected EntityFormFactory<? extends IEntity> createEntityFormFactory(IObject<?> member) {
        throw new Error("No factory for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }
}
