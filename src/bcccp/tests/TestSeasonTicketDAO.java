package bcccp.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.ISeasonTicketDAO;
import bcccp.tickets.season.IUsageRecord;
import bcccp.tickets.season.SeasonTicket;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;

public class TestSeasonTicketDAO {

	ISeasonTicketDAO sut;
	
	ISeasonTicket sticket;
	
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
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sut = new SeasonTicketDAO(new UsageRecordFactory());
		sticket = new SeasonTicket(sticketId, carparkId, startTime, endTime);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {
		assertEquals(sut.getNumberOfTickets(),0);
		sut.registerTicket(sticket);
		assertEquals(sut.getNumberOfTickets(),1);
		assertEquals(sticket,sut.findTicketById(sticketId));
		
		assertFalse(sut.findTicketById(sticketId).inUse());
		sut.recordTicketEntry(sticketId);
		assertTrue(sut.findTicketById(sticketId).inUse());
		sut.recordTicketExit(sticketId);
		assertFalse(sut.findTicketById(sticketId).inUse());
		
		sut.deregisterTicket(sticket);
		assertEquals(sut.getNumberOfTickets(),0);
		assertNull(sut.findTicketById(sticketId));
		
		
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNullConstructor(){
		sut = new SeasonTicketDAO(null);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNullRegisterTicket(){
		sut.registerTicket(null);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNullDeRegisterTicket(){
		sut.deregisterTicket(null);
		fail("Should've thrown exception");
	}
	
	
	@Test(expected=RuntimeException.class) 
	public void testNonExistantTicketEntry(){
		sut.recordTicketEntry(sticketId);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNonExistantTicketExit(){
		sut.recordTicketExit(sticketId);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNotInUseTicketExit(){
		sut.registerTicket(sticket);
		
		sut.recordTicketExit(sticketId);
		fail("Should've thrown exception");
	}

}
