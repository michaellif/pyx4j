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

public interface IMailService {

    public MailDeliveryStatus send(MailMessage mailMessage, IMailServiceConfigConfiguration mailConfig);

    public MailDeliveryStatus send(MailMessage mailMessage);

    /**
     * External transaction is used to persist the message, no attempt to deliver message will be made until transaction is committed
     * 
     * @param mailMessage
     * @param callbackClass
     *            optional class that will be instantiated and called once email was sent
     * @param mailConfig
     *            optional default configuration will be used
     */
    public void queue(MailMessage mailMessage, Class<MailDeliveryCallback> callbackClass, IMailServiceConfigConfiguration mailConfig);

    /**
     * Apply address restrictions to the messages
     * 
     * @param mailMessage
     * @return
     */
    public MailMessage filter(MailMessage mailMessage, IMailServiceConfigConfiguration mailConfig);

    public boolean isDisabled();

    public void setDisabled(boolean disabled);
}
