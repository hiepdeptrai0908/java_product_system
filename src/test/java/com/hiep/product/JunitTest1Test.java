package com.hiep.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JunitTest1Test {

	@Test
	void test() {
		JunitTest1 test1Test = new JunitTest1();
		String resultString = test1Test.henkanString(10);
		assertEquals(resultString, "10");
		
	}

}
