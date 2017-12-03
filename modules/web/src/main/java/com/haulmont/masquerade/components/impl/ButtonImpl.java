package com.haulmont.masquerade.components.impl;

import com.codeborne.selenide.Condition;
import com.haulmont.masquerade.components.Button;
import com.haulmont.masquerade.conditions.Caption;
import org.openqa.selenium.By;

import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.haulmont.masquerade.Selectors.byChain;
import static com.haulmont.masquerade.sys.VaadinClassNames.DISABLED_CLASSNAME;
import static org.openqa.selenium.By.className;

public class ButtonImpl extends AbstractComponent<Button> implements Button {

    public static final String BUTTON_CAPTION_CLASSNAME = "v-button-caption";

    public ButtonImpl(By by) {
        super(by);
    }

    @Override
    public String getCaption() {
        return $(byChain(by, className(BUTTON_CAPTION_CLASSNAME))).getText();
    }

    @Override
    public boolean is(Condition c) {
        if (c == enabled) {
            return !impl.has(cssClass(DISABLED_CLASSNAME));
        }
        if (c == disabled) {
            return impl.has(cssClass(DISABLED_CLASSNAME));
        }
        return Button.super.is(c);
    }

    @Override
    public boolean has(Condition condition) {
        if (condition instanceof Caption) {
            String expectedCaption = ((Caption) condition).getCaption();
            return Objects.equals(getCaption(), expectedCaption);
        }
        return Button.super.has(condition);
    }

    @Override
    public Button click() {
        impl.shouldBe(visible)
                .shouldNotHave(cssClass(DISABLED_CLASSNAME))
                .click();
        return this;
    }

    @Override
    public Button should(Condition... condition) {
        for (Condition c : condition) {
            if (c == enabled) {
                impl.shouldNotHave(cssClass(DISABLED_CLASSNAME));
            } else if (c == disabled) {
                impl.shouldHave(cssClass(DISABLED_CLASSNAME));
            } else if (c instanceof Caption) {
                String caption = ((Caption) c).getCaption();

                $(byChain(by, className(BUTTON_CAPTION_CLASSNAME)))
                        .shouldHave(exactTextCaseSensitive(caption));
            } else {
                Button.super.should(c);
            }
        }
        return this;
    }

    @Override
    public Button shouldNot(Condition... condition) {
        for (Condition c : condition) {
            if (c == enabled) {
                impl.shouldHave(cssClass(DISABLED_CLASSNAME));
            } else if (c == disabled) {
                impl.shouldNotHave(cssClass(DISABLED_CLASSNAME));
            } else if (c instanceof Caption) {
                String caption = ((Caption) c).getCaption();

                $(byChain(by, className(BUTTON_CAPTION_CLASSNAME)))
                        .shouldNotHave(exactTextCaseSensitive(caption));
            } else {
                Button.super.shouldNot(c);
            }
        }
        return this;
    }
}