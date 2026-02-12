package ddwu.com.mobile.wearly_frontend.codiDiary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient
import kotlinx.coroutines.launch

class CodiDiaryViewModel : ViewModel() {
    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    fun saveDiary(request: CodiDiaryRecordRequest) {
        viewModelScope.launch {
            try {
                val response = CodiCalendarRetrofitClient.codiDiaryService.saveWearRecord(
                    true,
                    request
                )

                _saveStatus.value = response.success

            } catch (e: Exception) {
                Log.e("CodiDiaryVM", "저장 실패: ${e.message}")
                _saveStatus.value = false
            }
        }
    }

    class CodiDiaryViewModel : ViewModel() {
        private val _selectedDiary = MutableLiveData<WearRecordDetail?>()
        val selectedDiary: LiveData<WearRecordDetail?> = _selectedDiary

        fun fetchDiaryDetail(token: String, date: String) {
            viewModelScope.launch {
                try {
                    val response = CodiCalendarRetrofitClient.codiDiaryService.getWearRecords(
                        token = "Bearer $token",
                        date = date
                    )

                    if (response.success && !response.data.isNullOrEmpty()) {
                        _selectedDiary.value = response.data[0]
                    } else {
                        _selectedDiary.value = null
                    }
                } catch (e: Exception) {
                    Log.e("CodiDiary", "조회 실패: ${e.message}")
                    _selectedDiary.value = null
                }
            }
        }
    }
}