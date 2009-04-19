/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.debugging;

import static java.util.Arrays.*;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.util.MockitoLoggerStub;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;

public class WarningsPrinterTest extends TestBase {

    @Mock
    private IMethods mock;
    private MockitoLoggerStub logger = new MockitoLoggerStub();

    @Test
    public void shouldPrintUnusedStub() {
        // given
        Invocation unusedStub = new InvocationBuilder().simpleMethod().toInvocation();
        WarningsPrinter p = new WarningsPrinter(asList(unusedStub), Arrays.<InvocationMatcher> asList());

        // when
        p.print(logger);

        // then
        assertThat(logger.getLoggedInfo(), contains("stub was not used"));
        assertThat(logger.getLoggedInfo(), contains("simpleMethod()"));
    }

    @Test
    public void shouldPrintUnstubbedInvocation() {
        // given
        InvocationMatcher unstubbedInvocation = new InvocationBuilder().differentMethod().toInvocationMatcher();
        WarningsPrinter p = new WarningsPrinter(Arrays.<Invocation> asList(), Arrays.<InvocationMatcher> asList(unstubbedInvocation));

        // when
        p.print(logger);

        // then
        assertThat(logger.getLoggedInfo(), contains("was not stubbed"));
        assertThat(logger.getLoggedInfo(), contains("differentMethod()"));
    }

    @Test
    public void shouldPrintStubWasUsedWithDifferentArgs() {
        // given
        Invocation stub = new InvocationBuilder().arg("foo").mock(mock).toInvocation();
        InvocationMatcher wrongArg = new InvocationBuilder().arg("bar").mock(mock).toInvocationMatcher();

        WarningsPrinter p = new WarningsPrinter(Arrays.<Invocation> asList(stub), Arrays.<InvocationMatcher> asList(wrongArg));

        // when
        p.print(logger);

        // then
        assertThat(logger.getLoggedInfo(), contains("Stubbed this way"));
        assertThat(logger.getLoggedInfo(), contains("simpleMethod(\"foo\")"));
        assertThat(logger.getLoggedInfo(), contains("called with different arguments"));
        assertThat(logger.getLoggedInfo(), contains("simpleMethod(\"bar\")"));
    }

    @Test
    public void shouldNotPrintRedundantInformation() {
        // given
        Invocation stub = new InvocationBuilder().arg("foo").mock(mock).toInvocation();
        InvocationMatcher wrongArg = new InvocationBuilder().arg("bar").mock(mock).toInvocationMatcher();

        WarningsPrinter p = new WarningsPrinter(Arrays.<Invocation> asList(stub), Arrays.<InvocationMatcher> asList(wrongArg));

        // when
        p.print(logger);

        // then
        assertNotContains("stub was not used", logger.getLoggedInfo());
        assertNotContains("was not stubbed", logger.getLoggedInfo());
    }

    // TODO after 1.7 consider writing a warning when someone tries to mock a
    // class that has any final methods
}