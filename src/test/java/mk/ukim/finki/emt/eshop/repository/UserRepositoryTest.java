package mk.ukim.finki.emt.eshop.repository;

import mk.ukim.finki.emt.eshop.model.User;
import mk.ukim.finki.emt.eshop.model.enumerations.Role;
import mk.ukim.finki.emt.eshop.model.exceptions.UserNotFoundException;
import mk.ukim.finki.emt.eshop.model.projections.UserProjection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("user");
            user.setName("user");
            user.setSurname("user");
            user.setRole(Role.ROLE_USER);

            User user1 = new User();
            user1.setUsername("user1");
            user1.setName("Name1");


            userRepository.save(user);
            userRepository.save(user1);
        }
    }

    @Test
    public void testFindAll() {
        List<User> userList = this.userRepository.findAll();
        Assert.assertEquals(2L, userList.size());
    }

    @Test
    public void testFetchAll() {
        List<User> userList = this.userRepository.fetchAll();
        Assert.assertEquals(2L, userList.size());
    }

    @Test
    public void testLoadAll() {
        List<User> userList = this.userRepository.loadAll();
        Assert.assertEquals(2L, userList.size());
    }

    @Test
    public void testProjectUsernameAndNameAndSurname() {
        UserProjection userProjection = this.userRepository.findByRole(Role.ROLE_USER);
        Assert.assertEquals("user", userProjection.getUsername());
        Assert.assertEquals("user", userProjection.getName());
        Assert.assertEquals("user", userProjection.getSurname());
    }

    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void testOptimisticLock() {
        User user1 = this.userRepository.findByUsername("user")
                .orElseThrow(() -> new UserNotFoundException("user"));
        User user2 = this.userRepository.findByUsername("user")
                .orElseThrow(() -> new UserNotFoundException("user"));

        user1.setName("user1");
        user2.setName("user2");

        this.userRepository.save(user1);
        this.userRepository.save(user2);
    }
}
