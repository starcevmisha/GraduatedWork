package com.example.bluetoothpasswordmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso


class PasswordAdapter(
    private val context: Context,
    private val passwordsList: ArrayList<Password>
) : BaseAdapter(), Filterable {

    private var filteredPasswordsList: ArrayList<Password> = ArrayList()
    init {
        filteredPasswordsList.addAll(passwordsList)
    }

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return filteredPasswordsList.size
    }

    override fun getItem(position: Int): Any {
        return filteredPasswordsList[position]
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
            holder.usernameTextView = view.findViewById(R.id.password_list_username) as TextView
            holder.favicoImageView = view.findViewById(R.id.password_list_favico) as ImageView

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val hostTextView = holder.hostTextView
        val urlTextView = holder.usernameTextView
        val faviconImageView = holder.favicoImageView;

        val password = getItem(position) as Password

        hostTextView.text = password.host
        urlTextView.text = password.username

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                if (constraint == null || constraint.isEmpty()) {
                    results.values = passwordsList
                } else {
                    results.values =
                        passwordsList.filter { it.host.contains(constraint.trim(), ignoreCase = true) }
                }

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredPasswordsList.clear()
                filteredPasswordsList.addAll(results.values as List<Password>)
                notifyDataSetChanged()
            }
        }
    }

    private class ViewHolder {
        lateinit var hostTextView: TextView
        lateinit var usernameTextView: TextView
        lateinit var favicoImageView: ImageView
    }
}

