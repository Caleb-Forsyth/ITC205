package bcccp.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bcccp.carpark.CarSensor;
import bcccp.carpark.Carpark;
import bcccp.carpark.Gate;
import bcccp.carpark.entry.EntryController;
import bcccp.carpark.entry.EntryUI;
import bcccp.carpark.exit.ExitController;
import bcccp.carpark.exit.ExitUI;
import bcccp.carpark.paystation.PaystationController;
import bcccp.carpark.paystation.PaystationUI;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.adhoc.AdhocTicketFactory;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.ISeasonTicketDAO;
import bcccp.tickets.season.SeasonTicket;
import bcccp.tickets.season.SeasonTicketDAO;
import bcccp.tickets.season.UsageRecordFactory;

public class TestUseCaseEnterCarpark {
	CarSensor eos;
	Gate egate;
	CarSensor eis;
	EntryUI eui;
	IAdhocTicketDAO adhocTicketDAO;
	ISeasonTicketDAO seasonTicketDAO;
	Carpark carpark;
	ISeasonTicket t1,t2;
	EntryController entryController;
	PaystationController payController;
	ExitController exitController;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		eos = new CarSensor("Entry Outside Sensor", 20, 100);
		egate = new Gate(20, 320);
		eis = new CarSensor("Entry Inside Sensor", 20, 440);
		eui = new EntryUI(320, 100);	
		adhocTicketDAO = new AdhocTicketDAO(new AdhocTicketFactory());
		seasonTicketDAO = new SeasonTicketDAO(new UsageRecordFactory());
		carpark = new Carpark("Bathurst Chase", 5, adhocTicketDAO, seasonTicketDAO);
		long curTime = System.currentTimeMillis();
		ISeasonTicket t1 = new SeasonTicket("S1111","Bathurst Chase", curTime-1000, 2*curTime);
		ISeasonTicket t2 = new SeasonTicket("S2222","Bathurst Chase", 1L, 2L);//out of hours ticket
		carpark.registerSeasonTicket(t1);
		carpark.registerSeasonTicket(t2);
		entryController = new EntryController(carpark, egate, eos, eis, eui);
	}

	@After
	public void tearDown() throws Exception {
	}

	public void l(String msg){ //temporary
		System.out.println(msg);
	}
	
	public String getUImsg(){
		return ((JTextField)((JPanel)eui.getContentPane().getComponent(0)).getComponent(0)).getText();
	}
	
	public void toggleSensor(CarSensor sensor){
		((JButton) sensor.getContentPane().getComponent(0)).doClick();
	}
	
	public void pushIssueTicketButton(){
		((JButton)eui.getContentPane().getComponent(1)).doClick();
	}
	
	public void pushTakeTicketButton(){
		((JButton)((JPanel)(eui.getContentPane()).getComponent(3)).getComponent(1)).doClick();
	}
	
	public String getPrintedTicket(){
		return ((JTextArea)((JPanel)eui.getContentPane().getComponent(3)).getComponent(0)).getText();
	}
	
	@Test
	public void testNormalFlow() {
		//Changed capacity to 1 so incremented can be confirmed
		adhocTicketDAO = new AdhocTicketDAO(new AdhocTicketFactory());
		seasonTicketDAO = new SeasonTicketDAO(new UsageRecordFactory());
		carpark = new Carpark("Bathurst Chase", 1, adhocTicketDAO, seasonTicketDAO);
		entryController = new EntryController(carpark, egate, eos, eis, eui);
		
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		
		//3. Customer pushes button
		pushIssueTicketButton();
		
		//4. System ensures spaces available
		assertNotEquals("Carpark Full",getUImsg());
		
		//5. System issues ticket
		String ticketBarcode = getPrintedTicket().split("Barcode *:")[1].trim();
		
		//6. System displays ‘Take Ticket’ message
		assertEquals("Take Ticket",getUImsg());
		
		//7. Customer takes ticket
		pushTakeTicketButton();
		
		//8. System raises entry barrier.
		assertTrue(egate.isRaised());
		
		//9.	Customer enters car park
		toggleSensor(eis);
		toggleSensor(eos);
		
		//10.	System detects that car has entered
		assertEquals("Entered",getUImsg());
		toggleSensor(eis);
		
		//11. System lowers entry barrier
		//l((Boolean.toString(egate.isRaised())) );
		assertFalse(egate.isRaised()); //error here
		
		
		//12. System records ticket usage
		assertEquals(carpark.getAdhocTicket(ticketBarcode).getBarcode(),ticketBarcode);
		
		//13. System decrements available spaces
		assertTrue(carpark.isFull());
		
	}
	
	@Test
	public void testFullWaitFlow(){
		testNormalFlow();
		//(Does testNormal flow so car is already in carpark)
		
		
		
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		//3. Customer pushes button
		pushIssueTicketButton();
		//4. System ensures spaces available
		//4.1 System displays ‘Carpark Full’ message
		assertEquals("Carpark Full",getUImsg());
		
		
		//4.2 Ad-hoc customer waits
		//4.3 Another ad-hoc customer exits the carpark
		//(out of scope to completly simulate car exiting)
		assertTrue(carpark.isFull());
		carpark.recordAdhocTicketExit();
		assertFalse(carpark.isFull());
		//4.3 System ensures current gate is first waiting?
		
		
		//The normal flow is resumed at Step 2.
		//2.System displays ‘Push Button’ message.
		
		
		assertEquals("Push Button",getUImsg());//Should change to "Push Button" when car exits but doesn't
		pushIssueTicketButton();
		
		//4. System ensures spaces available
		assertNotEquals("Carpark Full",getUImsg());
		//5. System issues ticket
		String ticketBarcode = getPrintedTicket().split("Barcode *:")[1].trim();
		//6. System displays ‘Take Ticket’ message
		assertEquals("Take Ticket",getUImsg());
		//7. Customer takes ticket
		pushTakeTicketButton();
		//8. System raises entry barrier.
		assertTrue(egate.isRaised());
		//9.	Customer enters car park
		toggleSensor(eis);
		toggleSensor(eos);
		//10.	System detects that car has entered
		assertEquals("Entered",getUImsg());
		toggleSensor(eis);
		//11. System lowers entry barrier
		assertFalse(egate.isRaised());
		//12. System records ticket usage
		assertEquals(ticketBarcode,carpark.getAdhocTicket(ticketBarcode).getBarcode());
		//13. System decrements available spaces
		assertTrue(carpark.isFull());
	}
	
	@Test
	public void testFullLeaveFlow(){
		testNormalFlow();
		//(Does testNormal flow so car is already in carpark)
		
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		//3. Customer pushes button
		pushIssueTicketButton();
		//4. System ensures spaces available
		//4.1 System displays ‘Carpark Full’ message
		assertEquals("Carpark Full",getUImsg());
		
		//4.2 Ad-hoc customer waits
		//4.2.2 System detects that a car has left without entering
		toggleSensor(eos);
		assertEquals("Idle",getUImsg());
	}
	
	@Test
	public void testValidSeasonTicketInserted(){
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		//3. Customer inserts season ticket
		entryController.ticketInserted("S1111");
		
		//small check
		assertFalse(seasonTicketDAO.findTicketById("S1111").inUse());
		
		//3.1 System ensures season ticket valid
		
		//3.2 System ejects the season ticket
		
		//The normal flow is resumed at Step 6.
		//Step 6 is incorrect for season ticket, season ticket will display ticket validated
		//6. System displays ‘Take Ticket’ message
		
		assertEquals("Ticket Validated",getUImsg());

		//7. Customer takes ticket
		pushTakeTicketButton();

		//8. System raises entry barrier.
		assertTrue(egate.isRaised());

		//9.	Customer enters car park
		toggleSensor(eis);
		toggleSensor(eos);

		//10.	System detects that car has entered
		assertEquals("Entered",getUImsg());
		toggleSensor(eis);

		//11. System lowers entry barrier
		assertFalse(egate.isRaised());


		//12. System records ticket usage
		assertTrue(seasonTicketDAO.findTicketById("S1111").inUse());
		
		
		//13. System decrements available spaces
		//(False for season ticket holders according to use case description)
		//assertTrue(carpark.isFull());
		
	}
	
	@Test
	public void testInValidSeasonTicketInserted(){
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		//3. Customer inserts season ticket
		entryController.ticketInserted("S34564");	
		//3.1 System ensures season ticket valid
		
		
		//4.1 System displays ‘Invalid ticket’ message
		assertEquals("Invalid ticket",getUImsg()); //Should display "Invalid ticket" but just beeps
		
		//4.2 System displays ‘Remove Invalid Ticket’ message
		//4.3 Customer removes ticket
		//The normal flow is resumed at Step 2.		
	}
	
	@Test
	public void testOutOfHoursSeasonTicketInserted(){
		//1. System detects that a car has arrived.
		toggleSensor(eos);
		//2.System displays ‘Push Button’ message.
		assertEquals("Push Button",getUImsg());
		//3. Customer inserts season ticket
		entryController.ticketInserted("S2222");	
		//3.1 System ensures season ticket valid
		
		
		//4.1 System displays ‘Invalid ticket’ message
		assertEquals("Invalid ticket",getUImsg()); //Should display "Ivalid ticket" but Carpark.isSeasonTicketValid logic isn't completly implemented
		
		//4.2 System displays ‘Remove Invalid Ticket’ message
		//4.3 Customer removes ticket
		//The normal flow is resumed at Step 2.		
	}

}
