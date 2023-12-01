package com.example.project1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
import com.example.project1.databinding.FragmentCalenderBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalenderFragment : Fragment() {
    private var mBinding: FragmentCalenderBinding? = null
    private val binding get() = mBinding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCalenderBinding.inflate(inflater, container, false)
        val username = arguments?.getString("username")
        //화면 설정
        setMonthView()
        //이전달 버튼 이벤트
        binding.preBtn.setOnClickListener {
            //현재 월 -1 변수에 담기
            CalendarUtil.selectedDate.add(Calendar.MONTH, -1)// 현재 달 -1
            setMonthView()
        }

        //다음달 버튼 이벤트
        binding.nextBtn.setOnClickListener {
            CalendarUtil.selectedDate.add(Calendar.MONTH, 1) //현재 달 +1
            setMonthView()
        }

        //편집 버튼 이벤트
        binding.editBtn.setOnClickListener{
            val selectedDate = CalendarUtil.selectedDate.time
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            activity?.let{
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra("username",username)
                intent.putExtra("selectDate",formattedDate)
                startActivity(intent)
            }
        }
        return binding.root
    }
    //날짜 화면에 보여주기
    private fun setMonthView() {
        //년월 텍스트뷰 셋팅
        binding.monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)

        //날짜 생성해서 리스트에 담기
        val dayList = dayInMonthArray()

        //어댑터 초기화
        val adapter = CalendarAdapter(dayList)

        //레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)

        //레이아웃 적용
        binding.recyclerView.layoutManager = manager

        //어뎁터 적용
        binding.recyclerView.adapter = adapter
    }

    //날짜 타입 설정(월, 년)
    private fun monthYearFromDate(calendar: Calendar): String {

        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1

        return "$month 월 $year"
    }


    //날짜 생성
    private fun dayInMonthArray(): ArrayList<Date>{

        var dayList = ArrayList<Date>()

        var monthCalendar = CalendarUtil.selectedDate.clone() as Calendar

        //1일로 셋팅
        monthCalendar[Calendar.DAY_OF_MONTH] = 1

        //해당 달의 1일의 요일[1:일요일, 2: 월요일.... 7일: 토요일]
        val firstDayOfMonth = monthCalendar[Calendar.DAY_OF_WEEK]-1

        //요일 숫자만큼 이전 날짜로 설정
        //예: 6월1일이 수요일이면 3만큼 이전날짜 셋팅
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while(dayList.size < 42){

            dayList.add(monthCalendar.time)

            //1일씩 늘린다. 1일 -> 2일 -> 3일
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dayList
    }
}