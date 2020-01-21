package com.mavenclinic.mobile.android.demo.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.mavenclinic.mobile.android.demo.R
import com.mavenclinic.mobile.android.demo.utility.widgets.PopupListProvider
import com.mavenclinic.mobile.demo.common.di.ServiceLocator
import com.mavenclinic.mobile.demo.common.domain.model.State
import com.mavenclinic.mobile.demo.common.utility.cache.SuspendingCache
import kotlinx.android.synthetic.main.main_fragment.view.*
import timber.log.Timber

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val metadataRepo by lazy { ServiceLocator.metadataRepo }
    private val statePopupProvider = SuspendingCache {
        PopupListProvider<State>(
            context = context!!,
            listItems = metadataRepo.getUsStates().values.toList(),
            dropDownWidth = 400,
            contentFormatter = { state -> "${state?.displayName?:""} (${state?.abbreviation?:""})"},
            selectedHandler = { state -> Timber.i("State selected: $state")}
        )
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false).apply {
            statesButton.setOnClickListener { v ->
                lifecycleScope.launchWhenStarted {
                    statePopupProvider.get().show(v)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
