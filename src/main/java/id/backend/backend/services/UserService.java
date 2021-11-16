package id.backend.backend.services;

import java.util.List;

import id.backend.backend.models.entities.Role;
import id.backend.backend.models.entities.User;

public interface UserService {
  User saveUser(User user);
  Role saveRole(Role role);
  void addRoleToUser(String username, String roleName);
  User getUser(String username);
  void removeOne(String username);
  void updatePhoneNumberUser(String phoneNumber, String username);
  List<User>getUsers();
}
