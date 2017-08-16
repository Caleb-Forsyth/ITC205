package bcccp.carpark;

import java.util.List;

import bcccp.tickets.adhoc.IAdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.ISeasonTicketDAO;

public class Carpark implements ICarpark {
	
	private List<ICarparkObserver> observers;
	private String carparkId;
	private int capacity;
	private int numberOfCarsParked; //will be replaced by counting tickets
	private IAdhocTicketDAO adhocTicketDAO;
	private ISeasonTicketDAO seasonTicketDAO;
	
	private String name;
	
	public Carpark(String name, int capacity, 
			IAdhocTicketDAO adhocTicketDAO, 
			ISeasonTicketDAO seasonTicketDAO) {
		//TODO Implement constructor
		this.name = name;
		
		this.capacity = capacity;
		this.adhocTicketDAO = adhocTicketDAO;
		this.seasonTicketDAO = seasonTicketDAO;
		
	}



	@Override
	public void register(ICarparkObserver observer) {
		// TODO Auto-generated method stub
		if(!observers.contains(observer)){
			observers.add(observer);
		}
		
		
	}



	@Override
	public void deregister(ICarparkObserver observer) {
		// TODO Auto-generated method stub
		if(observers.contains(observer)){
			observers.remove(observer);
		}
		
	}



	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}



	@Override
	public boolean isFull() {
		// TODO Auto-generated method stub
		if(this.numberOfCarsParked<this.capacity){
			return false;			
		}
		return true;
	}



	@Override
	public IAdhocTicket issueAdhocTicket() {
		// TODO Auto-generated method stub
		
		return this.adhocTicketDAO.createTicket(this.carparkId);
	}



	@Override
	public void recordAdhocTicketEntry() {
		// TODO Auto-generated method stub
		this.numberOfCarsParked++;
		
	}



	@Override
	public IAdhocTicket getAdhocTicket(String barcode) {
		// TODO Auto-generated method stub
		return this.adhocTicketDAO.findTicketByBarcode(barcode);
	}



	@Override
	public float calculateAddHocTicketCharge(long entryDateTime) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void recordAdhocTicketExit() {
		// TODO Auto-generated method stub
		
		this.numberOfCarsParked--;
		
	}



	@Override
	public void registerSeasonTicket(ISeasonTicket seasonTicket) {
		// TODO Auto-generated method stub
		this.seasonTicketDAO.registerTicket(seasonTicket);
	}



	@Override
	public void deregisterSeasonTicket(ISeasonTicket seasonTicket) {
		// TODO Auto-generated method stub
		this.seasonTicketDAO.deregisterTicket(seasonTicket);
		
	}



	@Override
	public boolean isSeasonTicketValid(String ticketId) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean isSeasonTicketInUse(String ticketId) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void recordSeasonTicketEntry(String ticketId) {
		// TODO Auto-generated method stub
		this.seasonTicketDAO.recordTicketEntry(ticketId);
	}



	@Override
	public void recordSeasonTicketExit(String ticketId) {
		// TODO Auto-generated method stub
		this.seasonTicketDAO.recordTicketExit(ticketId);
		
	}

	
	

}
