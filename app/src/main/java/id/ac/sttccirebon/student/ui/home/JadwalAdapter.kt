package id.ac.sttccirebon.student.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.student.R
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.isikehadiran.Absen
import id.ac.sttccirebon.student.ui.isikehadiran.DetailAlpaActivity
import id.ac.sttccirebon.student.ui.isikehadiran.DetailHadirActivity
import id.ac.sttccirebon.student.ui.isikehadiran.IsiKehadiranActvity

class JadwalAdapter(val absenList: ArrayList<Absen>) : RecyclerView.Adapter<JadwalAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwalharini, parent, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = DataManager(holder.itemView.context)
        val absen = absenList[position]
        holder.textViewJam.text = "${absen.jammulai}-${absen.jamselesai}"
        holder.textViewMatkul.text = absen.matakuliah
        holder.textViewDosen.text = absen.namadosen

        if (absen.status.equals("Alpa")){
            holder.textViewAlpa.text=absen.status
        }

        if (absen.status.equals("Hadir")){
            holder.textViewHadir.text=absen.status
        }

        if (absen.status.equals("Belum isi kehadiran")){
            holder.textViewBelumIsi.text=absen.status
        }

        if (absen.status.equals("Belum dimulai")) {
            holder.textViewBelumAda.text = absen.status
        }

        if (absen.jenis_pertemuan.equals("Offline")) {
           holder.iconOnline.visibility = View.GONE
            holder.textJenisPertemuan.text=absen.jenis_pertemuan
        }else{
            holder.iconOffline.visibility = View.GONE
            holder.textJenisPertemuan.text=absen.jenis_pertemuan
        }


        holder.itemView.setOnClickListener {

            if (absen.status.equals("Belum isi kehadiran")) {
                val intent = Intent(holder.itemView.context, IsiKehadiranActvity::class.java)
                intent.putExtra(IsiKehadiranActvity.EXTRA_ABSEN, absen)
                holder.itemView.context.startActivity(intent)
            }

            if (absen.status.equals("Hadir")) {
                val intent = Intent(holder.itemView.context, DetailHadirActivity::class.java)
                intent.putExtra(DetailHadirActivity.EXTRA_ABSEN, absen)
                holder.itemView.context.startActivity(intent)
            }

            if (absen.status.equals("Alpa")) {
                val intent = Intent(holder.itemView.context, DetailAlpaActivity::class.java)
                intent.putExtra(DetailAlpaActivity.EXTRA_ABSEN, absen)
                holder.itemView.context.startActivity(intent)
            }else {
                 }
        }
    }

    override fun getItemCount(): Int {
        return absenList.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMatkul = itemView.findViewById<TextView>(R.id.list_mata_kuliah)
        val textViewDosen = itemView.findViewById<TextView>(R.id.list_nama_dosen)
        val textViewJam = itemView.findViewById<TextView>(R.id.jam)
        val textViewAlpa = itemView.findViewById<TextView>(R.id.alpa)
        val textViewHadir = itemView.findViewById<TextView>(R.id.hadir)
        val textViewBelumIsi = itemView.findViewById<TextView>(R.id.belumisikehadiran)
        val textViewBelumAda = itemView.findViewById<TextView>(R.id.belumdimulai)

        val iconOffline = itemView.findViewById<ImageView>(R.id.icon_offline)
        val iconOnline = itemView.findViewById<ImageView>(R.id.icon_online)
        val textJenisPertemuan = itemView.findViewById<TextView>(R.id.jenis_pertemuan)
    }

}