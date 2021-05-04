package triathlon.persistence.repository.hbm;

import triathlon.model.User;
import triathlon.persistence.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class UserHbmRepository implements UserRepository {
    private SessionFactory sessionFactory;

    public UserHbmRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Integer verifyAccount(String username, String password) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();

            Query<User> query = session.createQuery("from User where username=? and password=?", User.class);
            query.setParameter(0, username);
            query.setParameter(1, password);

            User user = query.uniqueResult();
            session.getTransaction().commit();

            return user.getId();
        }
    }

    @Override
    public User findOne(Integer integer) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            User user = session.createQuery("from User where id=?", User.class).setParameter(0, integer).uniqueResult();
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public Iterable<User> findAll() {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            List<User> users = session.createQuery("from User", User.class).list();
            session.getTransaction().commit();
            return users;
        }
    }

    @Override
    public User save(User entity) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public User delete(Integer integer) {
        User user = findOne(integer);
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            return null;
        }
    }

    @Override
    public User update(User entity) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            User user = session.load(User.class, entity.getId());
            user.setFirstName(entity.getFirstName());
            user.setLastName(entity.getLastName());
            user.setUsername(entity.getUsername());
            user.setPassword(entity.getPassword());
            session.getTransaction().commit();
            return entity;
        }
    }
}
