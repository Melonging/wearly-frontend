package ddwu.com.mobile.wearly_frontend.records.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.wearly_frontend.databinding.FragmentRecordBinding
import ddwu.com.mobile.wearly_frontend.records.data.repository.WearRecordRepository
import ddwu.com.mobile.wearly_frontend.records.ui.adapter.RecordsAdapter
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var adapter: RecordsAdapter

    private val repository by lazy {
        WearRecordRepository(ApiClient.recordsApi(requireContext()))
    }

    private val pending = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOutfits.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecordsAdapter(
            arrayListOf(),
            onItemClick = { /* 상세 이동 */ },
            onHeartClick = { item ->
                toggleHeartOptimistic(item.id)
            }
        )

        binding.rvOutfits.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            val list = repository.fetchWearRecords()
            adapter.submit(list)
        }

    }

    private fun toggleHeartOptimistic(dateId: Long) {

        if (!pending.add(dateId)) return

        val current = adapter.getItemById(dateId) ?: return
        val before = current.isHeart
        val after = !before

        adapter.updateHeart(dateId, after)

        viewLifecycleOwner.lifecycleScope.launch {

            val ok = runCatching {
                repository.updateHeart(dateId, after)
            }.getOrElse { false }

            if (!ok) {
                adapter.updateHeart(dateId, before)
                Toast.makeText(
                    requireContext(),
                    "좋아요 변경 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }

            pending.remove(dateId)
        }
    }
}
