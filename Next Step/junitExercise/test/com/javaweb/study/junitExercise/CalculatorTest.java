package com.javaweb.study.junitExercise;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {

	private Calculator cal;
	
	@Before //Before라는 애너테이션을 넣어서 테스트 시 필요한 인스턴스 초기화를 시켜 각 테스트의 독립성을 유지한다.
	public void setup() {
		cal = new Calculator();
		System.out.println("setup!");
	}

	@Test
	public void add() {
		assertEquals(3, cal.add(2, 1));
		System.out.println("add!");
	}
	
	@Test
	public void divide() {
		assertEquals(3, cal.divide(6, 2));
		System.out.println("divide!");
	}
	
	@After //After라는 애너테이션을 넣어서 테스트 종료 시마다 수행해야 하는 코드를 추가할 수 있다.
	public void teardown() {
		System.out.println("teardown!");
	}

}
