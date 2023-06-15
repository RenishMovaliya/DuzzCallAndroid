package com.logycraft.duzzcalll

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.logycraft.duzzcalll.fragment.EditPhoneFragment
import com.logycraft.duzzcalll.fragment.PortfolioFragment
import com.logycraft.duzzcalll.fragment.TearmsAndConditionFragment
import com.logycraft.duzzcalll.fragment.VerifyPhoneFragment

class SignupViewPager (fm:FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return TearmsAndConditionFragment()
            }
            1 -> {
                return EditPhoneFragment()
            }
            2 -> {
                return VerifyPhoneFragment()
            }
            3 -> {
                return PortfolioFragment()
            }
            else -> {
                return TearmsAndConditionFragment()
            }
        }
    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        when(position) {
//            0 -> {
//                return "Tab 1"
//            }
//            1 -> {
//                return "Tab 2"
//            }
//            2 -> {
//                return "Tab 3"
//            }
//        }
//        return super.getPageTitle(position)
//    }

}