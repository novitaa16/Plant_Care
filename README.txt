PLANTCARE FITUR LENGKAP - ANDROID STUDIO (JAVA)  v2.1

Aplikasi Android native (Java + SQLite) untuk mencatat dan merawat tanaman.

CARA MEMBUKA
1. Ekstrak ZIP ini ke folder baru (lebih baik hapus folder project lama dulu,
   supaya sisa cache .gradle / .idea yang rusak tidak ikut terbawa).
2. Android Studio > File > Open > pilih folder PlantCare_Fitur_Lengkap
   (folder yang berisi settings.gradle).
3. Tunggu Gradle Sync. Jika diminta, install Android SDK Platform 35 (klik Install).
4. Pastikan Gradle JDK = 17:
   Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK.
5. Sambungkan HP (USB debugging aktif, mode USB = Transfer file) atau pilih emulator.
6. Pilih perangkat di toolbar, lalu tekan Run (segitiga hijau).

FITUR
- Beranda: sapaan pemilik, jumlah tanaman, jumlah yang perlu disiram, daftar perawatan hari ini.
- Koleksi tanaman dengan pencarian nama / jenis / lokasi (kolom cari tidak lagi kehilangan fokus).
- Tambah tanaman (nama wajib diisi, interval siram 1-60 hari, catatan).
- Tandai sudah disiram; tanggal penyiraman terakhir dan sisa hari ditampilkan.
- Halaman Jadwal diurutkan dari tanaman yang paling dulu perlu disiram.
- Tips perawatan, profil pemilik (SharedPreferences), data tanaman (SQLite).

PERBAIKAN DI VERSI 2.1
- Memperbaiki error compile (setCornerRadius pada LinearLayout).
- Kartu hero di Beranda sebelumnya tertimpa warna putih sehingga teks tidak terbaca.
- Ukuran dan jarak memakai dp, jadi tampil proporsional di semua HP.
- Konten tidak lagi tertutup status bar / navigation bar di Android 15 (targetSdk 35).
- Query "perlu disiram" diperbaiki; filter pencarian tidak lagi ikut memengaruhi halaman Jadwal.
- Ditambah ikon aplikasi, gradle-wrapper.properties (Gradle 8.9), .gitignore, dan compileOptions Java 17.

CATATAN
- Tanpa login dan tanpa internet. Belum ada notifikasi otomatis.
- Butuh Android Studio yang mendukung AGP 8.6.1 dan JDK 17.
- Package: com.example.plantcare
