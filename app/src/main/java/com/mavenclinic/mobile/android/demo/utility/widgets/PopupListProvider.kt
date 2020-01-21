package com.mavenclinic.mobile.android.demo.utility.widgets

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.annotation.ArrayRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mavenclinic.mobile.android.demo.R
import com.mavenclinic.mobile.demo.common.utility.cache.SuspendingCache

/**
 * A provider that creates a simple popup (dropdown) choice list where the data comes from a resource
 * string array.
 *
 * Created by BillH on 5/14/2019
 */
open class PopupListProvider<T> (
        context: Context,
        private val listItems: List<T>,
        private val selectedHandler: (value: T)->Unit,
        private val dropDownWidth: Int,
        private val contentFormatter: (v: T?)->CharSequence? = { it.toString() }
) {
    companion object {
        fun createItems(
                context: Context,
                @ArrayRes valuesArrayId: Int,
                @ArrayRes labelsArrayId: Int
        ): List<Pair<String,String>> {
            return context.resources.getStringArray(valuesArrayId).toList().zip(
                    context.resources.getStringArray(labelsArrayId).toList())
        }
    }

    private val popupCache = SuspendingCache {
        ListPopupWindow(context).apply{
            height = if (listItems.size <= context.resources.getInteger(R.integer.list_picker_max_elements))
                ListPopupWindow.WRAP_CONTENT
            else
                context.resources.getDimensionPixelSize(R.dimen.list_picker_height)

            promptPosition = ListPopupWindow.POSITION_PROMPT_BELOW
            setContentWidth(dropDownWidth)
//        horizontalOffset = -getDimen(R.dimen.picker_horizontal_offset)
            isModal = true
            setAdapter( createAdapter(context, listItems) )
            setOnItemClickListener { _, _, position, _ ->
                val item = listItems[position]
                selectedHandler( item )
                dismiss()
            }
        }
    }


    //-- API --

    /**
     * Shows the popup, using the supplied view as the anchor.
     */
    fun show(anchor: View, dropDownGravity: Int = Gravity.END, offset: Int = 0 ) {

        (anchor.context as? LifecycleOwner)?.lifecycleScope?.launchWhenCreated {
            popupCache.get().apply {
                anchorView = anchor
                setDropDownGravity(dropDownGravity)
                horizontalOffset = offset
            }.show()
        }


    }

    /**
     * A convenience method that returns the label for a specified value.
     */
//    fun getLabelForValue(value: String): String? {
//        return listItems.firstOrNull { it.first == value }?.second
//    }


    private fun createAdapter(context: Context, listItems: List<T>): ArrayAdapter<CharSequence> {

        return object: ArrayAdapter<CharSequence>(context, R.layout.item_list_chooser){
            init {
                addAll( listItems.map { contentFormatter(it) } )
            }
        }
    }
}

class PopupStringArrayListProvider(
        context: Context,
        @ArrayRes valuesArrayId: Int,
        @ArrayRes labelsArrayId: Int,
        selectedHandler: (value: String, label: String)->Unit,
        dropDownWidth: Int,
        contentFormatter: (v: String?, l: String?)->CharSequence? = {_,l->l }
) : PopupListProvider<Pair<String,String>>(
        context = context,
        selectedHandler = { selectedHandler(it.first, it.second)},
        dropDownWidth = dropDownWidth,
        contentFormatter = { contentFormatter(it?.first, it?.second)},
        listItems =  createItems(context, valuesArrayId, labelsArrayId )
)

