package com.retrytech.ledgeapp.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.BottomsheetWebBinding
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.SessionManager

class WebBottomSheet(var type: Int) : BottomSheetDialogFragment() {
    lateinit var binding: BottomsheetWebBinding
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var v: View? = null
    var sessionManager: SessionManager? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog1: DialogInterface ->
            val dialog = dialog1 as BottomSheetDialog
            dialog.setCanceledOnTouchOutside(false)
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
                BottomSheetBehavior.from(bottomSheet).isHideable = true
            }
        }
        return bottomSheetDialog
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_web, container, false)
        sessionManager = SessionManager(requireActivity())
        binding = DataBindingUtil.bind(v!!)!!
        binding.root.isNestedScrollingEnabled = true
        binding.webView.settings.javaScriptEnabled = true
        if (type == 1) {
            binding.tvHeading.text = requireActivity().getString(R.string.privacy_policy)
            binding.webView.loadUrl(Const.PRIVACY_URL)
        }
        if (type == 2) {
            binding.tvHeading.text = requireActivity().getString(R.string.terms_of_use)
            binding.webView.loadUrl(Const.TERMS_URL)
        }
        binding.btnClose.setOnClickListener { dismiss() }
        return binding.root
    }

}