/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-01-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.shared.meta;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;

public class URLEncoder {

    private static IURLEncoder impl = createImplementation();

    private static IURLEncoder createImplementation() {
        if (ApplicationMode.isGWTClient()) {
            return new IURLEncoderClientImpl();
        } else {
            return ServerSideFactory.create(IURLEncoder.class);
        }
    }

    public static String encodeQueryString(String decodedURLComponent) {
        return impl.encodeQueryString(decodedURLComponent);
    }

}
