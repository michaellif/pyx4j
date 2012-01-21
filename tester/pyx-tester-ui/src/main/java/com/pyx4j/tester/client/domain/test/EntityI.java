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
 * Created on Oct 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.domain.test;

import java.sql.Time;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface EntityI extends IEntity {

    @I18n
    public enum Enum1 {

        Value0, Value1, Value2, Value3;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Caption(description = "Description for optionalStringMemberI")
    IPrimitive<String> textBox();

//    @NotNull
//    IPrimitive<String> mandatoryTextI();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> textArea();

//    @Editor(type = EditorType.textarea)
//    IPrimitive<String> optionalTextAreaII();

//    @Editor(type = EditorType.textarea)
//    @NotNull
//    IPrimitive<String> mandatoryTextAreaI();

//    @Editor(type = EditorType.textarea)
//    @NotNull
//    IPrimitive<String> mandatoryTextAreaII();

    IPrimitive<Integer> integerBox();

    IPrimitive<Boolean> checkBox();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> enterPassword();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> confirmPassword();

    IPrimitive<Enum1> enumBox();

//    @NotNull
//    IPrimitive<Enum1> mandatoryEnumI();

    @Editor(type = EditorType.suggest)
    IPrimitive<String> suggest();

//    @NotNull
//    @Editor(type = EditorType.suggest)
//    IPrimitive<String> mandatorySuggest();

    IPrimitive<Date> datePicker();

//    @NotNull
//    IPrimitive<Date> mandatoryDatePicker();

    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> optionalTimePicker();

//    @NotNull
//    @Editor(type = EditorType.timepicker)
//    IPrimitive<Time> mandatoryTimePicker();

    IPrimitive<Date> singleMonthdatePicker();

//    @NotNull
//    IPrimitive<Date> mandatorySingleMonthDatePicker();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

//    @Editor(type = EditorType.phone)
//    @NotNull
//    IPrimitive<String> mandatoryPhone();

    @Editor(type = EditorType.money)
    IPrimitive<Double> money();

//    @Editor(type = EditorType.money)
//    @NotNull
//    IPrimitive<Double> mandatoryMoney();

    @Owned
    IList<EntityII> entityIIList();

    @Owned
    IList<EntityIV> entityIVList();

    IList<EntityIV> entityIVListNotOwned();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

//    @Editor(type = EditorType.email)
//    @NotNull
//    IPrimitive<String> mandatoryEmail();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Boolean> booleanRadioGroup();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Enum1> enumRadioGroup();

    @Editor(type = EditorType.richtextarea)
    IPrimitive<String> richTextArea();

    @Editor(type = EditorType.combo)
    IPrimitive<Boolean> booleanComboBox();

}
