package lt.fivethreads.validation;

import lt.fivethreads.entities.*;
import lt.fivethreads.entities.request.FullAddressDTO;
import lt.fivethreads.exception.WrongTripData;
import lt.fivethreads.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TripAccommodationValidation {

    public void checkFinishStartDates(Date startDate, Date finishDate, String message) {
        if (finishDate.compareTo(startDate) < 0) {
            throw new WrongTripData(message);
        }
    }
    public void checkStartDateToday(Date startDate) {
        Date today = new Date();
        if (startDate.compareTo(today) < 0) {
            throw new WrongTripData("Start date is earlier than today.");
        }
    }

    public void checkTripAccommodationDatesAgainstTripDates(Date startDate, Date finishDate, Trip trip){
        if(startDate.compareTo(trip.getStartDate()) < 0)
            throw new WrongTripData("Start Date is earlier than trip start date.");
        if (startDate.compareTo(trip.getFinishDate())  > 0)
            throw new WrongTripData("Start Date precedes trip start date.");
        if (finishDate.compareTo(trip.getFinishDate()) > 0)
            throw new WrongTripData("Finish Date precedes trip finish date.");
        if (finishDate.compareTo(trip.getStartDate()) < 0)
            throw new WrongTripData("Finish Date is earlier trip start date.");
    }

    public void hotelRequiredFields(String hotelName, FullAddressDTO hotelAddress, Double price){
        if(hotelName == null)
            throw new WrongTripData("Hotel Name cannot be null.");
        if(hotelAddress == null)
            throw new  WrongTripData("Hotel Address cannot be null.");
        if(price == null)
            throw  new  WrongTripData("Price cannot be null.");
        if(price <=0)
            throw new WrongTripData("Price cannot be negative");
    }


    public void isAccommodationAccepted(TripMember tripMember){
        if(tripMember.getTripAcceptance() == TripAcceptance.ACCEPTED && tripMember.getIsAccommodationNeeded())
            throw new WrongTripData(tripMember.getUser().getEmail()+" has not accepted accommodation");
    }

}
