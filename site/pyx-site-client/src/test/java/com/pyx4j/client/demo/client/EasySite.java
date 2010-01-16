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
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CListBox;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CListBox.Layout;
import com.pyx4j.site.client.DynamicPage;
import com.pyx4j.site.client.domain.CommandLink;
import com.pyx4j.site.client.domain.Link;
import com.pyx4j.site.client.domain.PageLink;
import com.pyx4j.site.client.domain.PageUri;
import com.pyx4j.site.client.domain.SiteStaticProperties;
import com.pyx4j.site.client.domain.StaticPage;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Dialog;

public class EasySite extends SiteStaticProperties {

    public EasySite() {

        logoUrl = "images/logo.png";

        headerLinks = new ArrayList<Link>();

        headerLinks.add(new CommandLink("Sign Up", new Command() {
            @Override
            public void execute() {
                SignUpPanel signUpPanel = new SignUpPanel();
                Dialog dialog = new Dialog("Create your FREE account", signUpPanel);
                dialog.setBody(signUpPanel);
            }
        }));

        headerLinks.add(new CommandLink("Log In", new Command() {
            @Override
            public void execute() {
                LogInPanel logInPanel = new LogInPanel();
                Dialog dialog = new Dialog("Log In", logInPanel);
                dialog.setBody(logInPanel);
                dialog.setPixelSize(300, 200);
            }
        }));

        footerLinks = new ArrayList<Link>();
        footerLinks.add(new PageLink("Technical Support", new PageUri("home:technicalSupport")));
        footerLinks.add(new PageLink("Privacy policy", new PageUri("home:privacyPolicy")));
        footerLinks.add(new PageLink("Terms of Use", new PageUri("home:termsOfUse")));

        footerCopiright = "&copy; 2010 EasySite. All rights reserved.";

        {
            StaticPage page = new StaticPage();
            page.caption = "Home";
            page.uri = new PageUri("home");
            page.data.html = "Home";
            pages.add(page);
            homePage = page;
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Services";
            page.uri = new PageUri("services");
            page.data.html = "Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>Services<P>";
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "About Us";
            page.uri = new PageUri("aboutUs");
            page.data.html = "About Us<P> About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>About Us<P>";
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Contact Us";
            page.uri = new PageUri("contactUs");
            page.data.html = "Contact Us";
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Technical Support";
            page.uri = new PageUri("home:technicalSupport");
            page.data.html = "Technical Support Technical Support Technical Support Technical Support";
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Privacy policy";
            page.uri = new PageUri("home:privacyPolicy");
            page.data.html = "Privacy policy";
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Terms of Use";
            page.uri = new PageUri("home:termsOfUse");
            page.data.html = "Terms of Use";
            pages.add(page);
        }

        {
            SignUpPage page = new SignUpPage();
            page.caption = "Sign Up";
            page.uri = new PageUri("user:signUp");
            pages.add(page);
        }

        {
            StaticPage page = new StaticPage();
            page.caption = "Profile";
            page.uri = new PageUri("user:profile");
            page.data.html = "Profile";
            pages.add(page);
        }

    }

    class SignUpPage extends DynamicPage {

        @Override
        public Widget getWidget() {
            CComponent<?>[][] components = new CComponent[][] {

            { new CTextField("Address") },

            { new CTextField("Phone") },

            { new CCheckBox("Exclude from search") },

            { new CListBox<String>("Interested in", Layout.PLAIN) },

            };

            CForm form = new CForm();

            form.setComponents(components);
            return (Widget) form.initNativeComponent();
        }

    }

    interface SignUpDialogOptions extends Custom1Option, CancelOption {

    }

}
