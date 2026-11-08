package com.example.plantcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE = "plants";
    public static final long DAY_MS = 86400000L;

    public DatabaseHelper(Context context) {
        super(context, "plantcare.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE plants ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "type TEXT, "
                + "location TEXT, "
                + "water_days INTEGER DEFAULT 3, "
                + "last_watered INTEGER DEFAULT 0, "
                + "notes TEXT)");
        seed(db, "Monstera", "Hias", "Ruang tamu", 3, "Simpan di tempat terang tidak terkena matahari langsung");
        seed(db, "Lidah Mertua", "Hias", "Kamar", 7, "Tahan kondisi minim cahaya");
        seed(db, "Pandan", "Herbal", "Dapur", 2, "Jaga media tanam tetap lembap");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE plants ADD COLUMN notes TEXT");
        }
    }

    private void seed(SQLiteDatabase db, String name, String type, String location, int days, String notes) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("type", type);
        v.put("location", location);
        v.put("water_days", days);
        v.put("last_watered", System.currentTimeMillis());
        v.put("notes", notes);
        db.insert(TABLE, null, v);
    }

    public long addPlant(String name, String type, String location, int days, String notes) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("type", type);
        v.put("location", location);
        v.put("water_days", days);
        v.put("last_watered", System.currentTimeMillis());
        v.put("notes", notes);
        return getWritableDatabase().insert(TABLE, null, v);
    }

    /** Semua tanaman, urut nama. Jika query tidak kosong, cari di nama/jenis/lokasi. */
    public Cursor getPlants(String query) {
        SQLiteDatabase d = getReadableDatabase();
        if (query == null || query.trim().isEmpty()) {
            return d.rawQuery("SELECT * FROM plants ORDER BY name COLLATE NOCASE", null);
        }
        String like = "%" + query.trim() + "%";
        return d.rawQuery("SELECT * FROM plants WHERE name LIKE ? OR type LIKE ? OR location LIKE ? "
                + "ORDER BY name COLLATE NOCASE", new String[]{like, like, like});
    }

    /** Tanaman yang sudah waktunya disiram (paling terlambat di atas). */
    public Cursor getDuePlants() {
        long now = System.currentTimeMillis();
        return getReadableDatabase().rawQuery("SELECT * FROM plants WHERE last_watered + water_days * "
                + DAY_MS + " <= " + now + " ORDER BY last_watered + water_days * " + DAY_MS, null);
    }

    /** Semua tanaman, diurutkan dari jadwal siram paling dekat. */
    public Cursor getPlantsBySchedule() {
        return getReadableDatabase().rawQuery("SELECT * FROM plants ORDER BY last_watered + water_days * "
                + DAY_MS, null);
    }

    public boolean deletePlant(long id) {
        return getWritableDatabase().delete(TABLE, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public void waterPlant(long id) {
        ContentValues v = new ContentValues();
        v.put("last_watered", System.currentTimeMillis());
        getWritableDatabase().update(TABLE, v, "id=?", new String[]{String.valueOf(id)});
    }

    public int countPlants() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM plants", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public int countDue() {
        long now = System.currentTimeMillis();
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM plants WHERE last_watered + water_days * "
                + DAY_MS + " <= " + now, null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }
}
