package com.haulmont.masquerade.api.sys;

import com.haulmont.masquerade.Components;
import com.haulmont.masquerade.components.Component;
import com.haulmont.masquerade.components.impl.AbstractComponent;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertNotNull;

public class ComponentsImportTest {
    @Test
    public void register() {
        Components.register(Spinner.class, SpinnerImpl::new);

        Spinner spinner = Components.wire(Spinner.class, "spinner");

        assertNotNull(spinner);
    }

    public interface Spinner extends Component<Spinner> {
    }

    public static class SpinnerImpl extends AbstractComponent<Spinner> implements Spinner {
        protected SpinnerImpl(By by) {
            super(by);
        }
    }
}