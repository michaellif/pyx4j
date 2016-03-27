/*
 * Pyx4j framework
 * Copyright (C) 2008-2016 pyx4j.com.
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
 * Created on Mar 6, 2016
 * @author vlads
 *
 */
package com.pyx4j.server.mail.th;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.ServerEntityFactory;

public class EmailBuilder {

    private static class SingletonHolder {
        public static final EmailBuilder INSTANCE = new EmailBuilder();
    }

    static EmailBuilder instance() {
        return SingletonHolder.INSTANCE;
    }

    private TemplateEngine templateEngine;

    private CssInliner cssInliner;

    private EmailBuilder() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/email/");
        resolver.setSuffix(".th.html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF8");
        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            resolver.setCacheable(false);
            resolver.setCacheTTLMs(1L);
        } else {
            resolver.setCacheTTLMs(60 * Consts.MIN2MSEC);
            resolver.setCacheable(true);
        }
        templateEngine.setTemplateResolver(resolver);

        cssInliner = new JsoupCssInliner();
        // TODO consider setting inliner as template preprocessor
    }

    public static String processTemplate(String templateName, IEntity model) {
        Context context = new Context();
        context.setVariable("model", ServerEntityFactory.getPojo(model));
        String html = instance().templateEngine.process(templateName, context);
        return instance().cssInliner.inline(html);
    }

}
