package com.capgemini.demo.weatherapp.home.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.capgemini.demo.weatherapp.R
import com.capgemini.demo.weatherapp.datamodel.Result
import com.capgemini.demo.weatherapp.utils.NotificationHelper

class AutoSuggestAdapter(context: Context, @LayoutRes private val layoutResource: Int) :
    ArrayAdapter<Result>(context, layoutResource),
    Filterable {

    private var citiesData: MutableList<Result>? = ArrayList()

    fun setData(list: List<Result>?) {
        citiesData!!.clear()
        citiesData!!.addAll(list!!)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
            .inflate(layoutResource, parent, false) as TextView
        view.text = citiesData?.get(position)?.let { prepareDropdownText(it) }
        return view
    }

    override fun getCount(): Int {
        return if (citiesData != null && citiesData!!.size > 0) {
            citiesData!!.size
        } else 0
    }

    override fun getItem(position: Int): Result {
        return citiesData!![position]
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    fun getObject(position: Int): Result {
        return citiesData!![position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = citiesData
                    filterResults.count = citiesData!!.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else if (constraint != null && constraint.length > 3) {
                    showNoDataSnackBarMessage()
                    notifyDataSetInvalidated()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    private fun prepareDropdownText(result: Result): String {
        return result.areaName[0].value + ", " + result.country[0].value
    }

    private fun showNoDataSnackBarMessage() {
        try {
            val rootView =
                (context as Activity?)!!.window.decorView.findViewById<View>(R.id.content)
            NotificationHelper().setSnackBar(
                rootView,
                context.getString(R.string.search_no_result_found)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}