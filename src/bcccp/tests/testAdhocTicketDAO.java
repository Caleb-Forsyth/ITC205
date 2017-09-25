package bcccp.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.tickets.adhoc.IAdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.adhoc.IAdhocTicketFactory;
import bcccp.tickets.adhoc.AdhocTicket;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;

public class testAdhocTicketDAO {
	
	static IAdhocTicketDAO sut;
	
	static IAdhocTicket AdhocTicket;
	
	static String carparkId = "0000";
	static int ticketNo = 0;
	static String barcode;
	
	static Map<String, IAdhocTicket> currentTickets;
	
	static IAdhocTicketFactory AdhocTicketFactory;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sut = new AdhocTicketDAO(new AdhocTicketFactory());
		currentTickets = new HashMap<>();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateTicket(){
		assertTrue(sut.createTicket(carparkId) instanceof AdhocTicket);
	}
	
	@Test
	public void testFindTicketByBarcode(){
		assertEquals(sut.findTicketByBarcode(barcode),AdhocTicket);
	}
	
	
	@Test
	public void testGetCurrentTickets(){
		IAdhocTicket AdhocTicket = sut.createTicket(carparkId);
		IAdhocTicket AdhocTicket1 =sut.createTicket(carparkId);
		IAdhocTicket AdhocTicket2 =sut.createTicket(carparkId);
		assertEquals(sut.getCurrentTickets().get(0),AdhocTicket);
		assertEquals(sut.getCurrentTickets().get(1),AdhocTicket1);
		assertEquals(sut.getCurrentTickets().get(2),AdhocTicket2);
	}

	
	@Test(expected=RuntimeException.class) 
	public void testNullConstructor(){
		sut = new AdhocTicketDAO(null);
		fail("Should've thrown exception"); //Fails because it doesn't throw runtime exception
	}
	
	@Test(expected=RuntimeException.class) 
	public void testNullCreateTicket(){
		sut.createTicket(null);
		fail("Should've thrown exception");
	}
	
}
