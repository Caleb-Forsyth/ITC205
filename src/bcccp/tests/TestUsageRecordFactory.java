package bcccp.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.tickets.season.IUsageRecord;
import bcccp.tickets.season.IUsageRecordFactory;
import bcccp.tickets.season.UsageRecordFactory;

public class TestUsageRecordFactory {
	
	static IUsageRecordFactory sut;
	static String sticketId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sticketId = "S1234";
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sut = new UsageRecordFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		assertTrue(sut instanceof IUsageRecordFactory);
		IUsageRecord usage = sut.make(sticketId, 1L);
		assertTrue(usage instanceof IUsageRecord);
		
		//double checking valid ticket
		assertEquals(usage.getSeasonTicketId(),sticketId);
	}
	
	@Test(expected=RuntimeException.class)
	public void testMakeNullTicketId(){
		sut.make(null, 1L);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testMakeEmptyTicketId(){
		sut.make("", 1L);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testMakeStartDateZero(){
		sut.make("", 0L);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testMakeStartDateLessThanZero(){
		sut.make("", -1L);
		fail("Expected exception.");
	}

}
