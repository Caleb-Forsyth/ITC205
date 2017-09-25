package bcccp.tests;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.tickets.adhoc.*;

public class testAdhocTicket {
	
	static IAdhocTicket sut; 

	static String carparkId;
	static int ticketNo;
	static String barcode;
	static long entryDateTime;
	static long exitDateTime;
	static long paidDateTime;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		carparkId = "0000";
		ticketNo = 0;
		barcode = "A001";		
		entryDateTime = System.currentTimeMillis();
		paidDateTime = entryDateTime+100;
		exitDateTime = paidDateTime+100;
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		sut = new AdhocTicket(carparkId, ticketNo, barcode);
	}

	@After
	public void tearDown() throws Exception {
		sut = null;
	}

	@Test
	public void testTicket() {
		assertTrue(sut instanceof IAdhocTicket);
	}
	
	@Test
	public void testBarcode(){
		assertEquals(sut.getBarcode(),barcode);
	}
	
	@Test
	public void testTicketNo(){
		assertEquals(sut.getTicketNo(),ticketNo);
	}
	
	@Test
	public void testCarparkId(){
		assertEquals(sut.getCarparkId(),carparkId);
	}
	
	@Test
	public void testEnter(){ //returns 0 before entry is recorded
		assertEquals(sut.getEntryDateTime(),0);
		sut.enter(entryDateTime);
		assertEquals(sut.getEntryDateTime(),entryDateTime);
	}
	
	@Test
	public void testIsCurrent(){
		assertFalse(sut.isCurrent());
		sut.enter(entryDateTime);
		assertTrue(sut.isCurrent());
	}
	
	@Test
	public void testPay(){
		sut.enter(entryDateTime);
		assertEquals(sut.getPaidDateTime(), 0);
		assertTrue(sut.getCharge() == 0);
		float charge = 3;
		assertFalse(sut.isPaid());
		sut.pay(paidDateTime, charge);
		assertTrue(sut.isPaid());
		assertTrue(sut.getCharge() == charge);
		assertEquals(sut.getPaidDateTime(), paidDateTime);
	}

	@Test
	public void testExit(){
		
		assertEquals(sut.getExitDateTime(), 0);
		assertFalse(sut.hasExited());
		testPay();// paying before exiting
		assertFalse(sut.hasExited());
		sut.exit(exitDateTime);
		assertTrue(sut.hasExited());
		assertEquals(sut.getExitDateTime(), exitDateTime);
	}
	
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullCarparkId(){
		new AdhocTicket(null, ticketNo, barcode);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorEmptyCarparkId(){
		new AdhocTicket("", ticketNo, barcode);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNegativeTicketNo(){
		new AdhocTicket(carparkId, -ticketNo, barcode);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorZeroTicketNo(){
		new AdhocTicket(carparkId, 0, barcode);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullBarcode(){
		new AdhocTicket(carparkId, ticketNo, null);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorEmptyBarcode(){
		new AdhocTicket(carparkId, ticketNo, "");
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testZeroEnter(){
		sut.enter(0);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testNegativeEnter(){
		sut.enter(-entryDateTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testZeroExit(){
		sut.exit(0);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testNegativeExit(){
		sut.exit(-entryDateTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testBeforePaymentExit(){
		testPay();
		sut.exit(paidDateTime);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testBeforePaymentPay(){
		testEnter();
		sut.pay(entryDateTime-1, 0);
		fail("Should've thrown exception");
	}
}
