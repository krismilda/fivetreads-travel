package lt.fivethreads.repositories;

import lt.fivethreads.entities.Office;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class OfficeRepositoryImpl implements  OfficeRepository {

    @PersistenceContext
    EntityManager em;

    public Office findById(long id) {
        return em.find(Office.class, id);
    }

    public List<Office> getAll() {
        return em.createNamedQuery("Office.FindAll",
                Office.class).getResultList();
    }

    public void deleteOffice(long id) {
        Office office_to_delete = em.find(Office.class, id);
        em.remove(office_to_delete);
    }

    public Office updateOffice(Office office) {
        return em.merge(office);
    }

    public Office createOffice(Office office) {
        em.persist(office);
        em.flush();
        return office;
    }

    public Boolean existsByAddressAndName(String address, String name) {
        return em.createNamedQuery("Office.ExistsByAddressAndName")
                .setParameter("address", address)
                .setParameter("name", name)
                .getResultList().size() == 1;
    }

    public Office findByName(String name) {
        return em.find(Office.class, name);
    }

    public List<Office> getOfficesWithUnoccupiedRooms(Date startDate, Date finishDate) {
        return em.createNamedQuery("Office.FindUnoccupiedAccommodationOffices", Office.class)
                .setParameter("startDate", startDate)
                .setParameter("finishDate", finishDate)
                .getResultList();
    }
}
