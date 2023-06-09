package id.ac.sttccirebon.student.ui.pembayaran

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.student.R

class PembayaranAdapter(val pembayaranList: List<Pembayaran>) : RecyclerView.Adapter<PembayaranAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pembayaran, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pembayaran = pembayaranList[position]
        holder.textViewNominal.text = pembayaran.nominal
        holder.textViewKetPembayaran.text = pembayaran.jenis_pembayaran

        if (pembayaran.status.equals("Lunas")){
            holder.textViewLunas.text = "Lunas"
        }else{
            holder.textViewBelumLunas.text = "Belum Lunas"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PembayaranActivity::class.java)
            intent.putExtra(PembayaranActivity.EXTRA_PEMBAYARAN, pembayaran)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pembayaranList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNominal = itemView.findViewById<TextView>(R.id.nominal_pembayaran)
        val textViewKetPembayaran = itemView.findViewById<TextView>(R.id.ket_pembayaran)
        val textViewLunas = itemView.findViewById<TextView>(R.id.lunas)
        val textViewBelumLunas = itemView.findViewById<TextView>(R.id.belum_lunas)

    }

}
