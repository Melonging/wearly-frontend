package ddwu.com.mobile.wearly_frontend.codiDiary.data

import CodiDiaryRecordRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient.codiDiaryService
import kotlinx.coroutines.launch

class CodiDiaryViewModel : ViewModel() {
    private val _diaryDateList = MutableLiveData<List<String>>()
    val diaryDateList: LiveData<List<String>> = _diaryDateList

    fun fetchDiaryDates(year: Int, month: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = CodiCalendarRetrofitClient.codiDiaryService.getWearRecordsDates("Bearer $token", year, month)
                if (response.isSuccessful && response.body() != null) {
                    _diaryDateList.value = response.body()?.data ?: emptyList()
                    Log.d("CodiDiaryVM", "기록 있는 날짜들: ${ _diaryDateList.value}")
                }
            } catch (e: Exception) {
                Log.e("CodiDiaryVM", "에러 발생: ${e.message}")
            }
        }
    }


    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    fun saveRecord(isWeatherLog: Boolean, request: CodiDiaryRecordRequest) {
        viewModelScope.launch {
            try {
                val response = codiDiaryService.postWearRecord(
                    "Bearer ${BuildConfig.TEST_API_TOKEN}",
                    isWeatherLog,
                    request
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _saveStatus.value = true

                } else {
                    _saveStatus.value = false
                    Log.e("CodiDiaryVM", "코드: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _saveStatus.value = false
                Log.e("CodiDiaryVM", e.message.toString())
            }
        }
    }

    private val _diaryReadData = MutableLiveData<CodiDiaryRead?>()
    val diaryReadData: LiveData<CodiDiaryRead?> = _diaryReadData

    fun fetchDiaryRead(date: String, token: String) {
        viewModelScope.launch {
            try {
                val response = CodiCalendarRetrofitClient.codiDiaryService.getWearRecord("Bearer $token", date)
                if (response.isSuccessful && response.body()?.success == true) {
                    _diaryReadData.value = response.body()?.data?.firstOrNull()
                    Log.d("CodiDiaryVM", "조회 성공: ${_diaryReadData.value}")
                } else {
                    _diaryReadData.value = null
                    Log.e("CodiDiaryVM", "조회 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _diaryReadData.value = null
                Log.e("CodiDiaryVM", "네트워크 에러: ${e.message}")
            }
        }
    }
}