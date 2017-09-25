package bcccp.tests;

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarpark;
import bcccp.carpark.paystation.IPaystationController;
import bcccp.carpark.paystation.IPaystationUI;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.tickets.adhoc.AdhocTicket;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.adhoc.IAdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicketDAO;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;


public class TestUseCasePayforTicket {
	
	static ICarpark carpark;
	static PaystationUI pui;
	IPaystationController paystationController;
	IAdhocTicketDAO adhocTicketDAO;
	ISeasonTicketDAO seasonTicketDAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		adhocTicketDAO = new AdhocTicketDAO(new AdhocTicketFactory());
		seasonTicketDAO = new SeasonTicketDAO(new UsageRecordFactory());
		carpark = new Carpark("Bathurst Chase", 5, adhocTicketDAO, seasonTicketDAO);
		pui = new PaystationUI(660, 100);
		paystationController = new PaystationController(carpark, pui);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	 public String getUImsg(){
		  return ((JTextField)((JPanel)pui.getContentPane().getComponent(0)).getComponent(0)).getText();
	}
	
	 public void insertTicket(String ticketid){
		  ((JTextField)((JPanel)pui.getContentPane().getComponent(2)).getComponent(0)).setText(ticketid);
	}
	
	 public void pushReadTicketButton(){
		  ((JButton)((JPanel)pui.getContentPane().getComponent(2)).getComponent(1)).doClick();
	}
	
	 public void pushPayTicketButton(){
		  ((JButton)pui.getContentPane().getComponent(1)).doClick();  
	}
	
	 public String getPrintedTicket(){
		  return ((JTextArea)((JPanel)pui.getContentPane().getComponent(3)).getComponent(0)).getText();
	}
	
	 public void pushTakeTicketButton(){
		  ((JButton)((JPanel)pui.getContentPane().getComponent(3)).getComponent(1)).doClick();
	}

	@Test
	public void testNormalflow() {
		
		TestUseCaseEnterCarpark enterCarpark = new TestUseCaseEnterCarpark();
		try {
			TestUseCaseEnterCarpark.setUpBeforeClass();
			enterCarpark.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Unknown fail, couldn't setup enter use case do normal flow.");
			
		}
		enterCarpark.testNormalFlow();
		
		System.out.print(enterCarpark.carpark.isFull());
		
		assertTrue(enterCarpark.carpark.isFull());
		
		carpark = enterCarpark.carpark;
		adhocTicketDAO = enterCarpark.adhocTicketDAO;
		seasonTicketDAO = enterCarpark.seasonTicketDAO;
		pui = new PaystationUI(660, 100);
		paystationController = new PaystationController(carpark, pui);
		
		IAdhocTicket ticket = carpark.getAdhocTicket("A1");
		

		//1.System reads the ticket barcode.
			insertTicket("A1");
		 	pushReadTicketButton();
		//2.System retrieves ticket information
		//3.System calculates the charge for the ticket
		 	carpark.calculateAddHocTicketCharge(System.currentTimeMillis());
		 	
		 	
		//4.System displays the charge.
		 	assertEquals(getUImsg(),"Pay 3.00");
		 	
		//5.Customer pays the charge
		 	pushPayTicketButton();
		 	
		 	//paystationController.ticketPaid();
		 	assertTrue(ticket.isPaid());
		 	
		//6.System records the time of payment
		 	assertNotEquals(carpark.getAdhocTicket("A1").getPaidDateTime(),0);
		 	
		//7.System prints the payment time and charge on the ticket.
		//8.System ejects the ticket.
		 	getPrintedTicket();
		 	
		//9.Customer takes ticket
		 	pushTakeTicketButton();


	}
	
	@Test
	public void testAltflow1() {
		TestUseCaseEnterCarpark enterCarpark = new TestUseCaseEnterCarpark();
		try {
			TestUseCaseEnterCarpark.setUpBeforeClass();
			enterCarpark.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Unknown fail, couldn't setup enter use case do normal flow.");
			
		}
		enterCarpark.testNormalFlow();
		
		System.out.print(enterCarpark.carpark.isFull());
		
		assertTrue(enterCarpark.carpark.isFull());
		
		carpark = enterCarpark.carpark;
		adhocTicketDAO = enterCarpark.adhocTicketDAO;
		seasonTicketDAO = enterCarpark.seasonTicketDAO;
		pui = new PaystationUI(660, 100);
		paystationController = new PaystationController(carpark, pui);
				
		//1.System reads the ticket barcode.
		insertTicket("A5");
	 	pushReadTicketButton();
		
	 	//1.1 System rejects the ticket
	 	assertEquals(getUImsg(),"Take Rejected Ticket");
	}
	
	@Test
	public void testAltflow2() {
		TestUseCaseEnterCarpark enterCarpark = new TestUseCaseEnterCarpark();
		try {
			TestUseCaseEnterCarpark.setUpBeforeClass();
			enterCarpark.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Unknown fail, couldn't setup enter use case do normal flow.");
			
		}
		enterCarpark.testNormalFlow();
		
		System.out.print(enterCarpark.carpark.isFull());
		
		assertTrue(enterCarpark.carpark.isFull());
		
		carpark = enterCarpark.carpark;
		adhocTicketDAO = enterCarpark.adhocTicketDAO;
		seasonTicketDAO = enterCarpark.seasonTicketDAO;
		pui = new PaystationUI(660, 100);
		paystationController = new PaystationController(carpark, pui);
				
		//1.System reads the ticket barcode.
		insertTicket(null);
	 	pushReadTicketButton();
		
	 	//1.1 System rejects the ticket
	 	assertEquals(getUImsg(),"Take Rejected Ticket");
	}
}
