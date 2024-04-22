package com.app.imageloader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.imageloader.R
import com.app.imageloader.data.dataclasses.ContentResponse
import com.app.imageloader.databinding.FragmentHomeBinding
import com.app.imageloader.imageloading.ImageLoader
import com.app.imageloader.ui.adapter.ImageAdapter
import com.app.imageloader.utils.ApiResult
import com.app.imageloader.utils.GridSpaceItemDecoration
import com.app.imageloader.utils.NetworkUtils
import com.app.imageloader.utils.UrlBuilder
import com.app.imageloader.viewmodel.ContentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val contentViewModel: ContentViewModel by viewModels()
    private var imageAdapter: ImageAdapter? = null

    @Inject
    lateinit var imageLoader: ImageLoader
    private var visibleItemPositions: MutableSet<Int> = mutableSetOf()
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgressbar()
        hideMessage()
        setRecyclerView()
        observeContent()
        getContent()
    }

    private fun setRecyclerView() {
        imageAdapter = ImageAdapter(imageLoader)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeEdge = false
        val spanCount = 3
        binding.rvImages.run {
            addItemDecoration(GridSpaceItemDecoration(spanCount, spacing, includeEdge))
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = imageAdapter
        }

        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isScrolling) {
                    isScrolling = true
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    visibleItemPositions.clear()
                    for (i in firstVisiblePosition..lastVisiblePosition) {
                        visibleItemPositions.add(i)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (isScrolling && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    val itemsOffScreen = mutableSetOf<Int>()
                    for (position in visibleItemPositions) {
                        if (position < firstVisiblePosition || position > lastVisiblePosition) {
                            itemsOffScreen.add(position)
                        }
                    }

                    val data = imageAdapter?.getList()
                    data?.let { list ->
                        for (i in itemsOffScreen) {
                            if (isValidIndex(i, list)) {
                                val contentResponse = list[i]
                                contentResponse.thumbnail?.let {
                                    imageLoader.cancelDownloading(
                                        UrlBuilder.prepareImageUrl(
                                            it.domain,
                                            it.basePath,
                                            "0",
                                            it.key
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun isValidIndex(i: Int, contentResponses: List<ContentResponse>): Boolean {
        return i >= 0 && i < contentResponses.size
    }

    private fun observeContent() {
        contentViewModel.contentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Loading -> {
                    if (it.isLoading) {
                        showProgressbar()
                    } else {
                        hideProgressbar()
                    }
                }

                is ApiResult.Success -> {
                    it.data?.let { it1 ->
                        if (it1.isEmpty())
                            showMessage(getString(R.string.data_is_not_available))
                        else {
                            imageAdapter?.submitList(it1)
                            hideMessage()
                        }
                    } ?: showMessage(getString(R.string.data_is_not_available))
                }

                is ApiResult.Error -> {
                    if (NetworkUtils.isNetworkAvailable(requireContext()))
                        showMessage(getString(R.string.server_error))
                    else showMessage(getString(R.string.internet_is_not_available_please_check))
                }
            }
        }
    }

    private fun getContent() {
        contentViewModel.getContent(100)
    }

    private fun showProgressbar() {
        binding.pbLoading.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        binding.pbLoading.visibility = View.GONE
    }

    private fun hideMessage() {
        binding.tvMessage.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        binding.tvMessage.visibility = View.VISIBLE
        binding.tvMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}