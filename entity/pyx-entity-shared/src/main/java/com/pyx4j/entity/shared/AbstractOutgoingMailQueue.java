/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Feb 13, 2014
 * @author vlads
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractOutgoingMailQueue extends IEntity {

    public static enum MailQueueStatus implements Serializable {

        Queued,

        Undeliverable,

        Success,

        Cancelled,

        GiveUp
    }

    @Indexed
    IPrimitive<MailQueueStatus> status();

    IPrimitive<String> namespace();

    IPrimitive<String> configurationId();

    IPrimitive<String> statusCallbackClass();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    // last delivery attempt
    @Format("yyyy-MM-dd HH:mm:ss")
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    IPrimitive<Integer> attempts();

    IPrimitive<Integer> priority();

    @Length(4000)
    IPrimitive<String> lastAttemptErrorMessage();

    @Length(4000)
    IPrimitive<String> sendTo();

    @Length(4000)
    IPrimitive<String> sender();

    IPrimitive<String> keywords();

    IPrimitive<String> sentDate();

    @Caption(name = "Message-ID")
    IPrimitive<String> messageId();

    @Length(15 * 1024 * 1024)
    @RpcTransient
    IPrimitive<byte[]> data();

}
