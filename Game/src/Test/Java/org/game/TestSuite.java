package org.game;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("org.game")
@SelectClasses({
        CardDeckTest.class,
        CardGameTest.class,
        PlayerTest.class,
        CardTest.class
})

public class TestSuite {

}
