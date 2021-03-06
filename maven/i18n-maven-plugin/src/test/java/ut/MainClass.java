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
 * Created on Oct 5, 2011
 * @author vlads
 */
package ut;

import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;

public class MainClass {

    private static final I18n i18n = I18n.get(MainClass.class);

    private static final String A_CONST = "A Constantant.";

    class NestedClass {

        public void goNested() {
            System.out.println(i18n.tr("Go Nested"));
        }

    }

    public MainClass() {
        System.out.println(i18n.tr("Constructor"));
    }

    @I18nContext(javaFormatFlag = true)
    public MainClass(boolean v) {
        System.out.println(i18n.tr("Constructor with java format"));
    }

    @I18nContext(javaFormatFlag = true)
    public void somthing() {
        System.out.println(i18n.tr("method with java format"));
    }

    @I18nComment("This is where we go")
    public void go() {
        System.out.println(i18n.tr("Go {0}", "500"));

        System.out.println(i18n.tr(A_CONST));

        System.out.println(i18n.tr("Inline " + "concatenation"));

        System.out.println(i18n.tr("Finish {0}", 1800));
    }

}
