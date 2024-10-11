import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class RepositoryTest {
    private Repository repo1;
    private Repository repo2;

    @BeforeEach
    public void setUp() {
        repo1 = new Repository("repo1");
        repo2 = new Repository("repo2");
        Repository.Commit.resetIds();
    }

    @Test
    @DisplayName("Test getHistory()")
    public void getHistory() {
        // Initialize commit messages
        String[] commitMessages = new String[]{"Initial commit.", "Updated method documentation.",
                                                "Removed unnecessary object creation."};

        // Commit the commit messages to repo1
        for (int i = 0; i < commitMessages.length; i++) {
            String commitMessage = commitMessages[i];
            repo1.commit(commitMessage);

            // Assert that the current commit id is at the repository's head
            // We know our ids increment from 0, meaning we can just use i as our id
            assertEquals("" + i, repo1.getRepoHead());
        }

        assertEquals(repo1.getRepoSize(), commitMessages.length);

        // This is the method we are testing for. First, we'll obtain the 2 most recent commits
        // that have been made to repo1.
        String repositoryHistory = repo1.getHistory(2);
        String[] commits = repositoryHistory.split("\n");

        // Verify that getHistory() only returned 2 commits.
        assertEquals(commits.length, 2);

        // Verify that the 2 commits have the correct commit message and commit id
        for (int i = 0; i < commits.length; i++) {
            String commit = commits[i];

            // Old commit messages/ids are on the left and the more recent commit messages/ids are
            // on the right so need to traverse from right to left to ensure that 
            // getHistory() returned the 2 most recent commits.
            int backwardsIndex = (commitMessages.length - 1) - i;
            String commitMessage = commitMessages[backwardsIndex];

            assertTrue(commit.contains(commitMessage));
            assertTrue(commit.contains("" + backwardsIndex));
        }
    }

    @Test
    @DisplayName("Test drop() (empty case)")
    public void testDropEmpty() {
        assertFalse(repo1.drop("123"));
    }

    @Test
    @DisplayName("Test drop() (front case)")
    public void testDropFront() {
        assertEquals(repo1.getRepoSize(), 0);
        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Added unit tests."};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo2 - ID = "1"
        repo2.commit(commitMessages[1]);

        // Assert that repo1 successfully dropped "0"
        assertTrue(repo1.drop("0"));
        assertEquals(repo1.getRepoSize(), 0);
        
        // Assert that repo2 does not drop "0" but drops "1"
        // (Note that the commit ID increments regardless of the repository!)
        assertFalse(repo2.drop("0"));
        assertTrue(repo2.drop("1"));
        assertEquals(repo2.getRepoSize(), 0);
    }

    @Test
    public void testRepositoryEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Repository(""); 
        });
    }

    @Test
    public void testRepositoryNullName() {
        String nullName = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new Repository(nullName);
        });
    }

    @Test
    public void testGetRepoHeadNotEmpty() {
        Repository repository = new Repository("TestRepository");
        repository.commit("Commit 1");
        repository.commit("Commit 2");

        String head = repository.getRepoHead();
        assertNotNull(head);
    }

    @Test
    public void testGetRepoHeadEmpty() {
        Repository repository = new Repository("TestRepository");

        String head = repository.getRepoHead();
        assertNull(head); // The head should be null for an empty repository
    }

    public void testSizeEmptyRepository() {
        Repository repository = new Repository("TestRepository");
        int size = repository.getRepoSize();
        assertEquals(0, size);
    }

    public void testSizeWithCommits() {
        Repository repository = new Repository("TestRepository");
        repository.commit("Commit 1");
        repository.commit("Commit 2");
        repository.commit("Commit 3");

        assertEquals(3, repository.getRepoSize());
    }

    @Test
    public void testSizeAfterDrop() {
        Repository repository = new Repository("TestRepository");
        repository.commit("Commit 1");
        repository.commit("Commit 2");
        repository.commit("Commit 3");

        repository.drop(repository.getRepoHead());
        assertEquals(2, repository.getRepoSize());

        repository.drop(repository.getRepoHead());
        assertEquals(1, repository.getRepoSize());
    }

    @Test
    public void testContainsTrue() {
        Repository repository = new Repository("TestRepository");
        String targetId = repository.commit("Commit 1");
        repository.commit("Commit 2");
        repository.commit("Commit 3");

        boolean containsTarget = repository.contains(targetId);
        assertTrue(containsTarget);
    }

    @Test
    public void testContainsEmptyRepository() {
        Repository repository = new Repository("TestRepo");
        boolean containsTarget = repository.contains("SomeID");
        assertFalse(containsTarget);
    }

    @Test
    public void testGetHistoryCorrectly() {
        Repository repository = new Repository("TestRepository");
        repository.commit("Commit 1");
        repository.commit("Commit 2");
        repository.commit("Commit 3");
        repository.commit("Commit 4");

        String history = repository.getHistory(2);
        assertEquals(expectedHistory, "Commit 3 at [timestamp]: Commit 3\n" +
                                "Commit 2 at [timestamp]: Commit 2");
    }

    @Test
    public void testGetHistoryThrowExcpetion() {
        Repository repository = new Repository("TestRepository");
        assertThrows(IllegalArgumentException.class, () -> {
            repository.getHistory(-1);
        });
    }

    @Test
    public void testGetHistoryWithEmptyRepository() {
        Repository repository = new Repository("TestRepository");
        String history = repository.getHistory(1); 
        assertEquals("", history);
    }

    @Test
    public void testCommitValid() {
        Repository repository = new Repository("TestRepository");
        String commitId = repository.commit("Commit 1");

        assertNotNull(commitId);
    }

    @Test
    public void testCommitAndGetRepoHead() {
        Repository repository = new Repository("TestRepository");
        String commit1Id = repository.commit("Commit 1");
        String commit2Id = repository.commit("Commit 2");

        String repoHead = repository.getRepoHead();
        assertEquals(commit2Id, repoHead);
    }

    @Test
    public void testDropEmptyRepository() {
        Repository repository = new Repository("TestRepository");
        boolean result = repository.drop("SomeID");
        assertFalse(result);
    }

    @Test
    public void testSynchronize() {
        String ID1 = repo2.commit("Commit 1 in repo2");
        String ID2 = repo2.commit("Commit 2 in repo2");

        repo1.synchronize(repo2);

        assertEquals(commitId2, repo1.getRepoHead(), "Synchronize did not update repo1's head");

        assertEquals(null, repo2.getRepoHead(), "Synchronize did not clear repo2's head");
    }
}
