package com.miaxis.postal.data.dao;

import android.app.Application;

import com.miaxis.postal.data.converter.DateConverter;
import com.miaxis.postal.data.converter.OrderImageListConverter;
import com.miaxis.postal.data.converter.StringListConverter;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Config.class, Courier.class, IDCardRecord.class, Express.class, WarnLog.class, IDCard.class}, version = 18)
@TypeConverters({StringListConverter.class, OrderImageListConverter.class, DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DBName = "Postal.db";

    private static AppDatabase instance;

    public static AppDatabase getInstance() {
        return instance;
    }

    //should be init first
    public static void initDB(Application application) {
        instance = createDB(application);
    }

    private static AppDatabase createDB(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, DBName)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .addMigrations(MIGRATION_9_10)
                .fallbackToDestructiveMigration()
                .build();
    }

    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Express ADD COLUMN phone TEXT");
        }
    };

    public abstract ConfigDao configDao();

    public abstract CourierDao courierDao();

    public abstract IDCardRecordDao idCardRecordDao();

    public abstract ExpressDao expressDao();

    public abstract WarnLogDao warnLogDao();

    public abstract IDCardDao idCardDao();

}
