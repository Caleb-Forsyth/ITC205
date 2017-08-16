package bcccp.carpark.entry;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarSensor;
import bcccp.carpark.ICarSensorResponder;
import bcccp.carpark.ICarpark;
import bcccp.carpark.ICarparkObserver;
import bcccp.carpark.IGate;
import bcccp.tickets.adhoc.AdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicket;

public class EntryController 
		implements ICarSensorResponder,
				   ICarparkObserver,
		           IEntryController {
	
	private IGate entryGate;
	private ICarSensor outsideSensor; 
	private ICarSensor insideSensor;
	private IEntryUI ui;
	
	private ICarpark carpark;
	private IAdhocTicket  adhocTicket = null;
	private long entryTime;
	private String seasonTicketId = null;
	
	//private boolean canLowerGate;
	
	

	public EntryController(Carpark carpark, IGate entryGate, 
			ICarSensor os, 
			ICarSensor is,
			IEntryUI ui) {
		//TODO Implement constructor
		this.entryGate = entryGate;
		this.outsideSensor = os;
		this.insideSensor = is;
		this.ui = ui;
		this.carpark = carpark;
		
		this.ui.registerController(this);
		this.outsideSensor.registerResponder(this);
		this.insideSensor.registerResponder(this);
		
	}

	@Override
	public void buttonPushed() {
		if(this.outsideSensor.carIsDetected()==true){
			//check carpark
			if(this.carpark.isFull()){
				this.ui.display("Carpark Full");
			
				//display should reset as car backs out from sensor
				
			}else{
				//ticket printed
				this.ui.display("Take Ticket");
				
				//IAdhocTicket ticket = this.carpark.issueAdhocTicket();
				//this.ui.printTicket(ticket.getCarparkId(), ticket.getTicketNo(), ticket.getEntryDateTime(), ticket.getBarcode());
				this.ui.printTicket("#1", 121, System.currentTimeMillis(), "123243");
			}
				
		}else{
			//if there's no car when button pushed it'll clear
			this.ui.display("");
		}
		
	}



	@Override
	public void ticketInserted(String barcode) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void ticketTaken() {
		this.entryGate.raise();
		//this.canLowerGate = false;
		
	}



	@Override
	public void notifyCarparkEvent() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void carEventDetected(String detectorId, boolean detected) {
		//System.out.println(detectorId);
		if(detectorId == "Entry Outside Sensor"){
			if(detected){
				this.ui.display("Push button");
			}else{
				this.ui.display("");
			}
		}
		
		
		if(this.entryGate.isRaised()){
			//gate is raised when ticket is taken
			
			if(!this.outsideSensor.carIsDetected() && this.insideSensor.carIsDetected()){
				//Car is passing gate
				this.entryGate.lower();
				this.carpark.recordAdhocTicketEntry();
				//this.canLowerGate = true;
				
			}
		}

		
		
		
	}	
}
