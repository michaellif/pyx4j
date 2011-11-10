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
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.shared.meta.SiteMap;

public class TesterSiteMap implements SiteMap { 

    @NavigationItem(navigLabel = "Landing")
    public static class Landing extends AppPlace {
    }

    public static class Folder extends AppPlace {

        @NavigationItem(navigLabel = "FolderLayout")
        public static class FolderLayout extends AppPlace {
        }

        @NavigationItem(navigLabel = "FolderValidation")
        public static class FolderValidation extends AppPlace {
        }

    }

    public static class Form extends AppPlace {

        @NavigationItem(navigLabel = "FormBasic")
        public static class FormBasic extends AppPlace {
        }

        @NavigationItem(navigLabel = "FormVisibility")
        public static class FormVisibility extends AppPlace {
        }

    }

    public static class NativeWidget extends AppPlace {

        @NavigationItem(navigLabel = "NativeWidgetBasic")
        public static class NativeWidgetBasic extends AppPlace {
        }
    }
}
