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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureType;
import com.pyx4j.tester.client.TesterUtils;
import com.pyx4j.tester.domain.TFile;

public class DomainFactory {

    private static int counter = 0;

    private static int poscounter = 0;

    private static int intvalues[] = { 0, 1, 2, 3 };

    private static String optstrvalues[] = { "Optional String 0", "Optional String 1", "Optional String 2", "Optional String 3" };

    private static String mndstrvalues[] = { "Mandatory String 0", "Mandatory String 1", "Mandatory String 2", "Mandatory String 3" };

    private static String opttxtvalues[] = { "Optional Text Area 0", "Optional Text Area 1", "Optional Text Area 2", "Optional Text Area 3" };

    private static String mndtxtvalues[] = { "Mandatory Text Area 0", "Mandatory Text Area 1", "Mandatory Text Area 2", "Mandatory Text Area 3" };

    private static EntityI.Enum1 enumvalues[] = { EntityI.Enum1.Value0, EntityI.Enum1.Value1, EntityI.Enum1.Value2, EntityI.Enum1.Value3 };

    private static String pwdvalues[] = { "Password 0", "Password 1", "Password 2", "Password 3" };

    private static String suggestvalues[] = { "Saggest Value 0", "Saggest Value 1", "Saggest Value 2", "Saggest Value 3" };

    private static Date datevalues[] = { new Date("1/1/2000"), new Date("1/1/2010"), new Date("1/1/2011"), new Date("3/1/2012") };

    private static Time timevalues[] = { new Time(3, 20, 15), new Time(11, 59, 59), new Time(13, 13, 13), new Time(19, 20, 21) };

    private static String phonevalues[] = { "1234567890x321", "4163576348", "3357786543x22", "5567890073" };

    private static String emailvalues[] = { "dobrik@yahoo.ca", "murzik@hotmail.com", "byaka@gmail.com", "bublik@mail.ru" };

    private static BigDecimal moneyvalues[] = { new BigDecimal("20.33"), new BigDecimal("0.00"), new BigDecimal("77.00"), new BigDecimal("8.00") };

    private static BigDecimal percentvalues[] = { new BigDecimal("20"), new BigDecimal("12"), new BigDecimal("88.5"), new BigDecimal("8.0") };

    private static Boolean boolradiovalues[] = { true, false, true, false };

    private static Integer intradiovalues[] = { 0, 1, 2, 3 };

    public static EntityI createEntityI() {
        poscounter = 0;
        return createEntityI(0);
    }

    public static EntityI createEntityINext() {

        if (++poscounter >= intvalues.length)
            poscounter = 0;

        return createEntityI(poscounter);
    }

    public static EntityII createEntityII() {
        EntityII retVal = EntityFactory.create(EntityII.class);
        retVal.optionalTextI().setValue("== EntityII Value ==");
        retVal.optionalInteger().setValue(counter++);
        return retVal;
    }

    public static EntityIII createEntityIII() {
        EntityIII retVal = EntityFactory.create(EntityIII.class);
        retVal.stringMember().setValue("== EntityII Value ==");
        retVal.integerMember().setValue(counter++);
        return retVal;
    }

    public static EntityIV createEntityIV() {
        EntityIV retVal = EntityFactory.create(EntityIV.class);
        retVal.stringMember().setValue("== EntityII Value ==");
        retVal.integerMember().setValue(counter++);
        return retVal;
    }

    private static IPersonalIdentity createPersonalIdentity() {
        IPersonalIdentity retVal = EntityFactory.create(IPersonalIdentity.class);
        retVal.obfuscatedNumber().setValue("XXXXXX123");
        return retVal;
    }

    private static EntityI createEntityI(int pos) {
        EntityI retVal = EntityFactory.create(EntityI.class);

        retVal.textBox().setValue(optstrvalues[pos]);
        retVal.integerBox().setValue(intvalues[pos]);

        ISignature signature = EntityFactory.create(ISignature.class);
        signature.fullName().setValue("John Doe");
        signature.agreeBox().setValue(true);
        signature.signatureType().setValue(SignatureType.AgreeBoxAndFullName);
        retVal.signature1().set(signature);

        retVal.enumBox().setValue(enumvalues[pos]);

        retVal.textArea().setValue(opttxtvalues[pos]);

        retVal.suggest().setValue(suggestvalues[pos]);

        retVal.datePicker().setValue(datevalues[pos]);

        retVal.optionalTimePicker().setValue(timevalues[pos]);

        retVal.singleMonthDatePicker().setValue(datevalues[pos]);

        retVal.phone().setValue(phonevalues[pos]);

        retVal.email().setValue(emailvalues[pos]);

        retVal.money().setValue(moneyvalues[pos]);

        retVal.percent1().setValue(percentvalues[pos]);

        retVal.percent2().setValue(percentvalues[pos]);

        retVal.booleanRadioGroupHorizontal().setValue(boolradiovalues[pos]);
        retVal.booleanRadioGroupVertical().setValue(boolradiovalues[pos]);

        retVal.enumRadioGroupHorizontal().setValue(enumvalues[pos]);
        retVal.enumRadioGroupVertical().setValue(enumvalues[pos]);

        retVal.intRadioGroupHorizontal().setValue(intradiovalues[pos]);
        retVal.intRadioGroupVertical().setValue(intvalues[pos]);

        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());

        retVal.personalId().set(createPersonalIdentity());

        List<TFile> fileList = new ArrayList<TFile>();
        fileList.add(createTFile(1));
        fileList.add(createTFile(2));
        retVal.files().addAll(fileList);

        return retVal;
    }

    private static TFile createTFile(int key) {
        TFile tfile = EntityFactory.create(TFile.class);
        tfile.setPrimaryKey(new Key(key));
        return tfile;
    }

    public static List<ListerDataItem> createListerData(int cols, int rows) {
        List<ListerDataItem> listerData = new ArrayList<ListerDataItem>();
        for (int row = 0; row < rows; row++) {
            ListerDataItem item = EntityFactory.create(ListerDataItem.class);
            for (int col = 0; col < cols; col++) {
                item.setMemberValue("field" + col, row + ":" + col + " - " + TesterUtils.getRandomString(8));
            }
            listerData.add(item);
        }
        return listerData;
    }
}
