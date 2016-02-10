/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 22, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server;

import java.util.Arrays;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.events.ServerConfigurationChangeEvent;
import com.pyx4j.config.server.events.ServerEventBus;
import com.pyx4j.essentials.server.ReCaptchaV2API.ReCaptchaVerificationResponse;
import com.pyx4j.essentials.server.dev.WebServiceLoggingIn;
import com.pyx4j.essentials.server.dev.WebServiceLoggingOut;
import com.pyx4j.i18n.shared.I18n;

public class ReCaptchaV2APIClient {

    private final static Logger log = LoggerFactory.getLogger(ReCaptchaV2APIClient.class);

    private static final I18n i18n = I18n.get(ReCaptchaV2APIClient.class);

    private String privateKey;

    private ReCaptchaV2API api;

    private static class SingletonHolder {
        public static final ReCaptchaV2APIClient INSTANCE = new ReCaptchaV2APIClient();
    }

    public static ReCaptchaV2APIClient instance() {
        return SingletonHolder.INSTANCE;
    }

    private ReCaptchaV2APIClient() {
        configure();
        ServerEventBus.register(new ServerConfigurationChangeEvent.Handler() {
            @Override
            public void onConfigurationChanged(ServerConfigurationChangeEvent event) {
                configure();
            }
        });
    }

    private void configure() {
        JAXRSClientFactoryBean clientFactoryBean = new JAXRSClientFactoryBean();
        clientFactoryBean.setClassLoader(Thread.currentThread().getContextClassLoader());

        clientFactoryBean.setProviders(Arrays.asList(new JacksonJsonProvider()));

        Bus bus = new CXFBusFactory().createBus();
        clientFactoryBean.setBus(bus);
        bus.setExtension(new ReCaptchaV2HTTPConduitConfigurer(), HTTPConduitConfigurer.class);

        PropertiesConfiguration config = ServerSideConfiguration.instance().getConfigProperties();

        clientFactoryBean.setAddress(config.getValue("recaptcha.url", "https://www.google.com/recaptcha/api"));
        if (config.getBooleanValue("recaptcha.debug", false)) {
            clientFactoryBean.getInInterceptors().add(new WebServiceLoggingIn(log));
            clientFactoryBean.getOutInterceptors().add(new WebServiceLoggingOut(log));
        }
        privateKey = ServerSideConfiguration.instance(EssentialsServerSideConfiguration.class).getReCaptchaPrivateKey();

        clientFactoryBean.setServiceClass(ReCaptchaV2API.class);
        api = clientFactoryBean.create(ReCaptchaV2API.class);
    }

    public void assertCaptcha(String userResponseToken, String remoteAddr) {
        ReCaptchaVerificationResponse rc;
        try {
            rc = api.verify(privateKey, userResponseToken, remoteAddr);
        } catch (Throwable e) {
            log.error("reCAPTCHA error", e);
            throw new RuntimeExceptionSerializable(i18n.tr("reCAPTCHA Connection Failed"));
        }
        if (!rc.success) {
            if (rc.errorCodes != null) {
                log.error("reCAPTCHAv2 configuration error {}", (Object) rc.errorCodes);
            } else {
                log.warn("reCAPTCHAv2 Incorrect; userResponseToken {}, remoteAddr = {}, privateKey = {}", userResponseToken, remoteAddr, privateKey);
            }
            throw new UserRuntimeException(i18n.tr("The CAPTCHA Solution You Entered Was Incorrect"));
        }
    }

}
