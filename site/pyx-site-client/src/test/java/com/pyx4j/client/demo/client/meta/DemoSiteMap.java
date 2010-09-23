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

import com.pyx4j.site.client.incubator.annotations.Discriminator;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.SiteMap;

public class DemoSiteMap implements SiteMap {

    public interface Pub extends NavigNode {

        public interface Pub1 extends NavigNode {

            @Discriminator(type = Pub11Discriminator.class)
            public interface Pub11 extends NavigNode {
            }

            public interface Pub12 extends NavigNode {
            }

            public interface Pub13 extends NavigNode {
            }
        }

        public interface Pub2 extends NavigNode {

            public interface Pub21 extends NavigNode {
            }

            public interface Pub22 extends NavigNode {
            }
        }

    }

    public interface Int extends NavigNode {

        public interface Int1 extends NavigNode {

            public interface Int11 extends NavigNode {
            }

            public interface Int12 extends NavigNode {
            }

        }

        public interface Int2 extends NavigNode {

            public interface Int21 extends NavigNode {
            }

            public interface Int22 extends NavigNode {
            }
        }

    }

}
