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
 * Created on Feb 4, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.client.demo.client.place;

import com.google.gwt.place.shared.Place;

public class SiteMap {

    public static class Landing extends AppPlace {
    }

    public static class Home extends AppPlace {

        public static class Products extends AppPlace {

        }

        public static class Services extends AppPlace {

        }

    }

    public static class ContactUs extends AppPlace {
    }

    public static class AboutUs extends AppPlace {
    }

}
