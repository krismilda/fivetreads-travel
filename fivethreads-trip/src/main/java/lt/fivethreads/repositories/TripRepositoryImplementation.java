package lt.fivethreads.repositories;

import lt.fivethreads.entities.Trip;
import lt.fivethreads.entities.TripMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class TripRepositoryImplementation implements TripRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private CarTicketRepository carTicketRepository;

    public Trip findByID(long id) {
        return em.find(Trip.class, id);
    }

    public List<Trip> getAll() {
        return em.createNamedQuery("Trip.findAll", Trip.class).getResultList();
    }


    public void createTrip(Trip trip) {
        for (TripMember tripMember : trip.getTripMembers()
        ) {
            tripMemberRepository.saveTripMember(tripMember);
            if(tripMember.getCarTicket()!=null){
                em.persist(tripMember.getCarTicket());
            }
            if(tripMember.getTripAccommodation()!=null){
                em.persist(tripMember.getTripAccommodation());
            }
        }
        em.persist(trip);
    }

    public void updateTrip(Trip trip) {
        em.merge(trip);
    }

    public List<Trip> getAllByOrganizerEmail(String email) {
        return em.createNamedQuery("Trip.findByOrganizer", Trip.class)
                .setParameter("organizer_email", email)
                .getResultList();
    }

    public List<Trip> getAllByUserEmail(String email) {
        return em.createNamedQuery("Trip.findUserEmail", Trip.class)
                .setParameter("user_email", email)
                .getResultList();
    }

    public void deleteTrip(Trip trip) {
        for (TripMember tripMember : trip.getTripMembers()
        ) {
            tripMemberRepository.deleteTripMember(tripMember);
        }
        em.remove(trip);
    }

    public  void combineTrips(Trip newTrip, Trip trip1, Trip trip2){
        createTrip(newTrip);
        deleteTrip(trip1);
        deleteTrip(trip2);
    }
}
