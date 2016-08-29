Konvensi penamaan file 
######################
Dalam folder ini digunakan beberapa file, masing-masing file disertai angka (0,1,2,3) yang menunjukan nomor iterasi (4 cross validation):

1. *.traning		: Input untuk Conditional Random Field (plain text)
2. *.model			: Hasil learning berupa model Conditional Random Field dari mallet (binary)
3. *.untagged		: Input untuk melakukan test (telah dihilangkan labelnya) (plain text)
4. *.result			: Hasil prediksi model terhadap file *.untagged (plain text)
5. *.gold_standard 	: Label yang seharusnya diprediksi model, digunakan untuk menghitung confussion matrix (plain text)

Diluar file utama tersebut Terdapat beberapa file lain yaitu:
Description.txt 			: Berisi penjelasan mengenai fitur apa yang digunakan dalam eksperimen (plain text)
rekap_experiment			: Hasil rekapitulasi experiment berisi confussion matrix dan rata-rata akurasi sistem (plain text)
rekap_failed_to_recognize 	: Berisi daftar rekapitulasi detail dari masing-masing confussion matrix untuk setiap  iterasi. (plain text)



Label yang digunakan
######################
B-Name			: Awal nama event
I-Name			: Inside nama event
B-Time			: Awal ekspresi waktu
I-Time			: Inside ekspresi waktu
B-Location		: Awal lokasi event
I-Location		: Inside lokasi event
O 				: Other



Deskripsi format yang digunakan untuk rekap_failed_to_recognize 
######################

Isi file rekap_failed_to_recognize merupakan pasangan token dan konteks token tersebut dalam twet dengan format ;

			<token>		<tweet lengkap berisi token dalam tweet tersebut>

setiap iterasi dipisahkan sebuah string dengan format :

			============= Iteration : <NOMOR_ITERASI> =============

Dan masing-masing detail kelompok token yang dilabeli serta prediksi sistem

			Supposed(Tagged As) : <NAMA_LABEL>(<NAMA_LABEL>)



Contoh Cara membaca file rekap_failed_to_recognize
######################
Perhatikan baris ke 5 di dalam file rekap_failed_to recognize
dalam baris tersebut ditemukan String berupa :

		SastraBulanPurnama				BESOK !! SastraBulanPurnama #49 ' JejakCintaDiJalanSunyi ' Kamis 29-10-15 pkl 19.30 di Amphitheater @RumahTembi https://t.co/7e2BgUbL5P 

Setelah diperiksa, string tersebut berada pada kelompok Supposed(Tagged As) : B-Name(B-Name) dengan kelompok utama iterasi 1
dengan kata lain, token "SastraBulanPurnama" memiliki label asli (Gold standard) "B-Name" dan berhasil dikenali oleh model sebagai "B-Name".


Contoh lain, misal ingin dicari "token-token yang memiliki label asli 'B-Name' namun sistem mengenali sebagai Other " 
maka cukup cari (menggunakan Ctrl+F) String "B-Name(O)" (tanpa tanda petik)
tepat di bawah string tersebut adalah kelompok token-token dengan label gold standard 'B-Name' yang diklasifikasikan sebagai 'Other'

Untuk memeriksa apakah token tersebut ada di dalam training data, berarti harus diperiksa terlebih dahulu file training / testing data yang bersesuaian
Misal dalam contoh diatas, token "SastraBulanPurnama" ada di dalam kelompok utama "Iterasi 0" Berarti bisa dibuka file *.training ke-0 dan 
dicari (Ctrl+F) apakah terdapat token "SastraBulanPurnama" di dalam training tersebut


Catatan akhir:
######################
 - Teks lebih nyaman dilihat dengan text-editor tanpa menggunakan fitur "Word wrap"
 - Teks lebih nyaman dilihat dengan text-editor dengan konfigurasi tab-size spacing width : 4 karakter