package com.abbasov.currency2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abbasov.currency2.databinding.ItemRvBinding
import com.abbasov.currency2.models.User



class RvAdapter(val list: List<User>) : RecyclerView.Adapter<RvAdapter.Vh>() {

    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(user: User, position: Int) {
            itemRv.tv.text = user.Ccy
            itemRv.usd.text=user.Rate
/*
            Glide.with(itemRv.root).load(user.avatar_url).into(itemRv.images)
*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

}