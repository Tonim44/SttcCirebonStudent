package id.ac.sttccirebon.student.ui.tugas

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import id.ac.sttccirebon.student.databinding.FragmentTugasBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialogFragment

class TugasFragment : Fragment() {

private var _binding: FragmentTugasBinding? = null

  private val binding get() = _binding!!

  @SuppressLint("SuspiciousIndentation")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentTugasBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val loading = LoadingDialogFragment(this)
    loading.startLoading()

      Toast.makeText(this.context, "Masih tahap pengembangan", Toast.LENGTH_SHORT).show()

    loading.isDismiss()

    return root
  }

    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}