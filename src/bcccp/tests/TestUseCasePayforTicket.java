package bcccp.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarpark;
import bcccp.carpark.paystation.IPaystationUI;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;

public class TestUseCasePayforTicket {
	
	static ICarpark carpark;
	static IPaystationUI pui;
	PaystationController paystationController;
	AdhocTicketDAO adhocTicketDAO;
	SeasonTicketDAO seasonTicketDAO;

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

	@Test
	public void testNormalflow() {
		
		//1.System reads the ticket barcode.
		
		//2.System retrieves ticket information
		//3.System calculates the charge for the ticket
		//4.System displays the charge.
		//5.Customer pays the charge	
		//6.System records the time of payment
		//7.System prints the payment time and charge on the ticket.
		//8.System ejects the ticket.
		//9.Customer takes ticket	

	}
	

}
