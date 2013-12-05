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

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.tester.domain.TFile;

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

    @Editor(type = EditorType.textarea)
    IPrimitive<String> textArea();

    IPrimitive<Integer> integerBox();

    IPrimitive<Boolean> checkBox();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> enterPassword();

    @Editor(type = EditorType.password)
    @NotNull
    IPrimitive<String> confirmPassword();

    IPrimitive<Enum1> enumBox();

    @Editor(type = EditorType.suggest)
    @NotNull
    IPrimitive<String> suggest();

    IPrimitive<Date> datePicker();

    @Editor(type = EditorType.monthyearpicker)
    IPrimitive<LogicalDate> monthPicker();

    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> optionalTimePicker();

    IPrimitive<Date> singleMonthDatePicker();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> money();

    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percent1();

    @Editor(type = EditorType.percentage)
    @Format("#")
    IPrimitive<BigDecimal> percent2();

    @Owned
    IList<EntityII> entityIIList();

    @Owned
    IList<EntityIII> entityIIIList();

    @Owned
    IList<EntityIII> entityIIIList2();

    @Owned
    IList<EntityIV> entityIVList();

    IList<EntityIV> entityIVListNotOwned();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Boolean> booleanRadioGroupHorizontal();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Boolean> booleanRadioGroupVertical();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Enum1> enumRadioGroupHorizontal();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Enum1> enumRadioGroupVertical();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Integer> intRadioGroupHorizontal();

    @Editor(type = EditorType.radiogroup)
    IPrimitive<Integer> intRadioGroupVertical();

    @Editor(type = EditorType.richtextarea)
    IPrimitive<String> richTextArea();

    @Editor(type = EditorType.combo)
    IPrimitive<Boolean> booleanComboBox();

    @Editor(type = EditorType.combo)
    EntityV entityComboBox();

    EntityV entitySelectorBox();

    IPersonalIdentity personalId();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> hue();

    @Editor(type = EditorType.color)
    IPrimitive<Integer> color();

    IPrimitive<Boolean> signature1();

    IList<TFile> files();

}
