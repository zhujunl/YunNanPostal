package com.miaxis.postal.data.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.miaxis.postal.data.converter.DateConverter;
import com.miaxis.postal.data.converter.StringListConverter;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.WarnLog;
import com.miaxis.postal.util.FileUtil;

import java.io.File;

@Database(entities = {Config.class, Courier.class, IDCardRecord.class, Express.class, WarnLog.class}, version = 1)
@TypeConverters({StringListConverter.class, DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DBName = FileUtil.MAIN_PATH + File.separator + "Postal.db";

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
                .fallbackToDestructiveMigration()
                .build();
    }

//    private static Migration MIGRATION_4_5 = new Migration(4, 5) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE Express ADD COLUMN addresseeName TEXT default ''");
//            database.execSQL("ALTER TABLE Express ADD COLUMN addresseePhone TEXT default ''");
//            database.execSQL("ALTER TABLE Express ADD COLUMN addresseeAddress TEXT default ''");
//        }
//    };

    public abstract ConfigDao configDao();

    public abstract CourierDao courierDao();

    public abstract IDCardRecordDao idCardRecordDao();

    public abstract ExpressDao expressDao();

    public abstract WarnLogDao warnLogDao();

}
