/*
 * Copyright (c) 2008-2017 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.masquerade;

import com.haulmont.masquerade.jmx.JmxCallHandler;
import com.haulmont.masquerade.jmx.JmxName;
import com.haulmont.masquerade.restapi.AccessToken;
import com.haulmont.masquerade.restapi.OAuthTokenService;
import com.haulmont.masquerade.restapi.ServiceGenerator;
import retrofit2.Call;

import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * Factory that provides proxy objects for remote services.
 */
public class Connectors {
    public static final String JXM_BASE_ADDRESS =
            System.getProperty("masquerade.jmx.host", ":7777");

    public static final String REST_API_BASE_URL =
            System.getProperty("masquerade.restapi.baseurl", "http://localhost:8080/app/rest/v2/");

    public static <T> T jmx(Class<T> clazz) {
        return jmx(clazz, new JmxHost(null, null, JXM_BASE_ADDRESS));
    }

    @SuppressWarnings("unchecked")
    public static <T> T jmx(Class<T> clazz, JmxHost hostInfo) {
        JmxName jmxName = clazz.getAnnotation(JmxName.class);
        if (jmxName == null) {
            throw new RuntimeException("There is no @JmxName annotation for " + clazz);
        }
        if ("".equals(jmxName.value())) {
            throw new RuntimeException("JmxName.value is empty for " + clazz);
        }

        return (T) Proxy.newProxyInstance(Connectors.class.getClassLoader(), new Class[]{clazz},
                new JmxCallHandler(hostInfo, jmxName.value()));
    }

    public static OAuthTokenService restApiOAuthService(RestApiHost hostInfo) {
        return ServiceGenerator.createService(hostInfo.getBaseUrl(),
                OAuthTokenService.class, hostInfo.getClientId(), hostInfo.getClientSecret());
    }

    public static <T> T restApi(Class<T> clazz) {
        return restApi(clazz, new RestApiHost("admin", "admin", REST_API_BASE_URL));
    }

    @SuppressWarnings("unchecked")
    public static <T> T restApi(Class<T> clazz, RestApiHost hostInfo) {
        // authenticate
        OAuthTokenService oAuthTokenService = restApiOAuthService(hostInfo);
        Call<AccessToken> token = oAuthTokenService.token(
                hostInfo.getUser(), hostInfo.getPassword(),
                hostInfo.getGrantType());

        AccessToken accessToken;
        try {
            accessToken = token.execute().body();
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain OAuth2 token");
        }

        return restApi(clazz, hostInfo, accessToken);
    }

    public static <T> T restApi(Class<T> clazz, RestApiHost hostInfo, AccessToken accessToken) {
        return ServiceGenerator.createService(hostInfo.getBaseUrl(), clazz, accessToken);
    }

    public static class JmxHost {
        private String address;
        private String user;
        private String password;

        public JmxHost(String user, String password, String address) {
            this.user = user;
            this.password = password;
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class RestApiHost {
        private String baseUrl;
        private String user;
        private String password;

        private String clientId = "client";
        private String clientSecret = "secret";
        private String grantType = "password";

        public RestApiHost(String user, String password, String baseUrl) {
            this.user = user;
            this.password = password;
            this.baseUrl = baseUrl;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }
    }
}