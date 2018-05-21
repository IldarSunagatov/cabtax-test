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

package com.haulmont.masquerade.ui;

import com.haulmont.masquerade.components.Untyped;
import com.haulmont.masquerade.ui.composite.LoginWindow;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;
import static com.haulmont.masquerade.Components._$;
import static com.haulmont.masquerade.Components.wire;
import static com.haulmont.masquerade.Conditions.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class LoginTest {
    @Test
    public void login() {
        open("http://localhost:8080/app");

        LoginWindow loginWindow = _$(LoginWindow.class);

        assertNotNull(loginWindow.getLoginField());
        assertNotNull(loginWindow.getPasswordField());
        assertNotNull(loginWindow.getLocalesSelect());
        assertNotNull(loginWindow.getWelcomeLabel());

        loginWindow.getLoginField()
                .shouldBe(EDITABLE)
                .shouldBe(ENABLED);

        loginWindow.getLoginField().setValue("masquerade");
        loginWindow.getPasswordField().setValue("rulezzz");

        loginWindow.getRememberMeCheckBox().setChecked(true);
        loginWindow.getRememberMeCheckBox().getCaption();

        loginWindow.getWelcomeLabelTest()
                .shouldBe(VISIBLE);

        loginWindow.getLoginButton()
                .shouldBe(VISIBLE)
                .shouldBe(ENABLED)
                .shouldHave(caption("Submit"));

        String caption = loginWindow.getLoginButton().getCaption();
        assertNotNull(caption);

        boolean enabled = loginWindow.getLoginButton().is(ENABLED);
        assertTrue(enabled);

        Untyped loginFormLayout = wire(Untyped.class, "loginFormLayout");
        loginFormLayout.shouldBe(VISIBLE);

        loginWindow.getLoginButton().click();
    }
}