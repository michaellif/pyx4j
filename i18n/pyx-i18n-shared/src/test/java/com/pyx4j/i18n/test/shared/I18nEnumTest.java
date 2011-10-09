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
 * Created on 2011-05-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.test.shared;

import junit.framework.TestCase;

import com.pyx4j.i18n.shared.I18nEnum;

//TODO
public class I18nEnumTest extends TestCase {

    public void testTODO() {

    }

    public void XtestTranslation() {
        assertEquals("Dog", I18nEnum.tr(Pet.dog));
        assertEquals("Cat", I18nEnum.tr(Pet.cat));
        assertEquals("Ferret", I18nEnum.tr(Pet.ferret));
    }

    public void XtestUnAnotedEnum() {
        assertEquals("Hoof", I18nEnum.tr(HorseFood.Hoof));
        assertEquals("Grain", I18nEnum.tr(HorseFood.Grain));
    }

    public void XtestCapitalizeNames() {
        assertEquals("Felis Catus", I18nEnum.tr(Cats.FelisCatus));
        assertEquals("African Wildcat", I18nEnum.tr(Cats.africanWildcat));
    }
}
