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
		
		//Run Validate Ticket
		
	}



	@Override
	public void ticketTaken() {
		// TODO Auto-generated method stub
		
		this.ui.display("");
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
		if(detectorId == "Exit Outside Sensor"){
			this.exitGate.lower();
		}
		
	}

}
