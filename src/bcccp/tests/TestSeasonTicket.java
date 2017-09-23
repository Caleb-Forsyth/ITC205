package bcccp.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.IUsageRecord;
import bcccp.tickets.season.SeasonTicket;
import bcccp.tickets.season.UsageRecord;

@RunWith(MockitoJUnitRunner.class)
public class TestSeasonTicket {

	ISeasonTicket sut;
	
	static String sticketId;
	static String carparkId;
	static long startTime;
	static long endTime;
	
	static long startUsage;
	static long endUsage;
	
	static IUsageRecord usageRecord;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sticketId = "S1111";
		carparkId = "Bathurst Chase";
		startTime = 1L;
		endTime = 10L;
		
		startUsage = 1L;
		endUsage= 5L;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sut = new SeasonTicket(sticketId, carparkId, startTime, endTime);
		usageRecord = new UsageRecord(sticketId,startUsage);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		
		assertEquals(sticketId,sut.getId());
		assertEquals(carparkId,sut.getCarparkId());
		assertEquals(startTime,sut.getStartValidPeriod());
		assertEquals(endTime,sut.getEndValidPeriod());
		
		assertFalse(sut.inUse());
		assertNull(sut.getCurrentUsageRecord());
		
		sut.recordUsage(usageRecord);
		assertTrue(sut.inUse());
		
		assertEquals(usageRecord,sut.getCurrentUsageRecord());
		
		
		sut.endUsage(endUsage);
		assertEquals(endUsage,sut.getUsageRecords().get(0).getEndTime());
		
		IUsageRecord tempRecord = new UsageRecord(sticketId,startUsage+2L);
		sut.recordUsage(tempRecord); //recording second usage
		
		List<IUsageRecord> tempUsageList = new ArrayList<IUsageRecord>();
		tempUsageList.add(usageRecord);
		tempUsageList.add(tempRecord);
		assertEquals(tempUsageList,sut.getUsageRecords());
		
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorNullTicketId(){
		sut = new SeasonTicket(null, carparkId, startTime, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorEmptyTicketId(){
		sut = new SeasonTicket("", carparkId, startTime, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorNullCarparkId(){
		sut = new SeasonTicket(sticketId, null, startTime, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorEmptyCarparkId(){
		sut = new SeasonTicket(sticketId, "", startTime, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorZeroStartTime(){
		sut = new SeasonTicket(sticketId, sticketId, 0L, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorLessThanZeroStartTime(){
		sut = new SeasonTicket(sticketId, sticketId, -1L, endTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorZeroEndTime(){
		sut = new SeasonTicket(sticketId, sticketId, startTime, 0L);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testConstructorLessThanZeroEndTime(){
		sut = new SeasonTicket(sticketId, sticketId, startTime, -1L);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNullUsageRecord(){
		sut.recordUsage(null);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testEndUsageWithSeasonTicketNotInUse(){
		sut.endUsage(2L);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testEndUsageEndTimeEqualStartTime(){
		sut.recordUsage(usageRecord);
		sut.endUsage(startUsage);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testEndUsageEndTimeLessThanStartTime(){
		sut.recordUsage(usageRecord);
		sut.endUsage(startUsage-1L);
		fail("Should've thrown exception");
	}

}
