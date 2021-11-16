package id.backend.backend.models.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.backend.backend.models.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{
  User findByUsername(String username);
  void deleteByUsername(String username);

  @Modifying
  @Query("update User u set u.phoneNumber = :phoneNumber where u.username = :username")
  void setPhoneNumberUser(@Param(value = "phoneNumber") String phoneNumber, @Param(value = "username") String username);

  

  

  // @Query("update user u set u.phoneNumber = :phoneNumber where u.username = :username")
  // public User setPhoneNumberUser(@PathParam("phoneNumber") String phoneNumber,@PathParam("username") String username);
}
