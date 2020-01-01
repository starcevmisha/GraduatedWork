package com.example.bluetoothpasswordmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat





class BtDeviceAdapter(private val context: Context,
                      private val dataSource: ArrayList<BtDevice>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_btdevice, parent, false)
            holder = ViewHolder()
            holder.nameTextView = view.findViewById(R.id.btdevice_list_name) as TextView
            holder.addressTextView = view.findViewById(R.id.btdevice_list_address) as TextView

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val nameTextView = holder.nameTextView
        val addressTextView = holder.addressTextView

        val btDevice = getItem(position) as BtDevice

        nameTextView.text = btDevice.name
        addressTextView.text = btDevice.address

        val hostTypeFace = ResourcesCompat.getFont(context, R.font.josefinsans_bold)
        nameTextView.typeface = hostTypeFace

        val urlTypeFace = ResourcesCompat.getFont(context, R.font.josefinsans_semibolditalic)
        addressTextView.typeface = urlTypeFace

        return view
    }

    private class ViewHolder {
        lateinit var nameTextView: TextView
        lateinit var addressTextView: TextView
    }
}
