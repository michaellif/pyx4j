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
 * Created on Feb 16, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.Consts;

public abstract class LoginAttemptsCountAntiBot extends AbstractAntiBot {

    protected long loginFailureCountInterval() {
        return 3 * Consts.MIN2MSEC;
    }

    static class InvalidLoginAttempts implements Serializable {

        private static final long serialVersionUID = 4031499302195853296L;

        Vector<Long> when = new Vector<Long>();

        boolean isCaptchaRequired(long loginFailureCountInterval) {
            long now = System.currentTimeMillis();
            Iterator<Long> it = when.iterator();
            int active = 0;
            while (it.hasNext()) {
                if (it.next() < now - loginFailureCountInterval) {
                    it.remove();
                } else {
                    active++;
                }
            }
            return (active >= 3);
        }
    }

    private static Map<String, InvalidLoginAttempts> cache = new HashMap<String, InvalidLoginAttempts>();

    @Override
    protected boolean onAuthenticationFailed(String email) {
        InvalidLoginAttempts la = cache.get(email);
        if (la == null) {
            la = new InvalidLoginAttempts();
            cache.put(email, la);
        }
        la.when.add(System.currentTimeMillis());
        return la.isCaptchaRequired(loginFailureCountInterval());
    }

    @Override
    protected boolean isCaptchaRequired(String email) {
        InvalidLoginAttempts la = cache.get(email);
        if (la == null) {
            return false;
        } else {
            return la.isCaptchaRequired(loginFailureCountInterval());
        }
    }

}
