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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.Consts;
import com.pyx4j.server.contexts.Context;

public abstract class LoginAttemptsCountAntiBot extends AbstractAntiBot {

    protected long loginFailureCountInterval() {
        return 3 * Consts.MIN2MSEC;
    }

    protected int loginFailureCountByEmail(LoginType loginType) {
        switch (loginType) {
        case accessToken:
            return 2;
        case userLogin:
            return 3;
        default:
            throw new IllegalArgumentException();
        }
    }

    protected int loginFailureCountByIp(LoginType loginType) {
        return loginFailureCountByEmail(loginType) * 3;
    }

    static class InvalidLoginAttempts implements Serializable {

        private static final long serialVersionUID = 4031499302195853296L;

        Vector<Long> when = new Vector<Long>();

        boolean isCaptchaRequired(int loginFailureCount, long loginFailureCountInterval) {
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
            return (active >= loginFailureCount);
        }
    }

    public static enum CountParamType {

        email,

        ipAddress

    }

    private static Map<String, InvalidLoginAttempts> cacheByParam = new Hashtable<String, InvalidLoginAttempts>();

    /**
     * Allow to change IP address of request when accessing system via API
     */
    protected String getRequestRemoteAddr() {
        return Context.getRequestRemoteAddr();
    }

    protected InvalidLoginAttempts getCounter(boolean create, LoginType loginType, CountParamType paramType, String param) {
        String key = loginType.name() + paramType.name() + param;
        InvalidLoginAttempts la = cacheByParam.get(key);
        if (create && (la == null)) {
            la = new InvalidLoginAttempts();
            cacheByParam.put(key, la);
        }
        return la;
    }

    protected InvalidLoginAttempts addCounter(LoginType loginType, CountParamType paramType, String param) {
        InvalidLoginAttempts la = getCounter(true, loginType, paramType, param);
        la.when.add(System.currentTimeMillis());
        return la;
    }

    @Override
    protected boolean onAuthenticationFailed(LoginType loginType, String email) {
        InvalidLoginAttempts la1 = addCounter(loginType, CountParamType.email, email);
        InvalidLoginAttempts la2 = addCounter(loginType, CountParamType.ipAddress, getRequestRemoteAddr());

        return la1.isCaptchaRequired(loginFailureCountByEmail(loginType), loginFailureCountInterval())
                || la2.isCaptchaRequired(loginFailureCountByIp(loginType), loginFailureCountInterval());
    }

    @Override
    protected boolean isCaptchaRequired(LoginType loginType, String email) {
        {
            InvalidLoginAttempts la = getCounter(false, loginType, CountParamType.ipAddress, getRequestRemoteAddr());
            if ((la != null) && la.isCaptchaRequired(loginFailureCountByIp(loginType), loginFailureCountInterval())) {
                return true;
            }
        }

        {
            InvalidLoginAttempts la = getCounter(false, loginType, CountParamType.email, email);
            if (la == null) {
                return false;
            } else {
                return la.isCaptchaRequired(loginFailureCountByEmail(loginType), loginFailureCountInterval());
            }
        }
    }
}
