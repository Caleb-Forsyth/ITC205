package bcccp.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


import bcccp.carpark.Carpark;
import bcccp.carpark.ICarpark;
import bcccp.carpark.ICarparkObserver;


import javax.swing.JButton;

import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.IPaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.tickets.adhoc.AdhocTicket;
import bcccp.carpark.paystation.IPaystationUI;

@RunWith(MockitoJUnitRunner.class)
public class testPaystation {
	@Mock static ICarpark carpark;
	@Mock static IPaystationUI pui;
	
	static PaystationController sut;
	static String barcode;
	static String carparkId;
	static int ticketNo;
	static long entryTime; 
	static long paidTime;
	static float charge;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		barcode = "A0001";
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		sut = new PaystationController(carpark, pui);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructor(){
		assertTrue(sut instanceof IPaystationController);
		verify(pui).registerController(any(IPaystationController.class));
		verify(pui).display("Idle");
	}

	@Test
	public void testTicketInserted() {
		verify(pui).display("Idle");
		verify(pui,times(0)).display("Take Rejected Ticket");
		sut.ticketInserted(barcode);
		verify(pui).beep();
		verify(pui,times(1)).display("Take Rejected Ticket");
		
		MockitoAnnotations.initMocks(this);
		sut = new PaystationController(carpark, pui);
		
		when(carpark.calculateAddHocTicketCharge(any(Long.class))).thenReturn(3.0f);
		verify(pui).display("Idle");
		AdhocTicket ticket;
		when(carpark.getAdhocTicket(barcode)).thenReturn(ticket = new AdhocTicket("", 0, ""));
		ticket.enter(System.currentTimeMillis());
		verify(pui,times(0)).display("Pay 3.00");
		verify(carpark,times(0)).calculateAddHocTicketCharge(any(Long.class));
		sut.ticketInserted(barcode);
		verify(pui,times(1)).display("Pay 3.00");
		verify(carpark,times(1)).calculateAddHocTicketCharge(any(Long.class));
		
		// in the paid state
		
		sut.ticketInserted(barcode);
		verify(pui).beep();
	}
	
	@Test
	public void testTicketPaid() {
		sut.ticketPaid();
		verify(pui).beep();
		AdhocTicket ticket;
		when(carpark.getAdhocTicket(barcode)).thenReturn(ticket = new AdhocTicket("", 0, ""));
		sut.ticketInserted(barcode);
		sut.ticketPaid(); //in the waiting state
		verify(carpark,times(1)).calculateAddHocTicketCharge(ticket.getEntryDateTime());
		verify(pui,times(1)).display("Paid");
		verify(pui,times(1)).printTicket("", 0, ticket.getEntryDateTime(), ticket.getPaidDateTime(), ticket.getCharge(), "");		
	}
	
	@Test
	public void testTicketTaken() {
		sut.ticketTaken();
		verify(pui).beep();
		
		when(carpark.getAdhocTicket(barcode)).thenReturn(new AdhocTicket("", 0, ""));
		sut.ticketInserted(barcode);
		
		sut.ticketPaid(); //in the waiting state
		sut.ticketTaken();
		verify(pui,times(2)).display("Idle");//at idle by default when created
	
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullCarpark(){
		new PaystationController(null, pui);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullPaystationUI(){
		new PaystationController(carpark, null);
		fail("Should've thrown exception");
	}
}
