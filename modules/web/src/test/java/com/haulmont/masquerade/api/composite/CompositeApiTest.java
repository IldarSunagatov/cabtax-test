package com.haulmont.masquerade.api.composite;

import com.haulmont.masquerade.Components;
import com.haulmont.masquerade.Wire;
import com.haulmont.masquerade.base.Composite;
import com.haulmont.masquerade.components.GroupBox;
import com.haulmont.masquerade.components.Table;
import com.haulmont.masquerade.components.TextField;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CompositeApiTest {
    @Test
    public void create() {
        UserBrowse userBrowse = Components.wire(UserBrowse.class, "sec$User.browse");
        assertNotNull(userBrowse.usersTable);
        assertNotNull(userBrowse);
    }

    @Test
    public void findChild() {
        UserBrowse userBrowse = Components.wire(UserBrowse.class);
        TextField filterTextField = userBrowse.child(TextField.class, "filterTextField");

        assertNotNull(filterTextField);
    }

    @Test
    public void actAs() {
        UserBrowse userBrowse = Components.wire(UserBrowse.class);
        GroupBox groupBox = userBrowse.actAs(GroupBox.class);

        assertNotNull(groupBox);
    }

    public static class UserBrowse extends Composite<UserBrowse> {
        @Wire
        public Table usersTable;
    }
}