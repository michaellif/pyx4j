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
 * Created on Apr 20, 2009
 * @author michaellif
 */
package com.pyx4j.gwt.commons;

import com.google.gwt.core.client.GWT;

public class BrowserType {

    // This is in fact GWT permutations.

    //** Microsoft IE 11 works as MOZILLA. ! Unless X-UA-Compatible = IE=EmulateIE10 is set.
    //** Microsoft Edge (Windows 10) works as SAFARI
    public enum Browser {
        UNKNOWN, IE, MOZILLA, SAFARI, OPERA, IPHONE
    };

    public static native boolean isIE8Native() /*-{ return ($doc.documentMode != null); }-*/;

    //This return true in Firefox and Google chrome
    @Deprecated
    public static native boolean isIENative() /*-{ return ($doc.body.insertAdjacentHTML != null); }-*/;

    // true in Microsoft Edge
    public static native boolean isIE11Native() /*-{ return !!window.MSStream; }-*/;

    @Deprecated
    public native static boolean isFirefoxNative() /*-{
                                                   var agt = $wnd.navigator.userAgent.toLowerCase();
                                                   return (agt.indexOf("firefox") != -1);
                                                   }-*/;

    public native static String getUserAgent() /*-{ return $wnd.navigator.userAgent; }-*/;

    public static final boolean isMozillaPermutation() {
        return (impl.getType() == Browser.MOZILLA);
    }

    public static final boolean isFirefox() {
        return isMozillaPermutation() && !isIE11Native();
    }

    public static final boolean isIE() {
        return (impl.getType() == Browser.IE) || (isMozillaPermutation() && isIE11Native());
    }

    public static final boolean isOpera() {
        return (impl.getType() == Browser.OPERA);
    }

    public static final boolean isSafari() {
        return (impl.getType() == Browser.SAFARI);
    }

    public static final Browser getType() {
        return impl.getType();
    }

    public static final float getVersion() {
        return impl.getVersion();
    }

    public static final String getCompiledType() {
        StringBuilder b = new StringBuilder();
        b.append(impl.getCompiledType());
        if (isIE()) {
            b.append(", isIE");
            if (isIENative()) {
                b.append(", isIENative");
            }
        }
        if (isIE8()) {
            b.append(", isIE8");
        }
        if (isIE10()) {
            b.append(", isIE10");
        }
        if (isIE11()) {
            b.append(", isIE11");
        }
        if (isIE11Native()) {
            b.append(", isIE11Native");
        }
        if (isMsEdge()) {
            b.append(", isMsEdge");
        }
        if (isFirefox()) {
            b.append(", isFirefox");
        }
        return b.toString();
    }

    private static Boolean isIE6;

    private static Boolean isIE7;

    private static Boolean isIE8;

    private static Boolean isIE10;

    private static Boolean isIE11;

    private static Boolean isMsEdge;

    private static Boolean isMobile;

    public static final boolean isIE6() {
        if (isIE6 == null) {
            isIE6 = isIE() && (!isIE8()) && (!isIE7());
        }
        return isIE6;
    }

    public static final boolean isIE7() {
        if (isIE7 == null) {
            isIE7 = isIE() && getUserAgent().toLowerCase().contains("msie 7");
        }
        return isIE7;
    }

    public static final boolean isIE8() {
        if (isIE8 == null) {
            isIE8 = isIE() && getUserAgent().toLowerCase().contains("msie 8");
        }
        return isIE8;
    }

    public static final boolean isIE10() {
        if (isIE10 == null) {
            isIE10 = isIE() && getUserAgent().toLowerCase().contains("msie 10");
        }
        return isIE10;
    }

    public static final boolean isIE11() {
        if (isIE11 == null) {
            isIE11 = isIE() && getUserAgent().toLowerCase().contains("trident/7");
        }
        return isIE11;
    }

    public static final boolean isMsEdge() {
        if (isMsEdge == null) {
            isMsEdge = isSafari() && getUserAgent().toLowerCase().contains("edge/");
        }
        return isMsEdge;
    }

    public static final boolean isMobile() {
        if (isMobile == null) {
            String ua = getUserAgent().toLowerCase();
            isMobile = ua.contains("mobile") || ua.contains("symbian");
        }
        return isMobile;
    }

    private static interface Impl {

        Browser getType();

        float getVersion();

        String getCompiledType();
    }

    private static final Impl impl = GWT.create(Impl.class);

    @SuppressWarnings("unused")
    private static class ImplUnknown implements Impl {

        Browser type;

        @Override
        public final Browser getType() {
            if (type == null) {
                // Does other detection
                if (isIENative()) {
                    type = Browser.IE;
                } else {
                    type = Browser.UNKNOWN;
                }
            }
            return type;
        }

        @Override
        public float getVersion() {
            return 0;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#UNKNOWN";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplMozilla implements Impl {

        @Override
        public final Browser getType() {
            return Browser.MOZILLA;
        }

        @Override
        public float getVersion() {
            return 0;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#MOZILLA";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplMozilla18 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.MOZILLA;
        }

        @Override
        public float getVersion() {
            return 0;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#MOZILLA_1_8";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE6 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public float getVersion() {
            String ua = getUserAgent().toLowerCase();
            String ieVersionString = ua.substring(ua.indexOf("msie ") + 5);
            ieVersionString = ieVersionString.substring(0, ieVersionString.indexOf(";"));
            return Float.parseFloat(ieVersionString);
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE6";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE8 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public float getVersion() {
            return 8;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE8";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE9 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public float getVersion() {
            return 9;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE9";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE10 implements Impl {

        @Override
        public final Browser getType() {
            return Browser.IE;
        }

        @Override
        public float getVersion() {
            return 10;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#IE10";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplSafari implements Impl {

        @Override
        public final Browser getType() {
            return Browser.SAFARI;
        }

        @Override
        public float getVersion() {
            return 0;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#SAFARI";
        }
    }

    @SuppressWarnings("unused")
    private static class ImplOpera implements Impl {

        @Override
        public final Browser getType() {
            return Browser.OPERA;
        }

        @Override
        public float getVersion() {
            return 0;
        }

        @Override
        public final String getCompiledType() {
            return "CompiledType#OPERA";
        }
    }
}
