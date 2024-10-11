import java.util.*;
import java.text.SimpleDateFormat;

// This class represents a Repository object. 
// A repository is a set of documents and their histories.
// Commits can be added, dropped, or synchronized with each other.
public class Repository {
    private Commit head;
    private String repoName;

     // Constructs  a new, empty repository with the given String name.
     // Throws IllegalArgumentException if the name is null or empty.
     public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.repoName = name;
        this.head = null; 
     }

     // Returns the ID of the current head of this repository.
     // If the head is null, returns null.
     public String getRepoHead() {
        if (head == null) {
            return null;
        } else {
            return head.id;
        }
     }

     // Returns the number of commits in the repository.
     public int getRepoSize() {
        Commit curr = head;
        int count = 0;
        while (curr != null) {
            count++;
            curr = curr.past;
        }
        return count;
     }

     // Returns a string representation of the repository.
     // If there are no commits, returns a message stating that there are no commits
     public String toString() {
        if (head == null) {
            return repoName + " - No commits";
        } else {
            return repoName + " - Current head: " + head.toString();
        }
     }

     // Takes in a String targetID and returns true if 
     // the commit with ID targetId is in the repository, false if not.
     public boolean contains(String targetId) {
        Commit curr = head;
        while (curr != null) {
            if (curr.id == targetId) {
                return true;
            } 
            curr = curr.past;
        }
        return false;
     }

     // Takes in a number (n) which represents how many recent commits will return. 
     // Returns a string consisting of the String representations of 
     // the most recent (n) commits in this repository, 
     // with the most recent first. 
     // The string representation consists of this commit's unique identifier, timestamp, 
     // and message, in the following form: "[identifier] at [timestamp]: [message]"
     // If n > size, all commits will be returned.
     // If there are no commits in the repository, returns an empty string.
     // Throws an IllegalArgumentException if (n) is non-positive.
     public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        String history = "";
        Commit current = head;
        int count = 0;

        while (current != null && count < n) {
            String message = current.toString();
            if (!history.isEmpty()) {
                history = history + "\n" + message;
            } else {
                history = message;
            }
            current = current.past;
            count++;
        }
        return history;
     }

     // Creates a new commit with the given String message parameter, 
     // and adds it to this repository.
     // The new commit becomes the new head of this repository, 
     // preserving the history behind it.
     // Returns the ID of the new commit.
     // Throws an IllegalArgumentException if the input message is empty. 
     public String commit(String message) {
        if (message.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Commit newCommit = new Commit(message, head);
        head = newCommit;
        return newCommit.id;
     }

     // Takes in a String targetId and removes the commit with ID targetId from this repository,
     // maintaining the rest of the history.
     // Returns true if the commit was successfully dropped, 
     // and false if there is no commit that matches the given ID in the repository.
     public boolean drop(String targetId) {
        if (head == null) {
            return false;
        }

        if (head.id.equals(targetId)) {
            head = head.past;
            return true;
        }
        Commit curr = head;
        while (curr.past != null) {
            if (targetId.equals(curr.past.id)) {
                curr.past = curr.past.past;
                return true;
            }
            curr = curr.past;
        }
        return false;
     }

     // Takes in another repository as a parameter, and
     // gets all the commits in the other repository and moves them into this repository, 
     // combining the two repository histories such that chronological order is preserved. 
     // After executing this method, this repository should contain all commits that 
     // were from this and other, and the commits should be ordered in timestamp order 
     // from most recent to least recent.
     public void synchronize(Repository other) {
        if (this.head == null) {
            this.head = other.head;
            other.head = null;
        } else if (!(other.head == null)) {
            Commit temp = head;
            while (temp.past != null) {
                temp = temp.past;
            }
            temp.past = other.head;
        }
        other.head = null;
     }

    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
