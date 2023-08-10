package msp.gruppe3.wgmanager.ui.home.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.FragmentHomeBinding
import msp.gruppe3.wgmanager.enums.Features
import msp.gruppe3.wgmanager.models.Wg


private const val TAG = "WG Home Fragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val relativeLayout: CoordinatorLayout = binding.homeGrid
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]


        // disable back button for disabling leaving the app via back
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    return
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        Log.d(TAG, "Home Fragment init. mainViewModel.wgCurrent.value:  ${mainViewModel.wgCurrent.value}")

        if (mainViewModel.wgCurrent.value != null) {
            // No observer needed, just get the current wg if any
            initFeatures(mainViewModel.wgCurrent.value!!)
        } else {
            val wgObserver = Observer<Wg> { wg ->
                Log.d(TAG, "wgObserver: $wg")
                if (wg != null) {
                    initFeatures(wg)
                } else {
                    findNavController().navigate(R.id.wgRootFragment)
                }
            }
            mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)
        }

        initButtons()
        Log.e(TAG, "Home Fragment Loaded")

        return relativeLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFeatures(wg: Wg) {

        var dip = 150f
        val r: Resources = resources
        val pxHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        dip = 10f
        val pxMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        binding.homeButtonCalendar.visibility = View.INVISIBLE
        binding.homeButtonFinance.visibility = View.INVISIBLE
        binding.homeButtonShoppingList.visibility = View.INVISIBLE
        val wgFeatureNames = ArrayList<Features>()
        wg.features.map { feature ->
            wgFeatureNames.add(feature.name)
        }

        if (wgFeatureNames.contains(Features.FINANCE)){
            binding.homeButtonFinance.visibility = View.VISIBLE
        }
        if (wgFeatureNames.contains(Features.SHOPPING_LIST)){
            if (binding.homeButtonFinance.visibility == View.VISIBLE) {

                val param:RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pxHeight.toInt())
                param.setMargins(pxMargin.toInt(),0,pxMargin.toInt(),0)
                param.addRule(RelativeLayout.BELOW, R.id.home_button_finance)
                binding.homeButtonShoppingList.layoutParams = param
            }
            binding.homeButtonShoppingList.visibility = View.VISIBLE
        }
        if (wgFeatureNames.contains(Features.CALENDAR)){
            if (binding.homeButtonShoppingList.visibility == View.VISIBLE) {
                val param:RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  pxHeight.toInt())
                param.setMargins(pxMargin.toInt(),0,pxMargin.toInt(),0)
                param.addRule(RelativeLayout.BELOW, R.id.home_button_shopping_list)
                binding.homeButtonCalendar.layoutParams = param
            }else{
                if (binding.homeButtonFinance.visibility == View.VISIBLE){
                    val param:RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  pxHeight.toInt())
                    param.setMargins(pxMargin.toInt(),0,pxMargin.toInt(),0)
                    param.addRule(RelativeLayout.BELOW, R.id.home_button_finance)
                    binding.homeButtonCalendar.layoutParams = param
                }
            }
            binding.homeButtonCalendar.visibility = View.VISIBLE
        }
    }

    fun initButtons(){
        binding.homeButtonCalendar.setOnClickListener{
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            val wgOberver = Observer<Wg> { wg ->
                wg.features.map { feature ->
                    if (feature.name == Features.CALENDAR) {
                        val navHostFragment = it.findNavController()
                        navHostFragment.navigate(feature.name.layoutResource)
                    }
                }
            }
            mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgOberver)
        }

        binding.homeButtonFinance.setOnClickListener {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            val wgOberver = Observer<Wg> { wg ->
                wg.features.map { feature ->
                    if (feature.name == Features.FINANCE) {
                        val navHostFragment = it.findNavController()
                        navHostFragment.navigate(feature.name.layoutResource)
                    }
                }
            }
            mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgOberver)
        }

        binding.homeButtonShoppingList.setOnClickListener {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            val wgOberver = Observer<Wg> { wg ->
                wg.features.map { feature ->
                    if (feature.name == Features.SHOPPING_LIST) {
                        val navHostFragment = it.findNavController()
                        navHostFragment.navigate(feature.name.layoutResource)
                    }
                }
            }
            mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgOberver)        }
    }

    fun replaceFragmentWithBackStack(fragment: Fragment, name: String, tag: String) {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .add(
                R.id.homeGrid,
                fragment,
                tag
            )
            .addToBackStack(name)
            .commit()
    }

}