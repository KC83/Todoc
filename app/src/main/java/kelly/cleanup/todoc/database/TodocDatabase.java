package kelly.cleanup.todoc.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import kelly.cleanup.todoc.database.dao.ProjectDao;
import kelly.cleanup.todoc.database.dao.TaskDao;
import kelly.cleanup.todoc.model.Project;
import kelly.cleanup.todoc.model.Task;

@Database(entities = {Project.class, Task.class}, version = 2, exportSchema = false)
public abstract class TodocDatabase extends RoomDatabase {
    // SINGLETON
    private static volatile TodocDatabase INSTANCE;
    // DAO
    public abstract ProjectDao projectDao();
    public abstract TaskDao taskDao();

    // INSTANCE
    public static TodocDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TodocDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TodocDatabase.class, "Todoc.db")
                            .addCallback(addProjectsInDatabase())
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static Callback addProjectsInDatabase() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                for (Project project : Project.getAllProjects()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id",project.getId());
                    contentValues.put("name",project.getName());
                    contentValues.put("color",project.getColor());

                    db.insert("Project", OnConflictStrategy.IGNORE, contentValues);
                }

            }
        };
    }
}
