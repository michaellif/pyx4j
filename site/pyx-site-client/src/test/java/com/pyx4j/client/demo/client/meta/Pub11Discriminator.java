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
 * Created on Sep 23, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.client.demo.client.meta;

import com.pyx4j.client.demo.client.meta.pages.Pub1;
import com.pyx4j.site.client.incubator.DiscriminatorAdapter;

public class Pub11Discriminator implements DiscriminatorAdapter {

    public Class<Pub1> getPagePanel() {
        return Pub1.class;
    }
}
