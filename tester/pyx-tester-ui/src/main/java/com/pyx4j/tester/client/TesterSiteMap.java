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
 * Created on Oct 6, 2011
 * @author michaellif
 */
package com.pyx4j.tester.client;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class TesterSiteMap implements SiteMap {

    public static class Landing extends AppPlace {
    }

    public static class Folder extends AppPlace {

        public static class FolderLayout extends AppPlace {
        }

        public static class FolderValidation extends AppPlace {
        }

    }

    public static class FormTester extends AppPlace {

    }

    public static class NativeWidget extends AppPlace {

        public static class NativeWidgetBasic extends AppPlace {
        }

        public static class RichTextEditor extends AppPlace {
        }

        public static class AddressEditor extends AppPlace {
        }

        public static class Lister extends AppPlace {
        }

    }
}
