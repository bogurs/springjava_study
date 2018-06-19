package com.javaweb.study.junitExercise;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpitTest {

	@Test
	public void test() {
		assertArrayEquals(new String[] {"1"}, "1".split(","));
		assertArrayEquals(new String[] {"1", "2"}, "1,2".split(","));
	}

}
