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


import bcccp.carpark.CarSensor;
import bcccp.carpark.Carpark;
import bcccp.carpark.Gate;
import bcccp.carpark.ICarSensorResponder;
import bcccp.carpark.ICarpark;
import bcccp.carpark.ICarparkObserver;
import bcccp.carpark.entry.EntryController;
import bcccp.carpark.entry.EntryUI;
import bcccp.carpark.entry.IEntryController;
import bcccp.tickets.adhoc.AdhocTicket;
import bcccp.tickets.adhoc.AdhocTicketDAO;
import bcccp.tickets.season.SeasonTicketDAO;

import javax.swing.JButton;

@RunWith(MockitoJUnitRunner.class)
public class TestEntryController {

	
	@Mock Carpark carpark;
	
	@Mock Gate egate;
	@Mock CarSensor eos;
	@Mock CarSensor eis;
	@Mock EntryUI eui;
	
	@Mock EntryController sut;
	
	@Mock AdhocTicketDAO adhocTicketDAO;
	@Mock SeasonTicketDAO seasonTicketDAO;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}
	
	
	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testConstructor() {
		assertTrue(sut instanceof ICarparkObserver);
		assertTrue(sut instanceof ICarSensorResponder);
		assertTrue(sut instanceof IEntryController);
		
		sut = new EntryController(carpark, egate, eos, eis, eui);
		verify(carpark).register(any(ICarparkObserver.class));
		verify(eos).registerResponder(any(ICarSensorResponder.class));
		verify(eis).registerResponder(any(ICarSensorResponder.class));
		verify(eui).display("Idle");
		verify(eui).registerController(any(IEntryController.class));
		
	}
	
	@Test
	public void testNotifyCarparkEvent(){
		
		verify(sut,times(0)).notifyCarparkEvent();		
		ICarpark tempCarpark = new Carpark("Name",3,adhocTicketDAO,seasonTicketDAO);
		tempCarpark.register(sut);
		tempCarpark.recordAdhocTicketExit();
		verify(sut).notifyCarparkEvent();
		
		
		verify(eui,times(0)).display("Push Button");
		sut = new EntryController(carpark, egate, eos, eis, eui);
		sut.notifyCarparkEvent();
		verify(eui).display("Push Button");
	}
	
	@Test
	public void testButtonPushed(){
		sut = new EntryController(carpark, egate, eos, eis, eui);
		
		//default state is idle, should beep since no car arrived
		verify(eui,times(0)).beep();
		sut.buttonPushed();
		verify(eui).beep();
		
		//car arrives, go to waiting state
		when(eos.carIsDetected()).thenReturn(true);
		when(eos.getId()).thenReturn("eosID");
		sut.carEventDetected("eosID", true);
		
		
		verify(eui,times(0)).display("Carpark Full");
		when(carpark.isFull()).thenReturn(true);
		sut.buttonPushed();
		verify(eui).display("Carpark Full");
		
		when(carpark.isFull()).thenReturn(false);
		//mock ticket
		when(carpark.issueAdhocTicket()).thenReturn(new AdhocTicket("",0,""));
		sut.notifyCarparkEvent(); //returns carpark state to waiting is not full
		sut.buttonPushed();
		
		verify(eui).display("Take Ticket");
		
	}

	@Test
	public void testTicketInserted(){
		sut = new EntryController(carpark, egate, eos, eis, eui);
		
		//No car arrived, invalid
		verify(eui,times(0)).beep();
		sut.ticketInserted("S1111");
		verify(eui,times(1)).beep();

		
		//car arrives, go to waiting state
		when(eos.carIsDetected()).thenReturn(true);
		when(eos.getId()).thenReturn("eosID");
		sut.carEventDetected("eosID", true);
		
		//Invalid ticket while waiting/in car
		verify(eui,times(1)).beep();
		sut.ticketInserted("S1111");
		verify(eui,times(2)).beep();
		
		//Valid ticket while waiting/in car
		verify(eui,times(0)).display("Ticket Validated");
		when(carpark.isSeasonTicketValid("S1234")).thenReturn(true);
		sut.ticketInserted("S1234");
		verify(eui,times(1)).display("Ticket Validated");
		
	}

	@Test
	public void testTicketTaken(){
		sut = new EntryController(carpark, egate, eos, eis, eui);
		
		//Take ticket with no ticket there
		verify(eui,times(0)).beep();
		sut.ticketTaken();
		verify(eui,times(1)).beep();
		
		
		//car arrives, puts in valid season ticket
		when(eos.carIsDetected()).thenReturn(true);
		when(eos.getId()).thenReturn("eosID");
		sut.carEventDetected("eosID", true);
		when(carpark.isSeasonTicketValid("S1234")).thenReturn(true);
		sut.ticketInserted("S1234");
		
		
		
		verify(eui,times(0)).display("Ticket Taken");
		sut.ticketTaken();
		verify(eui,times(1)).display("Ticket Taken");
		
		
		
		//Entering carpark, new car coming in, pushed button
		when(eis.carIsDetected()).thenReturn(true);
		when(eis.getId()).thenReturn("eisID");
		sut.carEventDetected("eisID", true);
		when(eos.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eosID", false);
		when(eis.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eisID", false);
		when(eos.carIsDetected()).thenReturn(true);
		when(eos.getId()).thenReturn("eosID");
		sut.carEventDetected("eosID", true);
		//mock ticket to print
		when(carpark.issueAdhocTicket()).thenReturn(new AdhocTicket("",0,""));
		sut.buttonPushed();
		
		
		verify(eui,times(1)).display("Ticket Taken");
		sut.ticketTaken();
		verify(eui,times(2)).display("Ticket Taken");
	}
	
	@Test
	public void testCarEventDetectedCalled(){
		//Verifies that carEventDetected is called when a sensor detects car
		CarSensor tempOS = new CarSensor("osID",10,10);

		tempOS.registerResponder(sut);
		verify(sut,times(0)).carEventDetected(any(String.class),any(Boolean.class));	
		((JButton) tempOS.getContentPane().getComponent(0)).doClick();
		verify(sut,times(1)).carEventDetected(any(String.class),any(Boolean.class));
	}
	
	@Test
	public void testCarEventDetectedIdleBlockedWaiting(){
		//Tests all transitions from Idle, Blocked and Waiting
		
		sut = new EntryController(carpark, egate, eos, eis, eui);
		when(eos.getId()).thenReturn("eosID");
		when(eis.getId()).thenReturn("eisID");

		//car arrives, go from idle to waiting state
		when(eos.carIsDetected()).thenReturn(true);
		verify(eui,times(0)).display("Push Button");
		sut.carEventDetected("eosID", true);
		verify(eui,times(1)).display("Push Button");
		
		
		//car backs out, go from waiting to idle
		when(eos.carIsDetected()).thenReturn(false);
		verify(eui,times(1)).display("Idle");
		sut.carEventDetected("eosID", false);
		verify(eui,times(2)).display("Idle");
		
		
		//car reversed to inside sensor, go from idle to blocked
		//System.out.println("Test: CAR REVERSING");
		when(eis.carIsDetected()).thenReturn(true);
		verify(eui,times(0)).display("Blocked");
		sut.carEventDetected("eisID", true);
		verify(eui,times(1)).display("Blocked");
		
		//blocked to Idle since outside sensor detects absence
		//Doesn't match with specifications
		verify(eui,times(2)).display("Idle");
		when(eos.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eosID", false);
		verify(eui,times(3)).display("Idle");
		
	}
	
	@Test
	public void testCarEventDetectedTicketTakenEnteringEntered(){
		sut = new EntryController(carpark, egate, eos, eis, eui);
		when(eos.getId()).thenReturn("eosID");
		when(eis.getId()).thenReturn("eisID");
		//ticket to use
		when(carpark.issueAdhocTicket()).thenReturn(new AdhocTicket("",0,""));
		

		//car arrives & pushed button, go from idle->waiting->ticket_issued->ticket_taken
		when(eos.carIsDetected()).thenReturn(true);
		verify(eui,times(0)).display("Push Button");
		sut.carEventDetected("eosID", true);
		verify(eui,times(1)).display("Push Button");
		verify(eui,times(0)).display("Take Ticket");
		sut.buttonPushed();
		verify(eui,times(1)).display("Take Ticket");
		verify(eui,times(0)).display("Ticket Taken");
		sut.ticketTaken();
		verify(eui,times(1)).display("Ticket Taken");
		
		//Car backs out after taking ticket, Ticket_taken->Idle
		when(eos.carIsDetected()).thenReturn(false);
		verify(eui,times(1)).display("Idle");
		sut.carEventDetected("eosID", false);
		verify(eui,times(2)).display("Idle");
		
		//car arrives & pushed button, go from idle->waiting->ticket_issued->ticket_taken
		when(eos.carIsDetected()).thenReturn(true);
		sut.carEventDetected("eosID", true);
		sut.buttonPushed();
		sut.ticketTaken();
		
		//Car continues through to inside sensor, ticket_taken->entering
		when(eis.carIsDetected()).thenReturn(true);
		verify(eui,times(0)).display("Entering");
		sut.carEventDetected("eisID", true);
		verify(eui,times(1)).display("Entering");
		
		//Car backs out to outside sensor, entering->ticket_taken
		verify(eui,times(2)).display("Ticket Taken");
		when(eis.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eisID", false);
		verify(eui,times(3)).display("Ticket Taken");
		
		//back to entering state, ticket_taken->entering
		when(eis.carIsDetected()).thenReturn(true);
		sut.carEventDetected("eisID", true);
		
		//entering->entered
		when(eos.carIsDetected()).thenReturn(false);
		verify(eui,times(0)).display("Entered");
		sut.carEventDetected("eosID", false);
		verify(eui,times(1)).display("Entered");
		
		//car backs up outside sensor detect presence, entered->entering
		when(eos.carIsDetected()).thenReturn(true);
		verify(eui,times(2)).display("Entering");
		sut.carEventDetected("eosID", true);
		verify(eui,times(3)).display("Entering");
		
		//back to entered state, entering->entered
		when(eos.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eosID", false);
		
		when(egate.isRaised()).thenReturn(true);
		
		//car completely enters, entered-Idle
		verify(egate,times(0)).lower();
		verify(eui,times(2)).display("Idle");
		
		when(eis.carIsDetected()).thenReturn(false);
		sut.carEventDetected("eisID", false);
		
		verify(egate).lower();
		verify(eui,times(3)).display("Idle");
		
		when(egate.isRaised()).thenReturn(false);
		
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullCarpark(){
		new EntryController(null, egate, eos, eis, eui);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullGate(){
		new EntryController(carpark, null, eos, eis, eui);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullEnterSensor(){
		new EntryController(carpark, egate, null, eis, eui);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullExitSensor(){
		new EntryController(carpark, egate, eos, null, eui);
		fail("Should've thrown exception");
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorNullUI(){
		new EntryController(carpark, egate, eos, eis, null);
		fail("Should've thrown exception");
	}
	
	public void log(String msg){
		System.out.println("TestEC: " + msg);
	}
}
