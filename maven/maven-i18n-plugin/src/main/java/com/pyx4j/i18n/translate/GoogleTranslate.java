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
 * Created on Oct 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.translate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleTranslate {

    private final String apiKey;

    public GoogleTranslate(String apiKey) {
        this.apiKey = apiKey;
    }

    public String translate(String text, String sourceLanguage, String targetLanguage) throws ClientProtocolException, IOException, ParseException,
            JSONException {
        HttpPost request = new HttpPost("https://www.googleapis.com/language/translate/v2");
        request.addHeader("X-HTTP-Method-Override", "GET");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("key", apiKey));
        formparams.add(new BasicNameValuePair("source", sourceLanguage));
        formparams.add(new BasicNameValuePair("target", targetLanguage));
        formparams.add(new BasicNameValuePair("q", text));
        request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return null;
        } else {
            String responce = EntityUtils.toString(response.getEntity(), "UTF-8");
            return getTranslatedText(new JSONObject(responce));
        }
    }

    String getTranslatedText(JSONObject json) {
        try {
            JSONObject jsonData = json.getJSONObject("data");
            JSONArray translations = jsonData.getJSONArray("translations");
            return translations.getJSONObject(0).getString("translatedText");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
