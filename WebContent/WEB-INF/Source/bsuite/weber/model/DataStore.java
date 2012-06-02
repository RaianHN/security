package bsuite.weber.model;
import java.util.List;

/**
 * Interface for a CRUD-based Data Store
 */
public interface DataStore {
       /**
         * The method creates a new Actor instance
         *
         * @return new Actor
         */
        public Activity createActivity();

       /**
         * The method searches for an Actor by its ID in the data store
         *
         * @param id Actor ID
         * @return Actor or null
         */
        public Activity findActorById(String id);

       /**
         * Writes changes made to the specified Actor instance to the data store
         * to store them permanently.
         *
         * @param changedActor Changed Actor instance
         */
        public void updateActivity(Activity changedActivity);

       /**
         * The method delete an Actor from the data store
         *
         * @param id Actor ID
         */
        public void deleteActivity(String id);

       /**
         * The method returns a list of Actor objects, ordered by the lastname property.
         * This list cannot be modified. Use the DataStore methods instead.
         *
         * @return List
         */
        public List<Activity> getActivityByDate();
}

