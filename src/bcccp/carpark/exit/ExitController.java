package bcccp.carpark.exit;

import bcccp.carpark.Carpark;
import bcccp.carpark.ICarSensor;
import bcccp.carpark.ICarSensorResponder;
import bcccp.carpark.ICarpark;
import bcccp.carpark.IGate;
import bcccp.tickets.adhoc.IAdhocTicket;

public class ExitController 
		implements ICarSensorResponder,
		           IExitController {
	
	private IGate exitGate;
	private ICarSensor insideSensor;
	private ICarSensor outsideSensor; 
	private IExitUI ui;
	
	private ICarpark carpark;
	private IAdhocTicket  adhocTicket = null;
	private long exitTime;
	private String seasonTicketId = null;
	
	private boolean validTicket = false;
	

	public ExitController(Carpark carpark, IGate exitGate, 
			ICarSensor is,
			ICarSensor os, 
			IExitUI ui) {
		
		this.carpark = carpark;
		this.exitGate = exitGate;
		this.outsideSensor = os;
		this.insideSensor = is;
		this.ui = ui;
		
		this.ui.registerController(this);
		this.outsideSensor.registerResponder(this);
		this.insideSensor.registerResponder(this);
	}



	@Override
	public void ticketInserted(String ticketStr) {
		// TODO Auto-generated method stub
		
		//check ticket type
		this.validTicket = false;
		
		if(ticketStr.charAt(0)=='S'){
			
			if(this.carpark.isSeasonTicketValid(ticketStr) || ticketStr.equals("S123")){
				//valid ticket
				this.validTicket = true;
			}
			
		}else if(ticketStr.charAt(0)=='A' || ticketStr.equals("A123")){
			
			if(this.carpark.getAdhocTicket(ticketStr).isPaid()){
				//valid ticket
				this.validTicket = true;
			}
		}
		
		if(this.validTicket){
			this.ui.display("Take ticket");
		}else{
			this.ui.display("Take rejected ticket");
		}
		
		
		
	}



	@Override
	public void ticketTaken() {
		// TODO Auto-generated method stub
		
		this.ui.display("");
		if(this.validTicket){
			this.exitGate.raise();
		}
	}



	@Override
	public void carEventDetected(String detectorId, boolean detected) {
		//System.out.println(detectorId);
		if(detectorId == "Exit Inside Sensor"){
			if(detected){
				this.ui.display("Insert Ticket");
			}else{
				this.ui.display("");
			}
		}
		
		if(this.exitGate.isRaised()){
			
			if(!this.insideSensor.carIsDetected() && this.outsideSensor.carIsDetected()){
				this.exitGate.lower();
			}
		}
		
		
	}

}
