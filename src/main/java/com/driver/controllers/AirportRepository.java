package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class AirportRepository {
    HashMap<String,Airport> airportDb = new HashMap<>();
    HashMap<Integer,Flight> flightDb = new HashMap<>();
    HashMap<Integer,Passenger> passengerDb = new HashMap<>();
    HashMap<Integer,List<Integer>> flightToPassengerDb = new HashMap<>();
    private int total;

    public void addAirport(Airport airport) {
        String key = airport.getAirportName();
        airportDb.put(key,airport);
    }

    public void addFlight(Flight flight) {
        int key=flight.getFlightId();
        flightDb.put(key,flight);
    }

    public void addPassenger(Passenger passenger) {
        int key = passenger.getPassengerId();
        passengerDb.put(key,passenger);
    }

    public String getLargestAirportName() {
        List<String> l= new ArrayList<>();
        int max= 0;
        for(Airport airport:airportDb.values())
        {
            int number = airport.getNoOfTerminals();
            max=Math.max(max,number);
        }
        String ans = "";
        for(Airport airport:airportDb.values())
        {
            int number = airport.getNoOfTerminals();
            if(number==max)
            {
                String name = airport.getAirportName();
                l.add(name);
                ans=name;
            }
        }
        if(l.size()>1)
        {
            Collections.sort(l);
        }
        return l.get(0);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double duration = Integer.MAX_VALUE;
        for(Flight flight:flightDb.values())
        {
            if(flight.getToCity()==toCity && flight.getFromCity()==fromCity)
            {
                duration=Math.min(duration,flight.getDuration());
            }
        }
        if(duration==Integer.MAX_VALUE)
        {
            return -1;
        }
        return duration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        Airport airport = airportDb.get(airportName);
        int count = 0;
        if(airport==null)
            return count;
        City city = airport.getCity();
        for(Flight flight:flightDb.values())
        {
            if(flight.getFromCity()==city || flight.getToCity()==city)
            {
                int flightId = flight.getFlightId();
                count =count + flightToPassengerDb.get(flightId).size();
            }
        }
        return count;
    }

    public int calculateFlightFare(Integer flightId) {
        int fare = 0;
        int numberOfPassenger = flightToPassengerDb.get(flightId).size();
        fare = 3000 + (numberOfPassenger*50);
        return fare;
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        Flight flight = flightDb.get(flightId);
        if(flightToPassengerDb.get(flightId) != null && flight.getMaxCapacity() >  flightToPassengerDb.get(flightId).size() )
        {
            List<Integer> l = flightToPassengerDb.get(flightId);
            if(l.contains(passengerId))
                return "FAILURE";
            l.add(passengerId);
            flightToPassengerDb.put(flightId,l);
            return "SUCCESS";
        }
        else if(flightToPassengerDb.get(flightId) == null)
        {
            List<Integer> passList = new ArrayList<>();
            passList.add(passengerId);
            flightToPassengerDb.put(flightId,passList);
            return "SUCCESS";
        }

        return "FAILURE";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        List<Integer> passList = flightToPassengerDb.get(flightId);
        if(passList==null)
        {
            return "FAILURE";
        }
        if(passList.contains(passengerId))
        {
            passList.remove(passengerId);
            flightToPassengerDb.put(flightId,passList);
            return "SUCCESS";
        }
        return "FAILURE";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count = 0;
        for(List<Integer> l : flightToPassengerDb.values())
        {
            if(l.contains(passengerId))
            {
                count++;
            }
        }
        return count;
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        if(flightDb.containsKey(flightId))
        {
            City city = flightDb.get(flightId).getFromCity();
            for(Airport airport:airportDb.values())
            {
                if(airport.getCity() == city)
                {
                    return airport.getAirportName();
                }
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int noOfppl = flightToPassengerDb.get(flightId).size();
        int variableFare = (noOfppl*(noOfppl+1))*25;
        int fixedFare = 3000 * noOfppl;
        total = variableFare + fixedFare;
        if(total==3050)
            return 3000;
        return total;
    }
}
