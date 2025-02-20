package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.enumeration.Role;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.User;
import hr.tvz.productpricemonitoringtool.util.Hash;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * UserFileRepository class.
 * Repository class for User.
 * Contains methods for finding by id, finding all, saving, updating and deleting users.
 */
public class UserFileRepository extends AbstractRepository<User> {

    private static final String FILE_PATH = "files/dat/users.txt";
    private static final Integer USER_DATA_SIZE = 6;
    private static final Logger log = LoggerFactory.getLogger(UserFileRepository.class);

    private final CompanyRepository companyRepository = new CompanyRepository();

    /**
     * Find user by id.
     * @param id User id.
     */
    @Override
    public Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    /**
     * Find all users.
     */
    @Override
    public Set<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Stream<String> stream = Files.lines(Path.of(FILE_PATH))) {
            List<String> fileRows = stream.toList();

            while(!fileRows.isEmpty()) {
                Long id = Long.parseLong(fileRows.getFirst());
                String name = fileRows.get(1);
                String surname = fileRows.get(2);
                String email = fileRows.get(3);
                String password = fileRows.get(4);
                Role role = Role.getRole(fileRows.get(5));

                users.add(new User.Builder(id, name)
                        .surname(surname)
                        .email(email)
                        .password(password)
                        .role(role)
                        .companies(companyRepository.findAllByUserId(id))
                        .build());

                fileRows = fileRows.subList(USER_DATA_SIZE, fileRows.size());
            }
        } catch (IOException | DatabaseConnectionActiveException e) {
            log.error("Failed to read users from file.", e);
            throw new RepositoryAccessException("Failed to read users from file.");
        }

        return users;
    }

    /**
     * Save user.
     * @param entities Users.
     */
    @Override
    public Set<User> save(Set<User> entities) {
        Set<User> allUsers = findAll();

        entities.forEach(user -> {
            user.setId(findNextId());
            user.setPassword(Hash.hashPassword(user.getPassword()));
        });

        allUsers.addAll(entities);
        writeUsersToFile(allUsers);

        return entities;
    }

    /**
     * Find user by email and password.
     * @param email User email.
     * @param password User password.
     */
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return findAll().stream()
                .filter(u -> u.getEmail().equals(email) && BCrypt.checkpw(password, u.getPassword()))
                .findFirst();
    }

    /**
     * Find user by email and password without companies.
     */
    public void update(User user) {
        Set<User> allUsers = findAll();

        allUsers.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setName(user.getName());
                    u.setSurname(user.getSurname());
                    u.setEmail(user.getEmail());
                    u.setPassword(user.getPassword());
                    u.setRole(user.getRole());
                    u.setCompanies(user.getCompanies());
                });

        writeUsersToFile(allUsers);
    }

    /**
     * Update user password.
     * @param user User.
     */
    public void updatePassword(User user) {
        Set<User> allUsers = findAll();

        allUsers.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> u.setPassword(Hash.hashPassword(user.getPassword())));

        writeUsersToFile(allUsers);
    }

    /**
     * Write users to file.
     * @param users Users.
     */
    private void writeUsersToFile(Set<User> users) {
        try {
            Files.write(Path.of(FILE_PATH), users.stream()
                    .map(user -> user.getId().toString() + "\n" +
                            user.getName() + "\n" +
                            user.getSurname() + "\n" +
                            user.getEmail() + "\n" +
                            user.getPassword() + "\n" +
                            user.getRole().name())
                    .toList());
        } catch (IOException e) {
            log.error("Failed to write users to file.", e);
            throw new RepositoryAccessException("Failed to write users to file.");
        }
    }

    /**
     * Find user by email.
     * @param email User email.
     */
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Find next id.
     */
    private Long findNextId() {
        return findAll().stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    /**
     * Delete user.
     * @param user User.
     */
    public void delete(User user) {
        Set<User> allUsers = findAll();
        allUsers.removeIf(u -> u.getId().equals(user.getId()));
        writeUsersToFile(allUsers);
    }
}
