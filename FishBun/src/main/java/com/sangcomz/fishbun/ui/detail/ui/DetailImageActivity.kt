package com.sangcomz.fishbun.ui.detail.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.snackbar.Snackbar
import com.sangcomz.fishbun.BaseActivity
import com.sangcomz.fishbun.R
import com.sangcomz.fishbun.datasource.FishBunDataSourceImpl
import com.sangcomz.fishbun.ui.detail.DetailImageContract
import com.sangcomz.fishbun.ui.detail.adapter.DetailViewPagerAdapter
import com.sangcomz.fishbun.ui.detail.model.DetailImageRepositoryImpl
import com.sangcomz.fishbun.ui.detail.mvp.DetailImagePresenter
import com.sangcomz.fishbun.util.RadioWithTextButton
import com.sangcomz.fishbun.util.setStatusBarColor

class DetailImageActivity : BaseActivity(), DetailImageContract.View, OnPageChangeListener {

    private val presenter: DetailImageContract.Presenter by lazy {
        DetailImagePresenter(
            this,
            DetailImageRepositoryImpl(FishBunDataSourceImpl(fishton))
        )
    }
    private var initPosition = -1

    private var btnDetailCount: RadioWithTextButton? = null
    private var vpDetailPager: ViewPager? = null
    private var btnDetailBack: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        setContentView(R.layout.activity_detail_activity)
        initValue()
        initView()
        initPager()
        presenter.getDesignViewData()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    override fun updateRadioButtonWithText(text: String) {
        btnDetailCount?.setText(text)
    }

    override fun updateRadioButtonWithDrawable() {
        val radioButton = btnDetailCount ?: return
        ContextCompat.getDrawable(this, R.drawable.ic_done_white_24dp)?.let {
            radioButton.setDrawable(it)
        }
    }

    override fun showSnackbar(message: String) {
        btnDetailCount?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun unselectImage() {
        btnDetailCount?.unselect()
    }

    override fun onPageSelected(position: Int) {
        presenter.changeButtonStatus(position)
    }

    override fun finishActivity() {
        val i = Intent()
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun setToolBar(colorStatusBar: Int, isStatusBarLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setStatusBarColor(colorStatusBar)
        }
        if (isStatusBarLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vpDetailPager?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun setCountButton(
        colorActionBar: Int,
        colorActionBarTitle: Int,
        colorSelectCircleStroke: Int
    ) {
        btnDetailCount?.run {
            unselect()
            setCircleColor(colorActionBar)
            setTextColor(colorActionBarTitle)
            setStrokeColor(colorSelectCircleStroke)
            setOnClickListener {
                val currentPosition = vpDetailPager?.currentItem ?: return@setOnClickListener
                presenter.onCountClick(currentPosition)
            }
        }
    }

    override fun setBackButton() {
        btnDetailBack?.setOnClickListener {
            finishActivity()
        }
    }

    override fun showImages(initPosition: Int, pickerImages: List<Uri>) {
        vpDetailPager?.run {
            (adapter as? DetailViewPagerAdapter)?.setImages(pickerImages)
            currentItem = initPosition
        }
        (vpDetailPager?.adapter as? DetailViewPagerAdapter)?.setImages(pickerImages)

    }

    override fun finishAndShowErrorToast() {
        Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    private fun initPager() {
        vpDetailPager?.run {
            adapter = DetailViewPagerAdapter()
            addOnPageChangeListener(this@DetailImageActivity)
        }

        presenter.setInitPagerPosition(initPosition)
    }

    private fun initView() {
        vpDetailPager = findViewById(R.id.vp_detail_pager)
        btnDetailCount = findViewById(R.id.btn_detail_count)
        btnDetailBack = findViewById(R.id.btn_detail_back)

    }

    private fun initValue() {
        initPosition = intent.getIntExtra(INIT_IMAGE_POSITION, -1)
    }

    companion object {

        private const val INIT_IMAGE_POSITION = "init_image_position"

        fun getDetailImageActivity(context: Context, initPosition: Int): Intent =
            Intent(context, DetailImageActivity::class.java).apply {
                putExtra(INIT_IMAGE_POSITION, initPosition)
            }
    }
}