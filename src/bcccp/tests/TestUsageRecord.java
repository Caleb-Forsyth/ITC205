package bcccp.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.tickets.season.IUsageRecord;
import bcccp.tickets.season.UsageRecord;

public class TestUsageRecord {
	
	IUsageRecord sut;
	
	static long startTime;
	static long endTime;

	static String sticketId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		startTime = 1L;
		endTime = 5L;
		sticketId = "S1234";
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sut = new UsageRecord(sticketId, startTime);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		assertEquals(sut.getEndTime(),0);//end time not set yet
		sut.finalise(endTime);
		assertEquals(sut.getEndTime(),endTime);
		assertEquals(sut.getStartTime(),startTime);
		assertEquals(sut.getSeasonTicketId(),sticketId);
		
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullTicketId(){
		new UsageRecord(null, 1L);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorZeroStartTime(){
		new UsageRecord(sticketId,0);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorLessThanZeroStartTime(){
		new UsageRecord(sticketId,-1);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testFinaliseEndTimeZero(){
		sut.finalise(0);
		fail("Expected exception.");
	}
	
	@Test(expected=RuntimeException.class)
	public void testFinaliseEndTimeLessThanZero(){
		sut.finalise(-1);
		fail("Expected exception.");
	}
	
	

}
