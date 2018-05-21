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

package com.haulmont.masquerade.sys;

import com.haulmont.masquerade.Selectors;
import com.haulmont.masquerade.base.ByLocator;
import com.haulmont.masquerade.components.Component;
import com.haulmont.masquerade.components.Element;
import com.haulmont.masquerade.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

public class LoggingInvocationHandler implements InvocationHandler {
    private final Logger log;
    private final String targetId;
    private final Object target;
    private ProxyFactory proxyFactory;

    public LoggingInvocationHandler(Class componentClass, Object target, ProxyFactory proxyFactory) {
        this.log = LoggerFactory.getLogger(componentClass);
        this.targetId = getTargetId(target);
        this.target = target;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getAnnotation(Log.class) != null) {
            logExecution(method, args);
        }

        Object result;
        try {
            result = method.invoke(target, args);
        } catch (UndeclaredThrowableException | InvocationTargetException e) {
            // rethrow e
            if (e.getCause() != null) {
                throw e.getCause();
            } else {
                throw e;
            }
        }

        return postProcessResult(proxy, method, result);
    }

    private Object postProcessResult(Object proxy, Method method, Object result) {
        if (result == target) {
            // return proxy
            return proxy;
        }

        if (proxyFactory != null && result != null) {
            return proxyFactory.createProxy(method.getReturnType(), result);
        }

        return result;
    }

    private void logExecution(Method method, Object[] args) {
        if (args != null && args.length >= 1) {
            if (method.getName().startsWith("set") && args.length == 1) {
                String propertyName = StringUtils.uncapitalize(method.getName().substring("set".length()));

                log.info("Set '{}' of '{}' to '{}'", propertyName, targetId, args[0]);
            } else {
                log.info("{} of '{}' with {}", formatMethodName(method), targetId, args);
            }
        } else {
            log.info("{} '{}'", formatMethodName(method), targetId);
        }
    }

    private String formatMethodName(Method method) {
        String name = method.getName();

        String[] strings = splitByCharacterTypeCamelCase(StringUtils.capitalize(name));

        List<String> parts = new ArrayList<>(strings.length);
        parts.add(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            parts.add(StringUtils.uncapitalize(strings[i]));
        }

        return StringUtils.join(parts, " ");
    }

    private String getTargetId(Object target) {
        if (target instanceof Element) {
            Component parent = ((Element) target).getParent();

            String loggingId = ((Element) target).getLoggingId();
            if (loggingId != null) {
                return loggingId + " of " + getTargetId(parent);
            }

            if (target instanceof ByLocator) {
                return formatBy(((ByLocator) target).getBy()) + " of " + getTargetId(parent);
            }

            return getTargetId(parent);
        }

        if (target instanceof ByLocator) {
            return formatBy(((ByLocator) target).getBy());
        }

        return target.toString();
    }

    private String formatBy(By by) {
        if (by instanceof Selectors.ByChain) {
            return formatBy(((Selectors.ByChain) by).getLastBy());
        }
        if (by instanceof Selectors.ByCubaId) {
            return ((Selectors.ByCubaId) by).getCubaId();
        }
        return by.toString();
    }
}