package bcccp.carpark.entry;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarSensor;
import bcccp.carpark.ICarSensorResponder;
import bcccp.carpark.ICarpark;
import bcccp.carpark.ICarparkObserver;
import bcccp.carpark.IGate;
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
			
			this.ui.display("Take Ticket");
			//ticket printed
			
			
			
		}else{
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
		if(detectorId == "Entry Inside Sensor"){
			this.entryGate.lower();
		}
		
	}	
}
