package com.example.bluetoothpasswordmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso





class PasswordAdapter(private val context: Context,
                      private val dataSource: ArrayList<Password>) : BaseAdapter() {

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
            view = inflater.inflate(R.layout.list_item_password, parent, false)
            holder = ViewHolder()
            holder.hostTextView = view.findViewById(R.id.password_list_host) as TextView
            holder.urlTextView = view.findViewById(R.id.password_list_url) as TextView
            holder.favicoImageView = view.findViewById(R.id.password_list_favico) as ImageView

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val hostTextView = holder.hostTextView
        val urlTextView = holder.urlTextView
        val faviconImageView = holder.favicoImageView;

        val password = getItem(position) as Password

        hostTextView.text = password.host
        urlTextView.text = password.url

        val faviconUrl = "https://www." + password.host + "/favicon.ico"

        Picasso.get()
            .load(faviconUrl)
            .into(faviconImageView)

        val hostTypeFace = ResourcesCompat.getFont(context, R.font.josefinsans_bold)
        hostTextView.typeface = hostTypeFace

        val urlTypeFace = ResourcesCompat.getFont(context, R.font.josefinsans_semibolditalic)
        urlTextView.typeface = urlTypeFace


        return view
    }

    private class ViewHolder {
        lateinit var hostTextView: TextView
        lateinit var urlTextView: TextView
        lateinit var favicoImageView: ImageView
    }
}
