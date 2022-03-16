package com.sample.hmssample.safetydetectdemo.ui.main

import android.content.pm.PackageManager
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.hmssample.safetydetectdemo.R
import com.sample.hmssample.safetydetectdemo.databinding.TextRowItemBinding

class MainAdapter(
    private val packageManager: PackageManager,
    private val mainDataModelArray: Array<MainDataModel>
) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(val binding: TextRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.text_row_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.imageViewIcon.setImageDrawable(mainDataModelArray[position].packageInfo.applicationInfo.loadIcon(packageManager))
        viewHolder.binding.textViewAppName.text = packageManager.getApplicationLabel(mainDataModelArray[position].packageInfo.applicationInfo)
        viewHolder.binding.textViewPackageName.text = mainDataModelArray[position].packageInfo.applicationInfo.packageName

        if (mainDataModelArray[position].isMaliciousApp) {
            viewHolder.binding.textViewResult.text = "Is malicious app"
            viewHolder.binding.textViewResult.setTextColor(Color.RED)
        } else {
            viewHolder.binding.textViewResult.text = "Pass"
        }
    }

    override fun getItemCount() = mainDataModelArray.size
}