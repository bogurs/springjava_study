package com.javaweb.study.junitExercise;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StringCalculatorTest {
	
	private StringCalculator strCal;
	
	@Before
	public void setup() {
		strCal = new StringCalculator();
	}

	@Test
	public void add_null_or_empty() {
		assertEquals(0, strCal.add(""));
		assertEquals(0, strCal.add(null));
	}
	
	@Test
	public void add_one_String() {
		assertEquals(1, strCal.add("1"));
	}
	
	@Test
	public void add_two_String_with_comma() {
		assertEquals(3, strCal.add("1,2"));
	}
	
	@Test
	public void add_split_with_another_char() {
		assertEquals(6, strCal.add("1,2:3"));
	}
	
	@Test
	public void add_custom_splitter() {
		assertEquals(6, strCal.add("//;\n1;2;3"));
	}
	
	@Test(expected=RuntimeException.class)
	public void add_with_negative() {
		strCal.add("-1,2,3");
	}

}
