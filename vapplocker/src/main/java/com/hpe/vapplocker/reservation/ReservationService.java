package com.hpe.vapplocker.reservation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReservationService {

	public static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
	
	
	static class Info {
		String name; // holder information
		int id; // current holder id
		Semaphore semaphore = new Semaphore(1);
		
		public Info() 
		{
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getId()
		{
			return id;
		}
		
		public boolean tryReservation(long willingToWaitSecs, String name, int id) 
		{
			try {
				if (semaphore.tryAcquire(15L, TimeUnit.SECONDS)) {
					this.name = name;
					this.id = id;
					System.out.println("*** reserved for: "+name+", id="+id);
					return true;
				}
				else {
					System.out.println("*** unable to reserve: "+name+", id="+id);
					return false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		public synchronized boolean releaseReservation(int id)
		{
			if (this.id != id) {
				System.out.println("You are not the holder of this reservation: this="+this.id+" you="+id);
				return false;
			}
			
			System.out.println("*** released for: "+name+", id="+id);
			
			this.name = ""; 
			this.id = -1;
			semaphore.release();
			return true;
		}
	}
	
	Map<String, Info> domainInfos = new HashMap<>();
	
	int id = 0; // TODO: Guids
	
	public int reserve(String domain, String name)
	{
		Info info = null;
		synchronized (domainInfos) {
			info = domainInfos.get(domain);
			if (info == null) {
				info = new Info();
				domainInfos.put(domain, info);
			}
		}
				
	
		if (info.tryReservation(15L, name, id)) {
			return id++;
		}
		
		return -1;
	}
	
	public boolean release(String domain, int id)
	{
		Info info = null;
		synchronized (domainInfos) {
			info = domainInfos.get(domain);
			if (info == null) {
				return false;
			}	
		}
		
		return info.releaseReservation(id);
	}
	
}
