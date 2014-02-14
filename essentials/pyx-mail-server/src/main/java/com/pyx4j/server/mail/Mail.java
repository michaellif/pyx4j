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
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

public class Mail {

    public static MailDeliveryStatus send(MailMessage mailMessage) {
        return getMailService().send(mailMessage);
    }

    public static MailDeliveryStatus send(MailMessage mailMessage, IMailServiceConfigConfiguration mailConfig) {
        return getMailService().send(mailMessage, mailConfig);
    }

    public static IMailService getMailService() {
        return new SMTPMailServiceImpl();
    }

    /**
     * External transaction is used to persist the message, no attempt to deliver message will be made until transaction is committed
     * 
     * @param mailMessage
     * @param callbackClass
     *            optional class that will be instantiated and called once email was sent
     * @return false if message can't be sent because address is invalid, You may get exception by calling filter function on the same message
     */
    public static boolean queue(MailMessage mailMessage, Class<MailDeliveryCallback> callbackClass, IMailServiceConfigConfiguration mailConfig) {
        return getMailService().queue(mailMessage, callbackClass, mailConfig);
    }

    public static boolean queueUofW(final MailMessage mailMessage, final Class<MailDeliveryCallback> callbackClass,
            final IMailServiceConfigConfiguration mailConfig) {
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Boolean, RuntimeException>() {

            @Override
            public Boolean execute() throws RuntimeException {
                return getMailService().queue(mailMessage, callbackClass, mailConfig);
            }
        });

    }
}
