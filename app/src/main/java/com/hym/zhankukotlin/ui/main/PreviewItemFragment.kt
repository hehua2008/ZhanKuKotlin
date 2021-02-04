package com.hym.zhankukotlin.ui.main

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.FragmentMainBinding
import com.hym.zhankukotlin.network.CatagoryItem
import com.hym.zhankukotlin.network.Order
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreviewItemFragment : Fragment(), Observer<LifecycleOwner> {
    private lateinit var mPageViewModel: PageViewModel
    private var mBinding: FragmentMainBinding? = null
    private lateinit var mCatagoryItem: CatagoryItem

    //private lateinit var mPreviewItemAdapter: PreviewItemAdapter
    private lateinit var mPagingPreviewItemAdapter: PagingPreviewItemAdapter
    private lateinit var mCatagoryItemLayoutManager: FlexboxLayoutManager
    private lateinit var mCatagoryItemAdapter: CatagoryItemAdapter
    private lateinit var mButtonItemDecoration: ItemDecoration
    private lateinit var mPreviewItemDecoration: ItemDecoration
    private lateinit var mOnScrollListener: RecyclerView.OnScrollListener

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CATAGORY_ITEM, mCatagoryItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mCatagoryItem = activeBundle!!.getParcelable(CATAGORY_ITEM)!!

        mPageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)
        //mPreviewItemAdapter = PreviewItemAdapter()
        mPagingPreviewItemAdapter = PagingPreviewItemAdapter()
        mCatagoryItemLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        mCatagoryItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        mCatagoryItemAdapter = CatagoryItemAdapter(mPageViewModel)

        mButtonItemDecoration = object : ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(
                R.dimen.button_item_horizontal_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val itemCount = state.itemCount
                val left = if (itemPosition == 0) mOffset else mHalfOffset
                val right = if (itemPosition == itemCount - 1) mOffset else mHalfOffset
                outRect.set(left, 0, right, 0)
            }
        }
        mPreviewItemDecoration = object : ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(
                R.dimen.preview_item_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (itemPosition and 1 == 0) {
                    outRect.set(mOffset, 0, mHalfOffset, mOffset)
                } else {
                    outRect.set(mHalfOffset, 0, mOffset, mOffset)
                }
            }
        }
        mOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    MyApplication.imageLoader.pause()
                } else {
                    MyApplication.imageLoader.resume()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val binding = mBinding!!

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        binding.swipeRefresh.setOnRefreshListener { mPagingPreviewItemAdapter.refresh() }

        binding.previewRecycler.addItemDecoration(mPreviewItemDecoration)
        binding.previewRecycler.adapter = mPagingPreviewItemAdapter
        //binding.previewRecycler.addOnScrollListener(mOnScrollListener)

        binding.catRecrcler.layoutManager = mCatagoryItemLayoutManager
        binding.catRecrcler.addItemDecoration(mButtonItemDecoration)
        binding.catRecrcler.adapter = mCatagoryItemAdapter

        binding.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            val order: Order = when (checkedId) {
                R.id.order_all_recommend -> Order.ALL_RECOMMEND
                R.id.order_home_recommend -> Order.HOME_RECOMMEND
                R.id.order_latest_publish -> Order.LATEST_PUBLISH
                R.id.order_editor_choice -> Order.EDITOR_CHOICE
                else -> Order.EDITOR_CHOICE
            }
            mPageViewModel.setOrder(order)
        }

        binding.pagedLayout.prePage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val previewResult = mPageViewModel.previewResult.value ?: return@setOnClickListener
            mPageViewModel.setPage(previewResult.pagedArr[0] - 1)
        }
        binding.pagedLayout.nextPage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val previewResult = mPageViewModel.previewResult.value ?: return@setOnClickListener
            mPageViewModel.setPage(previewResult.pagedArr[0] + 1)
        }
        binding.pagedLayout.jumpButton.setOnClickListener(View.OnClickListener {
            clearEditFocusAndHideSoftInput()
            val numberEdit = binding.pagedLayout.numberEdit.text.toString()
            if (numberEdit.isEmpty()) {
                return@OnClickListener
            }
            val previewResult = mPageViewModel.previewResult.value ?: return@OnClickListener
            val pagedArr = previewResult.pagedArr
            val page = numberEdit.toInt()
            if (page >= 1 && page <= pagedArr[1] && page != pagedArr[0]) {
                mPageViewModel.setPage(page)
            }
        })

        return binding.root
    }

    private fun clearEditFocusAndHideSoftInput() {
        mBinding!!.pagedLayout.numberEdit.clearFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        mPageViewModel.setUrl(mCatagoryItem.url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding!!.catRecrcler.layoutManager = null
        mBinding = null
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner) {
        val binding = mBinding!!
        mPageViewModel.previewResult.observe(viewLifecycleOwner, Observer { previewResult ->
            previewResult ?: return@Observer
            //mPreviewItemAdapter.setPreviewItems(previewResult)
            mCatagoryItemAdapter.setTitleSubcatMap(previewResult.catagoryItem)
            binding.pagedLayout.pageArr = previewResult.pagedArr
        })
        mPageViewModel.previewUrl.observe(viewLifecycleOwner, Observer { url ->
            binding.catagoryLink.text = url
        })
        mPageViewModel.pagingFlow.observe(viewLifecycleOwner, Observer {
            viewLifecycleOwner.lifecycleScope.launch {
                mPagingPreviewItemAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                mPageViewModel.pagingFlow.value?.collectLatest { pagingData ->
                    mPagingPreviewItemAdapter.submitData(pagingData)
                }
            }
        })
    }

    companion object {
        val TAG = PreviewItemFragment::class.java.simpleName
        const val CATAGORY_ITEM = "CATAGORY_ITEM"

        @JvmStatic
        fun newInstance(item: CatagoryItem): PreviewItemFragment {
            val fragment = PreviewItemFragment()
            val bundle = Bundle()
            bundle.putParcelable(CATAGORY_ITEM, item)
            fragment.arguments = bundle
            return fragment
        }
    }
}