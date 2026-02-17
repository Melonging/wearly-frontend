package ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ddwu.com.mobile.wearly_frontend.category.data.CategoryItem
import ddwu.com.mobile.wearly_frontend.codidiary.data.DiaryClothItem
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryRead
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryRecordRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.DiaryCategoryItem
import ddwu.com.mobile.wearly_frontend.codidiary.network.CodiCalendarRetrofitClient.codiDiaryService
import kotlinx.coroutines.launch

class CodiDiaryViewModel : ViewModel() {
    private val _diaryDateList = MutableLiveData<List<String>>()
    val diaryDateList: LiveData<List<String>> = _diaryDateList

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _diaryReadData = MutableLiveData<CodiDiaryRead?>()
    val diaryReadData: LiveData<CodiDiaryRead?> = _diaryReadData

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> = _deleteStatus

    private val _categoryList = MutableLiveData<List<DiaryCategoryItem>>()
    val categoryList: LiveData<List<DiaryCategoryItem>> = _categoryList

    private val _clothesList = MutableLiveData<List<DiaryClothItem>>()
    val clothesList: LiveData<List<DiaryClothItem>> = _clothesList

    private val clothesCache = mutableMapOf<Int, List<DiaryClothItem>>()

    val monthRecords = MutableLiveData<List<CodiDiaryRead>>()

    fun fetchMonthRecords(token: String, year: Int, month: Int) {
        viewModelScope.launch {
            try {
                Log.d("MonthRecords", "request year=$year month=$month tokenHead=${token.take(10)}")

                val res = codiDiaryService.getWearRecordsByMonth("Bearer $token", year, month)

                Log.d("MonthRecords", "http=${res.code()} isSuccessful=${res.isSuccessful}")
                Log.d("MonthRecords", "body=${res.body()}")
                Log.d("MonthRecords", "errorBody=${res.errorBody()?.string()}")

                val list = if (res.isSuccessful && res.body()?.success == true) {
                    res.body()?.data ?: emptyList()
                } else emptyList()

                Log.d("MonthRecords", "list.size=${list.size}")
                Log.d("MonthRecords", "wear_dates=${list.map { it.wear_date }.sorted()}")

                monthRecords.value = list
            } catch (e: Exception) {
                Log.e("MonthRecords", "exception=${e.message}", e)
                monthRecords.value = emptyList()
            }
        }
    }


    /**
     * 달력에 일기가 기록된 날짜 리스트를 받아오는 API 호출
     *
     * @param year 연도
     * @param month 월
     * @param token 토큰
     */
    fun fetchDiaryDates(year: Int, month: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = codiDiaryService.getWearRecordsDates("Bearer $token", year, month)
                if (response.isSuccessful && response.body() != null) {
                    _diaryDateList.value = response.body()?.data ?: emptyList()
                    Log.d("CodiDiaryVM", "기록 있는 날짜들: ${ _diaryDateList.value}")
                }
            } catch (e: Exception) {
                Log.e("CodiDiaryVM", "에러 발생: ${e.message}")
            }
        }
    }


    /**
     * 일기 저장 API를 호출하는 함수.
     *
     * @param isWeatherLog 일기
     * @param request 저장된 형식
     */
    fun saveRecord(token: String, isWeatherLog: Boolean, request: CodiDiaryRecordRequest) {


        viewModelScope.launch {
            try {
                val response = codiDiaryService.postWearRecord(
                    "Bearer $token",
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


    /**
     * 특정 날짜에 저장된 일기를 불러오는 API를 호출하는 함수.
     *
     * @param date 지정된 날짜
     * @param token 토큰
     */
    fun fetchDiaryRead(token: String, date: String) {
        viewModelScope.launch {
            try {
                val response = codiDiaryService.getWearRecord("Bearer $token", date)
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
    fun clearDiaryReadData() {
        _diaryReadData.value = null
    }



    /**
     * 일기 수정 API 호출 함수.
     *
     * @param token 토큰
     * @param dateId 날짜
     * @param request 수정 데이터
     */
    fun updateRecord(token: String, dateId: Int, request: CodiDiaryEditRequest) {
        viewModelScope.launch {
            try {
                val response = codiDiaryService.updateDiaryRecord("Bearer $token", dateId, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _updateStatus.postValue(true)
                } else {
                    _updateStatus.postValue(false)
                }
            } catch (e: Exception) {
                _updateStatus.postValue(false)
                Log.e("CodiDiaryViewModel", "Update Error: ${e.message}")
            }
        }
    }


    /**
     * 일기 삭제 API 호출 함수
     *
     * @param token 토큰
     * @param dateId 날짜
     */
    fun deleteRecord(token: String, dateId: Int) {
        viewModelScope.launch {
            try {
                val response = codiDiaryService.deleteWearRecord("Bearer $token", dateId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _diaryReadData.postValue(null)
                    _deleteStatus.postValue(true)

                    _deleteStatus.value = false
                } else {
                    _deleteStatus.postValue(false)
                }
            } catch (e: Exception) {
                _deleteStatus.postValue(false)
            }
        }
    }

    fun resetDeleteStatus() {
        _deleteStatus.value = false
    }





    /**
     * 옷 카테고리 목록 조회 API 호출 함수.
     *
     * @param token 토큰
     */
    fun fetchCategories(token: String) {
        if (!_categoryList.value.isNullOrEmpty()) {
            Log.d("CodiDiaryVM", "카테고리 이미 존재.")
            return
        }

        viewModelScope.launch {
            try {
                val response = codiDiaryService.getCategories("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _categoryList.value = response.body()!!.data.categories
                    Log.d("CodiDiaryVM", "카테고리 로드 성공: ${_categoryList.value}")
                } else if (response.code() == 404) {
                    Log.e("CodiDiaryVM", "카테고리 데이터 없음 (seed 필요)")
                }
            } catch (e: Exception) {
                Log.e("CodiDiaryVM", "에러: ${e.message}")
            }
        }
    }


    /**
     * 카테고리에 해당하는 옷 목록 조회 API 호출
     *
     * @param token 토큰
     * @param categoryId 카테고리
     */
    fun fetchClothes(token: String, categoryId: Int, forceRefresh: Boolean = false) {
        if (clothesCache.containsKey(categoryId)) {
            val cachedData = clothesCache[categoryId]

            if (!cachedData.isNullOrEmpty()) {
                _clothesList.value = cachedData!!
                Log.d("CodiDiaryVM", "캐시 사용 중 (카테고리 ID: $categoryId)")
                return
            }
        }

        viewModelScope.launch {
            try {
                val response = codiDiaryService.getClothesByCategory("Bearer $token", categoryId)
                if (response.isSuccessful && response.body() != null) {
                    val clothes = response.body()!!.data.clothes

                    clothesCache[categoryId] = clothes
                    _clothesList.value = clothes
                    Log.d("CodiDiaryVM", "옷 목록 로드 성공: ${response.body()!!.data.clothes.size}개")
                }
            } catch (e: Exception) {
                Log.e("CodiDiaryVM", "에러: ${e.message}")
            }
        }
    }

    fun clearClothesCache() {
        clothesCache.clear()
    }
}