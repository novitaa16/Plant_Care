package com.example.plantcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int MATCH = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int WRAP = LinearLayout.LayoutParams.WRAP_CONTENT;

    private final int green = Color.rgb(42, 105, 61);
    private final int dark = Color.rgb(35, 54, 40);
    private final int muted = Color.rgb(105, 120, 108);
    private final int bg = Color.rgb(244, 248, 242);
    private final int warn = Color.rgb(180, 93, 35);

    private DatabaseHelper db;
    private PrefsManager prefs;
    private LinearLayout body;
    private String current = "Beranda";
    private String search = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        prefs = new PrefsManager(this);
        showPage("Beranda");
    }

    // ------------------------------------------------------------------ helpers

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private TextView text(String s, int sizeSp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sizeSp);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void padding(View v, int left, int top, int right, int bottom) {
        v.setPadding(dp(left), dp(top), dp(right), dp(bottom));
    }

    private void gap(LinearLayout parent, int heightDp) {
        parent.addView(new View(this), new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private TextView button(String label) {
        TextView t = text(label, 14, Color.WHITE, true);
        t.setGravity(Gravity.CENTER);
        padding(t, 18, 12, 18, 12);
        t.setBackground(shape(green, 24));
        return t;
    }

    private void card(LinearLayout parent, View child) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(MATCH, WRAP);
        p.bottomMargin = dp(12);
        child.setBackground(shape(Color.WHITE, 22));
        child.setElevation(dp(2));
        parent.addView(child, p);
    }

    private void title(String s) {
        TextView t = text(s, 18, dark, true);
        padding(t, 2, 14, 2, 10);
        body.addView(t);
    }

    private String str(Cursor c, String column) {
        String v = c.getString(c.getColumnIndexOrThrow(column));
        return v == null ? "" : v;
    }

    // --------------------------------------------------------------- page frame

    private void showPage(String page) {
        current = page;

        LinearLayout root = column();
        root.setBackgroundColor(bg);
        // Android 15 menggambar app sampai ke bawah status bar / nav bar, jadi beri jarak sendiri.
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(root);

        LinearLayout header = column();
        padding(header, 20, 18, 20, 10);
        LinearLayout top = row();
        top.addView(text("🌿 PlantCare", 23, green, true), new LinearLayout.LayoutParams(0, WRAP, 1));
        header.addView(top);
        String subtitle = page.equals("Beranda") ? "Rawat tanaman, mulai dari hal kecil 🌱" : page;
        header.addView(text(subtitle, 14, muted, false));
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        body = column();
        padding(body, 18, 6, 18, 18);
        scroll.addView(body);
        root.addView(scroll, new LinearLayout.LayoutParams(MATCH, 0, 1));

        if (page.equals("Beranda")) home();
        else if (page.equals("Tanaman")) plants();
        else if (page.equals("Jadwal")) schedule();
        else if (page.equals("Tips")) tips();
        else settings();

        root.addView(buildNav());
    }

    private LinearLayout buildNav() {
        LinearLayout nav = row();
        nav.setBackgroundColor(Color.WHITE);
        padding(nav, 4, 8, 4, 8);
        String[] pages = {"Beranda", "Tanaman", "Jadwal", "Tips", "Profil"};
        String[] icons = {"⌂", "♧", "◷", "✿", "●"};
        for (int i = 0; i < pages.length; i++) {
            final String p = pages[i];
            boolean active = p.equals(current);
            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);
            padding(item, 4, 4, 4, 4);
            TextView icon = text(icons[i], 20, active ? green : muted, true);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(p, 11, active ? green : muted, true);
            label.setGravity(Gravity.CENTER);
            item.addView(icon);
            item.addView(label);
            item.setOnClickListener(v -> {
                if (!p.equals(current)) search = "";
                showPage(p);
            });
            nav.addView(item, new LinearLayout.LayoutParams(0, WRAP, 1));
        }
        return nav;
    }

    // -------------------------------------------------------------------- pages

    private void home() {
        LinearLayout hero = column();
        padding(hero, 20, 20, 20, 20);
        GradientDrawable heroBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.rgb(39, 104, 57), Color.rgb(95, 151, 91)});
        heroBg.setCornerRadius(dp(24));
        hero.setBackground(heroBg);
        hero.addView(text("Halo, " + prefs.getOwner() + "! 👋", 21, Color.WHITE, true));
        gap(hero, 8);
        hero.addView(text("Tanaman sehat dimulai dari perawatan rutin.", 14, Color.WHITE, false));
        gap(hero, 16);
        TextView add = button("+ Tambah tanaman");
        add.setTextColor(green);
        add.setBackground(shape(Color.WHITE, 22));
        add.setOnClickListener(v -> addPlantDialog());
        hero.addView(add, new LinearLayout.LayoutParams(WRAP, WRAP));
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(MATCH, WRAP);
        heroParams.bottomMargin = dp(12);
        body.addView(hero, heroParams);

        LinearLayout stats = row();
        LinearLayout.LayoutParams left = new LinearLayout.LayoutParams(0, WRAP, 1);
        left.rightMargin = dp(6);
        LinearLayout.LayoutParams right = new LinearLayout.LayoutParams(0, WRAP, 1);
        right.leftMargin = dp(6);
        stats.addView(stat("🌱", String.valueOf(db.countPlants()), "Tanaman"), left);
        stats.addView(stat("💧", String.valueOf(db.countDue()), "Perlu disiram"), right);
        body.addView(stats);

        title("Perawatan hari ini");
        Cursor due = db.getDuePlants();
        if (due.getCount() == 0) {
            body.addView(text("Semua tanaman sudah tercukupi airnya hari ini 🎉", 14, muted, false));
        }
        fillPlants(body, due);

        title("Menu cepat");
        LinearLayout quick = row();
        LinearLayout.LayoutParams q1 = new LinearLayout.LayoutParams(0, WRAP, 1);
        q1.rightMargin = dp(6);
        LinearLayout.LayoutParams q2 = new LinearLayout.LayoutParams(0, WRAP, 1);
        q2.leftMargin = dp(6);
        quick.addView(quickButton("＋ Tambah", () -> addPlantDialog()), q1);
        quick.addView(quickButton("📅 Jadwal", () -> showPage("Jadwal")), q2);
        body.addView(quick);
    }

    private View stat(String icon, String value, String label) {
        LinearLayout c = column();
        padding(c, 16, 16, 12, 16);
        c.setBackground(shape(Color.WHITE, 20));
        c.addView(text(icon, 22, green, true));
        c.addView(text(value, 24, dark, true));
        c.addView(text(label, 12, muted, false));
        return c;
    }

    private View quickButton(String label, Runnable action) {
        TextView t = text(label, 14, green, true);
        t.setGravity(Gravity.CENTER);
        padding(t, 10, 16, 10, 16);
        t.setBackground(shape(Color.WHITE, 18));
        t.setOnClickListener(v -> action.run());
        return t;
    }

    private void plants() {
        EditText input = new EditText(this);
        input.setSingleLine();
        input.setHint("Cari nama, jenis, atau lokasi tanaman");
        input.setTextSize(14);
        padding(input, 16, 10, 16, 10);
        input.setBackground(shape(Color.WHITE, 18));
        input.setText(search);
        body.addView(input);
        gap(body, 10);

        TextView add = button("+ Tambah tanaman");
        add.setOnClickListener(v -> addPlantDialog());
        LinearLayout actions = row();
        actions.addView(add);
        body.addView(actions);

        title("Koleksi tanaman");
        // Hanya daftar yang dimuat ulang saat mengetik, supaya kolom cari tidak kehilangan fokus.
        final LinearLayout list = column();
        body.addView(list);
        loadList(list);

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                search = s.toString();
                loadList(list);
            }
            @Override public void afterTextChanged(Editable e) { }
        });
    }

    private void loadList(LinearLayout list) {
        list.removeAllViews();
        Cursor c = db.getPlants(search);
        if (c.getCount() == 0) {
            String msg = search.trim().isEmpty()
                    ? "Belum ada tanaman. Tambahkan tanaman pertamamu 🌱"
                    : "Tidak ada tanaman yang cocok dengan pencarian.";
            list.addView(text(msg, 14, muted, false));
        }
        fillPlants(list, c);
    }

    private void schedule() {
        title("Jadwal penyiraman");
        body.addView(text("Tanaman diurutkan dari yang paling dulu perlu disiram. Tandai sudah disiram agar jadwal berikutnya dihitung ulang.", 14, muted, false));
        gap(body, 12);
        Cursor c = db.getPlantsBySchedule();
        if (c.getCount() == 0) {
            body.addView(text("Belum ada tanaman. Tambahkan dulu di menu Tanaman 🌱", 14, muted, false));
        }
        fillPlants(body, c);
        TextView note = text("Catatan: jadwal dihitung dari interval penyiraman tiap tanaman. Versi ini belum mengirim notifikasi otomatis.", 12, muted, false);
        padding(note, 8, 14, 8, 8);
        body.addView(note);
    }

    private void tips() {
        String[][] data = {
                {"💧 Penyiraman", "Periksa kelembapan tanah dengan jari sebelum menyiram. Jangan menyiram hanya karena jadwal jika tanah masih basah."},
                {"☀️ Pencahayaan", "Kenali kebutuhan cahaya setiap tanaman. Sebagian tanaman menyukai cahaya tidak langsung."},
                {"🪴 Media tanam", "Gunakan pot berlubang agar air tidak menggenang dan akar tidak mudah membusuk."},
                {"🍃 Daun sehat", "Bersihkan debu pada daun dan periksa bagian bawah daun untuk mendeteksi hama."},
                {"🌱 Pemupukan", "Gunakan pupuk sesuai jenis tanaman dan ikuti takaran pada kemasan."},
                {"✂️ Pemangkasan", "Buang daun kering atau rusak dengan alat yang bersih."}
        };
        title("Panduan merawat tanaman");
        for (String[] d : data) {
            LinearLayout box = column();
            padding(box, 16, 16, 16, 16);
            box.addView(text(d[0], 17, dark, true));
            gap(box, 6);
            box.addView(text(d[1], 14, muted, false));
            card(body, box);
        }
    }

    private void settings() {
        title("Profil pemilik");
        LinearLayout box = column();
        padding(box, 18, 18, 18, 18);
        box.addView(text("Nama yang ditampilkan di beranda", 14, muted, false));
        final EditText name = new EditText(this);
        String owner = prefs.getOwner();
        name.setText(owner.equals(PrefsManager.DEFAULT_OWNER) ? "" : owner);
        name.setHint("Masukkan nama kamu");
        box.addView(name);
        gap(box, 12);
        TextView save = button("Simpan profil");
        save.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            prefs.saveOwner(n.isEmpty() ? PrefsManager.DEFAULT_OWNER : n);
            Toast.makeText(this, "Profil disimpan", Toast.LENGTH_SHORT).show();
            showPage("Profil");
        });
        box.addView(save);
        card(body, box);

        title("Tentang PlantCare");
        body.addView(text("PlantCare membantu mencatat koleksi tanaman, lokasi, jenis, kebutuhan penyiraman, dan catatan perawatan. Data tersimpan di perangkat ini.", 14, muted, false));
        gap(body, 16);
        body.addView(text("Versi 2.1 • Dibuat untuk membantu perawatan tanaman sehari-hari 🌿", 12, muted, false));
    }

    // ------------------------------------------------------------- plant cards

    private void fillPlants(LinearLayout target, Cursor c) {
        SimpleDateFormat fmt = new SimpleDateFormat("d MMM yyyy", new Locale("id", "ID"));
        long now = System.currentTimeMillis();
        while (c.moveToNext()) {
            final long id = c.getLong(c.getColumnIndexOrThrow("id"));
            final String name = str(c, "name");
            String type = str(c, "type");
            String loc = str(c, "location");
            String notes = str(c, "notes");
            int days = c.getInt(c.getColumnIndexOrThrow("water_days"));
            long last = c.getLong(c.getColumnIndexOrThrow("last_watered"));

            long next = last + days * DatabaseHelper.DAY_MS;
            boolean due = now >= next;
            String statusText;
            if (due) {
                long late = (now - next) / DatabaseHelper.DAY_MS;
                statusText = late == 0 ? "Siram hari ini" : "Telat " + late + " hari";
            } else {
                long left = (long) Math.ceil((next - now) / (double) DatabaseHelper.DAY_MS);
                statusText = "Sehat • " + left + " hari lagi";
            }

            LinearLayout box = column();
            padding(box, 16, 14, 16, 14);

            LinearLayout top = row();
            top.addView(text("🪴", 27, green, true));
            LinearLayout info = column();
            padding(info, 12, 0, 0, 0);
            info.addView(text(name, 17, dark, true));
            info.addView(text(type + " • " + loc, 12, muted, false));
            top.addView(info, new LinearLayout.LayoutParams(0, WRAP, 1));
            top.addView(text(statusText, 11, due ? warn : green, true));
            box.addView(top);

            String detailText = "💧 Setiap " + days + " hari  •  Terakhir disiram " + fmt.format(new Date(last));
            if (!notes.isEmpty()) detailText += "\n📝 " + notes;
            TextView detail = text(detailText, 12, muted, false);
            padding(detail, 0, 10, 0, 8);
            box.addView(detail);

            LinearLayout buttons = row();
            TextView watered = button("✓ Sudah disiram");
            watered.setTextSize(12);
            watered.setOnClickListener(v -> {
                db.waterPlant(id);
                Toast.makeText(this, "Jadwal penyiraman diperbarui", Toast.LENGTH_SHORT).show();
                showPage(current);
            });
            buttons.addView(watered);

            TextView delete = text("Hapus", 12, Color.RED, true);
            padding(delete, 18, 12, 12, 12);
            delete.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Hapus tanaman?")
                    .setMessage("Data " + name + " akan dihapus.")
                    .setNegativeButton("Batal", null)
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        db.deletePlant(id);
                        showPage(current);
                    })
                    .show());
            buttons.addView(delete);
            box.addView(buttons);

            card(target, box);
        }
        c.close();
    }

    // ----------------------------------------------------------- add plant form

    private EditText field(LinearLayout form, String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        form.addView(e);
        return e;
    }

    private void addPlantDialog() {
        LinearLayout form = column();
        padding(form, 22, 8, 22, 0);
        final EditText name = field(form, "Nama tanaman (contoh: Monstera)");
        final EditText type = field(form, "Jenis (Hias / Buah / Sayur / Herbal)");
        final EditText loc = field(form, "Lokasi tanaman");
        final EditText days = field(form, "Interval siram dalam hari (contoh: 3)");
        days.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText notes = field(form, "Catatan perawatan (opsional)");

        ScrollView scroll = new ScrollView(this);
        scroll.addView(form);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Tambah tanaman baru")
                .setView(scroll)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();
        dialog.show();
        // Dipasang setelah show() supaya dialog tidak tertutup saat nama masih kosong.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            if (n.isEmpty()) {
                name.setError("Nama tanaman wajib diisi");
                return;
            }
            int interval = 3;
            try {
                interval = Integer.parseInt(days.getText().toString().trim());
                if (interval < 1 || interval > 60) interval = 3;
            } catch (NumberFormatException ignored) {
                interval = 3;
            }
            String t = type.getText().toString().trim();
            String l = loc.getText().toString().trim();
            db.addPlant(n, t.isEmpty() ? "Tanaman" : t, l.isEmpty() ? "Belum diatur" : l,
                    interval, notes.getText().toString().trim());
            search = "";
            dialog.dismiss();
            Toast.makeText(this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            showPage("Tanaman");
        });
    }
}
