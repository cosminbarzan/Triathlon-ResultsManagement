package triathlon.persistence;

import triathlon.model.User;

public interface UserRepository extends Repository<Integer, User>{
    Integer verifyAccount(String username, String password);
}